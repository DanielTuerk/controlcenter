package net.wbz.moba.controlcenter.service.bus;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.Objects;
import java.util.Optional;
import net.wbz.moba.controlcenter.EventBroadcaster;
import net.wbz.moba.controlcenter.shared.bus.DeviceConnectionEvent;
import net.wbz.moba.controlcenter.shared.bus.DeviceInfo;
import net.wbz.moba.controlcenter.shared.viewer.RailVoltageEvent;
import net.wbz.selectrix4java.device.Device;
import net.wbz.selectrix4java.device.DeviceAccessException;
import net.wbz.selectrix4java.device.DeviceConnectionListener;
import net.wbz.selectrix4java.device.DeviceManager;
import net.wbz.selectrix4java.device.DeviceManager.DEVICE_TYPE;
import net.wbz.selectrix4java.device.RailVoltageListener;
import net.wbz.selectrix4java.device.serial.SerialDevice;
import org.jboss.logging.Logger;

/**
 * @author Daniel Tuerk
 */
@ApplicationScoped
public class DeviceService {

    private static final Logger LOGGER = Logger.getLogger(DeviceService.class);
    private final DeviceManager selectrixDeviceManager;
    private final net.wbz.moba.controlcenter.service.bus.DeviceManager deviceManager;
    private final EventBroadcaster eventBroadcaster;
    private final RailVoltageListener railVoltageListener;
    private Device activeDevice;

    @Inject
    public DeviceService(DeviceManager selectrixDeviceManager,
        final EventBroadcaster eventBroadcaster,
        net.wbz.moba.controlcenter.service.bus.DeviceManager deviceManager) {
        this.deviceManager = deviceManager;
        this.selectrixDeviceManager = selectrixDeviceManager;
        this.eventBroadcaster = eventBroadcaster;

        initConnectionListener();

        railVoltageListener = isOn -> eventBroadcaster.fireEvent(new RailVoltageEvent(isOn));
    }

    public void changeDevice(DeviceInfo deviceInfo) {
        activeDevice = selectrixDeviceManager.getDeviceById(deviceInfo.getKey());
    }

    public void connect() {
        if (activeDevice != null && !activeDevice.isConnected()) {
            try {
                activeDevice.connect();
            } catch (DeviceAccessException e) {
                LOGGER.error(String.format("can't connect active device: %s", activeDevice.getClass().getName()), e);
            }
        }
    }

    public void disconnect() {
        if (activeDevice != null && activeDevice.isConnected()) {
            try {
                activeDevice.disconnect();
            } catch (DeviceAccessException e) {
                LOGGER.error("disconnect", e);
            }
        }
    }

    public Optional<Device> getConnectedDevice() {
        return activeDevice != null && activeDevice.isConnected() ? Optional.of(activeDevice) : Optional.empty();
    }

    private void initConnectionListener() {
        for (DeviceInfo deviceInfo : deviceManager.load()) {
            // TODO - values from config
            registerDevice(deviceInfo);
        }

        selectrixDeviceManager.addDeviceConnectionListener(new DeviceConnectionListener() {
            @Override
            public void connected(Device device) {
                if (device.equals(activeDevice)) {
                    final DeviceInfo deviceInfo = getDeviceInfo(device);
                    deviceInfo.setConnected(true);
                    // fire initial rail voltage state
                    eventBroadcaster
                        .fireEvent(new DeviceConnectionEvent(deviceInfo, DeviceConnectionEvent.TYPE.CONNECTED));
                    // add listener to receive state change
                    device.addRailVoltageListener(railVoltageListener);
                }
            }

            @Override
            public void disconnected(Device device) {
                if (device.equals(activeDevice)) {
                    DeviceInfo deviceInfo = getDeviceInfo(device);
                    deviceInfo.setConnected(false);
                    eventBroadcaster
                        .fireEvent(new DeviceConnectionEvent(deviceInfo, DeviceConnectionEvent.TYPE.DISCONNECTED));
                    device.removeRailVoltageListener(railVoltageListener);
                }
            }
        });
    }


    private void registerDevice(DeviceInfo deviceInfo) {
        Device device = selectrixDeviceManager
            .createDevice(DEVICE_TYPE.valueOf(deviceInfo.getType().name()), deviceInfo.getKey(),
                SerialDevice.DEFAULT_BAUD_RATE_FCC);
        selectrixDeviceManager.registerDevice(device);
    }

    private DeviceInfo getDeviceInfo(final Device device) {
        for (DeviceInfo deviceInfoDeviceEntry : deviceManager.load()) {
            if (Objects.equals(deviceInfoDeviceEntry.getKey(), device.getDeviceId())) {
                return deviceInfoDeviceEntry;
            }
        }
        throw new RuntimeException("no stored device found for device:" + device);
    }

}
