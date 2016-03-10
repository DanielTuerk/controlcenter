package net.wbz.moba.controlcenter.web.server.constrution;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Provider;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import net.wbz.moba.controlcenter.web.server.EventBroadcaster;
import net.wbz.moba.controlcenter.web.shared.bus.BusData;
import net.wbz.moba.controlcenter.web.shared.bus.BusDataEvent;
import net.wbz.moba.controlcenter.web.shared.bus.BusService;
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

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;

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
    private final Provider<EntityManager> entityManager;
    private net.wbz.selectrix4java.device.Device activeDevice;
    private boolean trackingActive = false;
    private BusDataPlayer busDataPlayer;

    @Inject
    public BusServiceImpl(DeviceManager deviceManager, final EventBroadcaster eventBroadcaster,
            DeviceRecorder deviceRecorder, Provider<EntityManager> entityManager) {
        this.deviceManager = deviceManager;
        this.eventBroadcaster = eventBroadcaster;
        this.deviceRecorder = deviceRecorder;

        this.entityManager = entityManager;

        Query query = this.entityManager.get().createQuery("SELECT x FROM DeviceInfo x");
        List<DeviceInfo> storedDevices = query.getResultList();

        for (DeviceInfo deviceInfo : storedDevices) {
            // TODO - values from config
            deviceManager.registerDevice(DeviceManager.DEVICE_TYPE.valueOf(
                    deviceInfo.getType().name()), deviceInfo.getKey(), SerialDevice.DEFAULT_BAUD_RATE_FCC);
        }

        allBusDataConsumer = new AllBusDataConsumer() {

            @Override
            public void valueChanged(int bus, int address, int oldValue, int newValue) {
                eventBroadcaster.fireEvent(new BusDataEvent(bus, address, newValue));
            }
        };

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

    @Override
    public void changeDevice(DeviceInfo deviceInfo) {
        activeDevice = deviceManager.getDeviceById(deviceInfo.getKey());
    }

    @Override
    @Transactional
    public void createDevice(DeviceInfo deviceInfo) {
        // TODO - device settings (e.g. serial/test)
        entityManager.get().persist(deviceInfo);

        deviceManager.registerDevice(DeviceManager.DEVICE_TYPE.valueOf(
                deviceInfo.getType().name()), deviceInfo.getKey(), SerialDevice.DEFAULT_BAUD_RATE_FCC);

        eventBroadcaster.fireEvent(new DeviceInfoEvent(deviceInfo, DeviceInfoEvent.TYPE.CREATE));
    }

    @Override
    public void deleteDevice(DeviceInfo deviceInfo) {
        Device device = deviceManager.getDeviceById(deviceInfo.getKey());

        Query query = this.entityManager.get().createQuery("SELECT x FROM DeviceInfo x where key=:key");
        DeviceInfo persistDeviceInfo = (DeviceInfo) query.setParameter("key", deviceInfo.getKey()).getSingleResult();
        entityManager.get().remove(persistDeviceInfo);

        deviceManager.removeDevice(device);

        eventBroadcaster.fireEvent(new DeviceInfoEvent(deviceInfo, DeviceInfoEvent.TYPE.REMOVE));
    }

    @Override
    public ArrayList<DeviceInfo> getDevices() {
        return Lists.newArrayList(Lists.transform(deviceManager.getDevices(), new Function<Device, DeviceInfo>() {
            @Override
            public DeviceInfo apply(@Nullable Device input) {
                return getDeviceInfo(input);
            }
        }));
    }

    private DeviceInfo getDeviceInfo(Device device) {
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setKey(deviceManager.getDeviceId(device));
        if (device instanceof SerialDevice) {
            deviceInfo.setType(DeviceInfo.DEVICE_TYPE.SERIAL);
        } else {
            deviceInfo.setType(DeviceInfo.DEVICE_TYPE.TEST);
        }
        deviceInfo.setConnected(device.isConnected());
        return deviceInfo;
    }

    @Override
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

    @Override
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

    @Override
    public BusData[] readBusData(int busNr) {
        // TODO required?
        byte[] busData = activeDevice.getBusDataDispatcher().getData(busNr);
        BusData[] replyData = new BusData[busData.length];
        for (int i = 0; i < busData.length; i++) {
            replyData[i] = new BusData(i, (int) busData[i]);
        }
        return replyData;
    }

    @Override
    public void startTrackingBus() {
        if (activeDevice != null && !trackingActive) {
            activeDevice.getBusDataDispatcher().registerConsumer(allBusDataConsumer);
            trackingActive = true;
        }
    }

    @Override
    public void stopTrackingBus() {
        if (activeDevice != null && trackingActive) {
            activeDevice.getBusDataDispatcher().unregisterConsumer(allBusDataConsumer);
            trackingActive = false;
        }
    }

    @Override
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

    @Override
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

    @Override
    public void startRecording(String fileName) {
        deviceRecorder.startRecording(activeDevice, null);
    }

    @Override
    public void stopRecording() {
        deviceRecorder.stopRecording();
    }

    @Override
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

    @Override
    public void stopPlayer() {
        busDataPlayer.stop();
    }

    @Override
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

    @Override
    public void connectBus() {
        if (activeDevice != null) {
            try {
                activeDevice.connect();
            } catch (DeviceAccessException e) {
                LOGGER.error(String.format("can't connect active device: %s", activeDevice.getClass().getName()), e);
            }
        }
    }

    @Override
    public void disconnectBus() {
        if (activeDevice != null) {
            try {
                activeDevice.disconnect();
            } catch (DeviceAccessException e) {
                LOGGER.error("disconnect", e);
            }
        }
    }

    @Override
    public boolean isBusConnected() {
        return activeDevice != null && activeDevice.isConnected();
    }

}
