package net.wbz.moba.controlcenter.web.server.web.constrution;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;
import net.wbz.moba.controlcenter.web.server.EventBroadcaster;
import net.wbz.moba.controlcenter.web.server.persist.device.DeviceInfoDao;
import net.wbz.moba.controlcenter.web.server.persist.device.DeviceInfoEntity;
import net.wbz.moba.controlcenter.web.server.web.DtoMapper;
import net.wbz.moba.controlcenter.web.shared.bus.*;
import net.wbz.moba.controlcenter.web.shared.viewer.RailVoltageEvent;
import net.wbz.selectrix4java.bus.consumption.AllBusDataConsumer;
import net.wbz.selectrix4java.data.recording.BusDataPlayer;
import net.wbz.selectrix4java.data.recording.BusDataPlayerListener;
import net.wbz.selectrix4java.device.Device;
import net.wbz.selectrix4java.device.DeviceAccessException;
import net.wbz.selectrix4java.device.DeviceConnectionListener;
import net.wbz.selectrix4java.device.DeviceManager;
import net.wbz.selectrix4java.device.serial.SerialDevice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

/**
 * TODO die stored device sind bullshit... is ja nur für mapping von device info zu selectrix device
 *
 * @author Daniel Tuerk
 */
@Singleton
public class BusServiceImpl extends RemoteServiceServlet implements BusService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BusServiceImpl.class);
    private final DeviceManager deviceManager;
    private final EventBroadcaster eventBroadcaster;
    private final AllBusDataConsumer allBusDataConsumer;
    private final DeviceRecorder deviceRecorder;
    private net.wbz.selectrix4java.device.Device activeDevice;
    private boolean trackingActive = false;
    private BusDataPlayer busDataPlayer;
    private final Map<DeviceInfo, Device> storedDevices = Maps.newHashMap();
    private final DeviceInfoDao deviceInfoDao;
    private final DtoMapper<DeviceInfo, DeviceInfoEntity> dtoMapper;

    @Inject
    public BusServiceImpl(DeviceManager deviceManager, final EventBroadcaster eventBroadcaster,
                          DeviceRecorder deviceRecorder, DeviceInfoDao deviceInfoDao) {
        this.deviceManager = deviceManager;
        this.eventBroadcaster = eventBroadcaster;
        this.deviceRecorder = deviceRecorder;
        this.deviceInfoDao = deviceInfoDao;

        this.dtoMapper = new DtoMapper<>();

        this.allBusDataConsumer = new AllBusDataConsumer() {
            @Override
            public void valueChanged(int bus, int address, int oldValue, int newValue) {
                eventBroadcaster.fireEvent(new BusDataEvent(bus, address, newValue));
            }
        };

        initFoooo();
    }

    private void initFoooo() {

        for (DeviceInfoEntity deviceInfo : deviceInfoDao.listAll()) {
            // TODO - values from config
            registerDevice(deviceInfo);
        }

        deviceManager.addDeviceConnectionListener(new DeviceConnectionListener() {
            @Override
            public void connected(Device device) {
                BusServiceImpl.this.eventBroadcaster.fireEvent(new DeviceInfoEvent(getDeviceInfo(device),
                        DeviceInfoEvent.TYPE.CONNECTED));
                // receive actual state of rail voltage -> no consumer available for addresses > 112
                try {
                    eventBroadcaster.fireEvent(new RailVoltageEvent(device.getRailVoltage()));
                } catch (DeviceAccessException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void disconnected(Device device) {
                BusServiceImpl.this.eventBroadcaster.fireEvent(new DeviceInfoEvent(getDeviceInfo(device),
                        DeviceInfoEvent.TYPE.DISCONNECTED));
                device.getBusDataDispatcher().unregisterConsumer(allBusDataConsumer);
            }
        });
    }

    private void registerDevice(DeviceInfoEntity deviceInfo) {
        storedDevices.put(dtoMapper.transform(deviceInfo), deviceManager.registerDevice(DeviceManager.DEVICE_TYPE.valueOf(
                deviceInfo.getType().name()), deviceInfo.getKey(), SerialDevice.DEFAULT_BAUD_RATE_FCC));
    }

    public void changeDevice(DeviceInfoEntity deviceInfo) {
        activeDevice = deviceManager.getDeviceById(deviceInfo.getKey());
    }

    @Transactional
    public void createDevice(DeviceInfo deviceInfo) {
        // TODO - device settings (e.g. serial/test)
        DeviceInfoEntity entity = new DeviceInfoEntity();
        entity.setKey(deviceInfo.getKey());
        entity.setType(DeviceInfoEntity.DEVICE_TYPE.valueOf(deviceInfo.getType().name()));
        deviceInfoDao.create(entity);

        registerDevice(entity);

        eventBroadcaster.fireEvent(new DeviceInfoEvent(deviceInfo, DeviceInfoEvent.TYPE.CREATE));
    }

    @Transactional
    public void deleteDevice(DeviceInfo deviceInfo) {
        Device device = deviceManager.getDeviceById(deviceInfo.getKey());

        deviceInfoDao.delete(deviceInfoDao.getById(deviceInfo.getId()));

        deviceManager.removeDevice(device);

        storedDevices.remove(deviceInfo);
        eventBroadcaster.fireEvent(new DeviceInfoEvent(deviceInfo, DeviceInfoEvent.TYPE.REMOVE));
    }

    public List<DeviceInfo> getDevices() {
        return Lists.newArrayList(storedDevices.keySet());
//        return Lists.newArrayList(Lists.transform(deviceManager.getDevices(), new Function<Device, DeviceInfoEntity>() {
//            @Override
//            public DeviceInfoEntity apply(@Nullable Device input) {
//                return getDeviceInfo(input);
//            }
//        }));
    }

    private DeviceInfo getDeviceInfo(final Device device) {
        for (Map.Entry<DeviceInfo, Device> deviceInfoDeviceEntry : storedDevices.entrySet()) {

            if (deviceInfoDeviceEntry.getValue() == device) {
                return deviceInfoDeviceEntry.getKey();
            }
        }
        throw new RuntimeException("no stored device found for device:" + device);

//        DeviceInfoEntity deviceInfo = new DeviceInfoEntity();
//        deviceInfo.setKey(deviceManager.getDeviceId(device));
//        if (device instanceof SerialDevice) {
//            deviceInfo.setType(DeviceInfoEntity.DEVICE_TYPE.SERIAL);
//        } else {
//            deviceInfo.setType(DeviceInfoEntity.DEVICE_TYPE.TEST);
//        }
//        deviceInfo.setConnected(device.isConnected());
//        return deviceInfo;
    }

    public boolean getRailVoltage() {
        if (isBusConnected()) {
            try {
                return deviceManager.getConnectedDevice().getRailVoltage();
            } catch (Exception e) {
                LOGGER.error("can't read rail voltage", e);
            }
        }
        return false;
    }

    public void toggleRailVoltage() {
        if (isBusConnected()) {
            try {
                Device connectedDevice = deviceManager.getConnectedDevice();
                connectedDevice.setRailVoltage(!connectedDevice.getRailVoltage());
            } catch (Exception e) {
                LOGGER.error("can't toggle rail voltage", e);
            }
        }
    }

    public void startTrackingBus() {
        if (activeDevice != null && !trackingActive) {
            activeDevice.getBusDataDispatcher().registerConsumer(allBusDataConsumer);
            trackingActive = true;
        }
    }

    public void stopTrackingBus() {
        if (activeDevice != null && trackingActive) {
            activeDevice.getBusDataDispatcher().unregisterConsumer(allBusDataConsumer);
            trackingActive = false;
        }
    }

    public void sendBusData(int busNr, int address, int bit, boolean state) {
        if (activeDevice != null) {
            try {
                if (state) {
                    activeDevice.getBusAddress(busNr, (byte) address).setBit(bit).send();
                } else {
                    activeDevice.getBusAddress(busNr, (byte) address).clearBit(bit).send();
                }
            } catch (DeviceAccessException e) {
                LOGGER.error(String.format("can't send data (bus: %d, address: %d, bit: %d)", busNr, address, bit), e);
            }
        }
    }

    public void sendBusData(int busNr, int address, int data) {
        if (activeDevice != null) {
            try {
                activeDevice.getBusAddress(busNr, (byte) address).sendData((byte) data);
            } catch (DeviceAccessException e) {
                LOGGER.error(String.format("can't send data (bus: %d, address: %d, data: %d)", busNr, address, data),
                        e);
            }
        }
    }

    public void startRecording(String fileName) {
        deviceRecorder.startRecording(activeDevice, null);
    }

    public void stopRecording() {
        deviceRecorder.stopRecording();
    }

    public void startPlayer(String absoluteFilePath) {
        busDataPlayer = new BusDataPlayer(activeDevice.getBusDataDispatcher(), activeDevice.getBusDataChannel());
        busDataPlayer.addListener(new BusDataPlayerListener() {
            @Override
            public void playbackStarted() {
                LOGGER.info("playback started");
                eventBroadcaster.fireEvent(new PlayerEvent(PlayerEvent.STATE.START));
            }

            @Override
            public void playbackStopped() {
                LOGGER.info("playback stopped");
                eventBroadcaster.fireEvent(new PlayerEvent(PlayerEvent.STATE.STOP));
            }
        });
        try {
            busDataPlayer.start(Paths.get(absoluteFilePath));
        } catch (Exception e) {
            LOGGER.error("can't start player", e);
        }
    }

    public void stopPlayer() {
        busDataPlayer.stop();
    }

    public List<String> getRecords() {
        final List<String> recordsAbsoluteFilePath = Lists.newArrayList();
        try {
            DirectoryStream<Path> directoryStream = Files.newDirectoryStream(deviceRecorder.getDestinationFolder());
            for (Path path : directoryStream) {
                recordsAbsoluteFilePath.add(path.toString());
            }
        } catch (IOException e) {
            LOGGER.error("check files of records", e);
        }
        return recordsAbsoluteFilePath;
    }

    public void connectBus() {
        if (activeDevice != null) {
            try {
                activeDevice.connect();
            } catch (DeviceAccessException e) {
                LOGGER.error(String.format("can't connect active device: %s", activeDevice.getClass().getName()), e);
            }
        }
    }

    public void disconnectBus() {
        if (activeDevice != null) {
            try {
                activeDevice.disconnect();
            } catch (DeviceAccessException e) {
                LOGGER.error("disconnect", e);
            }
        }
    }

    public boolean isBusConnected() {
        return activeDevice != null && activeDevice.isConnected();
    }

}
