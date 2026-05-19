package net.wbz.moba.controlcenter.service.bus;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import net.wbz.moba.controlcenter.EventBroadcaster;
import net.wbz.moba.controlcenter.persist.entity.DeviceInfoEntity;
import net.wbz.moba.controlcenter.shared.device.AvailableDevice;
import net.wbz.moba.controlcenter.shared.device.DeviceDataChangedEvent;
import net.wbz.moba.controlcenter.shared.device.DeviceInfo;
import net.wbz.selectrix4java.jna.SerialPortLister;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Daniel Tuerk
 */
@ApplicationScoped
public class DeviceManager {

    @Inject
    DeviceInfoMapper deviceInfoMapper;
    @Inject
    EventBroadcaster eventBroadcaster;
    @Inject
    DeviceDataProvider dataProvider;
    @Inject
    Event<DeviceDataChangedEvent> deviceDataChangedEvent;

    public DeviceInfo create(DeviceInfo construction) {
        var entity = dataProvider.createDevice(construction);
        fireEvent(entity.id);
        return deviceInfoMapper.toDto(entity);
    }

    public DeviceInfoEntity update(Long id, DeviceInfo updated) {
        final var entity = dataProvider.updateDevice(id, updated);
        fireEvent(id);
        return entity;
    }

    private void fireEvent(Long id) {
        final var event = new DeviceDataChangedEvent(id);
        deviceDataChangedEvent.fire(event);
        eventBroadcaster.fireEvent(event);
    }

    public List<DeviceInfo> load() {
        return dataProvider.getDevices().stream()
            .map(deviceInfoMapper::toDto)
            .collect(Collectors.toList());
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
