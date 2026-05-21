package net.wbz.moba.controlcenter.service.bus;

import io.quarkus.runtime.Startup;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.wbz.moba.controlcenter.EventBroadcaster;
import net.wbz.moba.controlcenter.shared.bus.RailVoltageEvent;
import net.wbz.moba.controlcenter.shared.bus.SystemFormatEvent;
import net.wbz.moba.controlcenter.shared.device.DeviceConnectionEvent;
import net.wbz.moba.controlcenter.shared.device.DeviceDataChangedEvent;
import net.wbz.moba.controlcenter.shared.device.DeviceInfo;
import net.wbz.selectrix4java.device.Device;
import net.wbz.selectrix4java.device.DeviceAccessException;
import net.wbz.selectrix4java.device.DeviceConnectionListener;
import net.wbz.selectrix4java.device.DeviceManager;
import net.wbz.selectrix4java.device.DeviceManager.DEVICE_TYPE;
import net.wbz.selectrix4java.device.RailVoltageListener;
import net.wbz.selectrix4java.device.SystemFormatListener;
import net.wbz.selectrix4java.device.serial.SerialDevice;

import java.util.Optional;

/**
 * @author Daniel Tuerk
 */
@Slf4j
@Startup
@ApplicationScoped
public class DeviceService {

    private final DeviceManager selectrixDeviceManager;
    private final net.wbz.moba.controlcenter.service.bus.DeviceManager deviceManager;
    private final EventBroadcaster eventBroadcaster;
    private final RailVoltageListener railVoltageListener;
    private final SystemFormatListener systemFormatListener;
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
        systemFormatListener = systemFormat -> eventBroadcaster.fireEvent(new SystemFormatEvent(systemFormat));
    }

    public void onDeviceDataChanged(@Observes DeviceDataChangedEvent evt) {
        log.info("DeviceDataChangedEvent: {}", evt);

        initConnectionListener();
    }

    public void changeDevice(DeviceInfo deviceInfo) throws DeviceAccessException {
        activeDevice = selectrixDeviceManager.getDeviceById(deviceInfo.getKey())
            .orElseThrow(() -> new DeviceAccessException("no device registered for key: %s available: %s"
                .formatted(deviceInfo.getKey(), selectrixDeviceManager.getDevices())));
        activeDeviceInfo = deviceInfo;
    }

    public void connect() throws DeviceAccessException {
        if (activeDevice == null) throw new DeviceAccessException("no active device");
        if (activeDevice.isConnected()) throw new DeviceAccessException("device already connected");
        activeDevice.connect();
    }

    public void disconnect() throws DeviceAccessException {
        if (activeDevice == null) throw new DeviceAccessException("no active device");
        if (!activeDevice.isConnected()) throw new DeviceAccessException("device not connected");
        try {
            activeDevice.disconnect();
        } catch (DeviceAccessException e) {
            log.error("disconnect", e);
        }
    }

    public Optional<Device> getConnectedDevice() {
        return activeDevice != null && activeDevice.isConnected() ? Optional.of(activeDevice) : Optional.empty();
    }

    private void initConnectionListener() {
        log.debug("init connection listeners");
        reregisterDevices();

        selectrixDeviceManager.addDeviceConnectionListener(new DeviceConnectionListener() {
            @Override
            public void connected(Device device) {
                if (device.equals(activeDevice)) {
                    final DeviceInfo deviceInfo = activeDeviceInfo;
                    deviceInfo.setConnected(true);
                    // fire initial rail voltage state
                    eventBroadcaster
                        .fireEvent(new DeviceConnectionEvent(deviceInfo, true));
                    // add listener to receive state change
                    device.addRailVoltageListener(railVoltageListener);
                    device.addSystemFormatListener(systemFormatListener);
                }
            }

            @Override
            public void disconnected(Device device) {
                if (device.equals(activeDevice)) {
                    DeviceInfo deviceInfo = activeDeviceInfo;
                    deviceInfo.setConnected(false);
                    eventBroadcaster
                        .fireEvent(new DeviceConnectionEvent(deviceInfo, false));
                    device.removeRailVoltageListener(railVoltageListener);
                    device.removeSystemFormatListener(systemFormatListener);
                }
            }
        });
    }

    private void reregisterDevices() {
        selectrixDeviceManager.getDevices().forEach(selectrixDeviceManager::removeDevice);
        deviceManager.load().forEach(deviceInfo -> {
            Device device = selectrixDeviceManager.createDevice(
                DEVICE_TYPE.valueOf(deviceInfo.getType().name()),
                deviceInfo.getKey(),
                SerialDevice.DEFAULT_BAUD_RATE_FCC);
            selectrixDeviceManager.registerDevice(device);
        });
    }

}
