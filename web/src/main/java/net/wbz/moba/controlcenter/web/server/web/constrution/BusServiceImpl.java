package net.wbz.moba.controlcenter.web.server.web.constrution;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.wbz.moba.controlcenter.web.server.event.EventBroadcaster;
import net.wbz.moba.controlcenter.web.server.persist.device.DeviceInfoDao;
import net.wbz.moba.controlcenter.web.server.persist.device.DeviceInfoEntity;
import net.wbz.moba.controlcenter.web.server.web.DataMapper;
import net.wbz.moba.controlcenter.web.shared.bus.BusDataEvent;
import net.wbz.moba.controlcenter.web.shared.bus.BusService;
import net.wbz.moba.controlcenter.web.shared.bus.DeviceConnectionEvent;
import net.wbz.moba.controlcenter.web.shared.bus.DeviceInfo;
import net.wbz.moba.controlcenter.web.shared.bus.DeviceInfoEvent;
import net.wbz.moba.controlcenter.web.shared.bus.PlayerEvent;
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

/**
 * @author Daniel Tuerk
 */
@Singleton
public class BusServiceImpl extends RemoteServiceServlet implements BusService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BusServiceImpl.class);
    private final DeviceManager deviceManager;
    private final EventBroadcaster eventBroadcaster;
    private final AllBusDataConsumer allBusDataConsumer;
    private final DeviceRecorder deviceRecorder;
    private final Map<DeviceInfo, Device> storedDevices = Maps.newHashMap();
    private final DeviceInfoDao deviceInfoDao;
    private final DataMapper<DeviceInfo, DeviceInfoEntity> dtoMapper;
    private net.wbz.selectrix4java.device.Device activeDevice;
    private boolean trackingActive = false;
    private BusDataPlayer busDataPlayer;

    @Inject
    public BusServiceImpl(DeviceManager deviceManager, final EventBroadcaster eventBroadcaster,
        DeviceRecorder deviceRecorder, DeviceInfoDao deviceInfoDao) {
        this.deviceManager = deviceManager;
        this.eventBroadcaster = eventBroadcaster;
        this.deviceRecorder = deviceRecorder;
        this.deviceInfoDao = deviceInfoDao;

        this.dtoMapper = new DataMapper<>(DeviceInfo.class, DeviceInfoEntity.class);

        this.allBusDataConsumer = new AllBusDataConsumer() {
            @Override
            public void valueChanged(int bus, int address, int oldValue, int newValue) {
                eventBroadcaster.fireEvent(new BusDataEvent(bus, address, newValue));
            }
        };

        initConnectionListener();
    }

    private void initConnectionListener() {

        for (DeviceInfoEntity deviceInfo : deviceInfoDao.listAll()) {
            // TODO - values from config
            registerDevice(deviceInfo);
        }

        deviceManager.addDeviceConnectionListener(new DeviceConnectionListener() {
            @Override
            public void connected(Device device) {
                final DeviceInfo deviceInfo = getDeviceInfo(device);
                deviceInfo.setConnected(true);
                // fire initial rail voltage state
                eventBroadcaster.fireEvent(new DeviceConnectionEvent(deviceInfo, DeviceConnectionEvent.TYPE.CONNECTED));
                // add listener to receive state change
                device.addRailVoltageListener(isOn -> eventBroadcaster.fireEvent(new RailVoltageEvent(isOn)));
            }

            @Override
            public void disconnected(Device device) {
                DeviceInfo deviceInfo = getDeviceInfo(device);
                deviceInfo.setConnected(false);
                BusServiceImpl.this.eventBroadcaster
                    .fireEvent(new DeviceConnectionEvent(deviceInfo, DeviceConnectionEvent.TYPE.DISCONNECTED));
                device.getBusDataDispatcher().unregisterConsumer(allBusDataConsumer);
            }
        });
    }

    private void registerDevice(DeviceInfoEntity deviceInfo) {
        storedDevices.put(dtoMapper.transformSource(deviceInfo), deviceManager
            .registerDevice(DeviceManager.DEVICE_TYPE.valueOf(deviceInfo.getType().name()), deviceInfo.getKey(),
                SerialDevice.DEFAULT_BAUD_RATE_FCC));
    }

    @Override
    public void changeDevice(DeviceInfo deviceInfo) {
        activeDevice = deviceManager.getDeviceById(deviceInfo.getKey());
    }

    @Override
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

    @Override
    @Transactional
    public void deleteDevice(DeviceInfo deviceInfo) {
        Device device = deviceManager.getDeviceById(deviceInfo.getKey());

        deviceInfoDao.delete(deviceInfoDao.findById(deviceInfo.getId()));

        deviceManager.removeDevice(device);

        storedDevices.remove(deviceInfo);
        eventBroadcaster.fireEvent(new DeviceInfoEvent(deviceInfo, DeviceInfoEvent.TYPE.REMOVE));
    }

    public List<DeviceInfo> getDevices() {
        return Lists.newArrayList(storedDevices.keySet());
    }

    private DeviceInfo getDeviceInfo(final Device device) {
        for (Map.Entry<DeviceInfo, Device> deviceInfoDeviceEntry : storedDevices.entrySet()) {

            if (deviceInfoDeviceEntry.getValue() == device) {
                return deviceInfoDeviceEntry.getKey();
            }
        }
        throw new RuntimeException("no stored device found for device:" + device);
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
        final List<String> recordsAbsoluteFilePath = new ArrayList<>();
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

    @Override
    public void requestResendLastEvent(final String eventClazzName) {
        eventBroadcaster.resendEvent(eventClazzName);
    }

    public void connectBus() {
        if (activeDevice != null && !activeDevice.isConnected()) {
            try {
                activeDevice.connect();
            } catch (DeviceAccessException e) {
                LOGGER.error(String.format("can't connect active device: %s", activeDevice.getClass().getName()), e);
            }
        }
    }

    public void disconnectBus() {
        if (activeDevice != null && activeDevice.isConnected()) {
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
