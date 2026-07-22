package io.github.danieltuerk.controlcenter.service.bus;

import io.github.danieltuerk.controlcenter.EventBroadcaster;
import io.github.danieltuerk.controlcenter.MetricConstants;
import io.github.danieltuerk.controlcenter.shared.bus.RailVoltageEvent;
import io.github.danieltuerk.controlcenter.shared.bus.SystemFormatEvent;
import io.github.danieltuerk.controlcenter.shared.device.DeviceConnectionEvent;
import io.github.danieltuerk.controlcenter.shared.device.DeviceDataChangedEvent;
import io.github.danieltuerk.controlcenter.shared.device.DeviceInfo;
import io.github.danieltuerk.selectrix4java.device.Device;
import io.github.danieltuerk.selectrix4java.device.DeviceAccessException;
import io.github.danieltuerk.selectrix4java.device.DeviceConnectionListener;
import io.github.danieltuerk.selectrix4java.device.DeviceManager;
import io.github.danieltuerk.selectrix4java.device.DeviceManager.DEVICE_TYPE;
import io.github.danieltuerk.selectrix4java.device.RailVoltageListener;
import io.github.danieltuerk.selectrix4java.device.SystemFormatListener;
import io.github.danieltuerk.selectrix4java.device.serial.SerialDevice;
import io.micrometer.core.instrument.MeterRegistry;
import io.quarkus.runtime.Startup;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

/**
 * @author Daniel Tuerk
 */
@Slf4j
@Startup
@ApplicationScoped
public class DeviceService {

    private final DeviceManager selectrixDeviceManager;
    private final io.github.danieltuerk.controlcenter.service.bus.DeviceManager deviceManager;
    private final EventBroadcaster eventBroadcaster;
    private final RailVoltageListener railVoltageListener;
    private final SystemFormatListener systemFormatListener;
    private final BusService busService;
    private final MeterRegistry meterRegistry;
    private Device activeDevice;
    private DeviceInfo activeDeviceInfo;

    @Inject
    public DeviceService(DeviceManager selectrixDeviceManager,
                         final EventBroadcaster eventBroadcaster,
                         io.github.danieltuerk.controlcenter.service.bus.DeviceManager deviceManager, BusService busService,
                         MeterRegistry meterRegistry) {
        this.deviceManager = deviceManager;
        this.selectrixDeviceManager = selectrixDeviceManager;
        this.eventBroadcaster = eventBroadcaster;
        this.busService = busService;
        this.meterRegistry = meterRegistry;

        initConnectionListener();

        railVoltageListener = isOn -> eventBroadcaster.fireEvent(new RailVoltageEvent(isOn));
        systemFormatListener = systemFormat -> eventBroadcaster.fireEvent(new SystemFormatEvent(systemFormat));

        meterRegistry.gauge(MetricConstants.DEVICE_CONNECTED, this,
                deviceService -> deviceService.getConnectedDevice().isPresent() ? 1 : 0);
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
        meterRegistry.counter(MetricConstants.DEVICE_CONNECT_TOTAL).increment();
    }

    public void disconnect() throws DeviceAccessException {
        if (activeDevice == null) throw new DeviceAccessException("no active device");
        if (!activeDevice.isConnected()) throw new DeviceAccessException("device not connected");
        activeDevice.disconnect();
        meterRegistry.counter(MetricConstants.DEVICE_DISCONNECT_TOTAL).increment();
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
                    // fire initial rail voltage state
                    eventBroadcaster.fireEvent(new DeviceConnectionEvent(activeDeviceInfo, true));
                    // add listener to receive state change
                    device.addRailVoltageListener(railVoltageListener);
                    device.addSystemFormatListener(systemFormatListener);
                    // throw initial value, because it could be zero
                    eventBroadcaster.fireEvent(new SystemFormatEvent(busService.getSystemFormat()));
                }
            }

            @Override
            public void disconnected(Device device) {
                if (device.equals(activeDevice)) {
                    eventBroadcaster.fireEvent(new DeviceConnectionEvent(activeDeviceInfo, false));
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
