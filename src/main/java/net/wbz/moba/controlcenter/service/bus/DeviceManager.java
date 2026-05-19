package net.wbz.moba.controlcenter.service.bus;

import jakarta.enterprise.context.ApplicationScoped;
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

    public DeviceInfo create(DeviceInfo construction) {
        var entity = dataProvider.createDevice(construction);
        eventBroadcaster.fireEvent(new DeviceDataChangedEvent(entity.id));
        return deviceInfoMapper.toDto(entity);
    }

    public DeviceInfoEntity update(Long id, DeviceInfo updated) {
        final var entity = dataProvider.updateDevice(id, updated);
        eventBroadcaster.fireEvent(new DeviceDataChangedEvent(id));
        return entity;
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
        eventBroadcaster.fireEvent(new DeviceDataChangedEvent(id));
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
