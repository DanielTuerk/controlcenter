package net.wbz.moba.controlcenter.service.bus;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
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
    private DeviceInfo activeDeviceInfo;

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
        activeDeviceInfo = deviceInfo;
    }

    public void connect() throws DeviceAccessException {
        if (activeDevice != null && !activeDevice.isConnected()) {
                activeDevice.connect();
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
                    final DeviceInfo deviceInfo = activeDeviceInfo;
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
                    DeviceInfo deviceInfo = activeDeviceInfo;
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

}
