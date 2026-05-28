package net.wbz.moba.controlcenter.service.bus;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import net.wbz.moba.controlcenter.EventBroadcaster;
import net.wbz.moba.controlcenter.shared.device.AvailableDevice;
import net.wbz.moba.controlcenter.shared.device.DeviceDataChangedEvent;
import net.wbz.moba.controlcenter.shared.device.DeviceInfo;
import net.wbz.selectrix4java.jna.SerialPortLister;

import java.util.List;
import java.util.Optional;

/**
 * @author Daniel Tuerk
 */
@ApplicationScoped
public class DeviceManager {

    @Inject
    EventBroadcaster eventBroadcaster;
    @Inject
    DeviceDataProvider dataProvider;
    @Inject
    Event<DeviceDataChangedEvent> deviceDataChangedEvent;

    public DeviceInfo create(DeviceInfo construction) {
        var id = dataProvider.createDevice(construction);
        fireEvent(id);
        return getById(id).orElseThrow(() ->
            new IllegalStateException("Device with id " + id + " not found after create"));
    }

    public DeviceInfo update(Long id, DeviceInfo updated) {
        dataProvider.updateDevice(id, updated);
        fireEvent(id);
        return getById(id).orElseThrow(() ->
            new IllegalStateException("Device with id " + id + " not found after update"));
    }

    private void fireEvent(Long id) {
        final var event = new DeviceDataChangedEvent(id);
        deviceDataChangedEvent.fire(event);
        eventBroadcaster.fireEvent(event);
    }

    public List<DeviceInfo> load() {
        return dataProvider.getDevices();
    }

    public Optional<DeviceInfo> getById(Long id) {
        return load().stream()
            .filter(deviceInfo -> id.equals(deviceInfo.getId()))
            .findFirst();
    }

    public boolean deleteById(Long id) {
        var state = dataProvider.deleteDevice(id);
        fireEvent(id);
        return state;
    }

    public boolean existsById(Long id) {
        return getById(id).isPresent();
    }

    public List<AvailableDevice> getAvailable() {
        return SerialPortLister.list().stream()
            .map(serialPort -> new AvailableDevice(serialPort.path(),
                serialPort.name()))
                .toList();
    }
}
