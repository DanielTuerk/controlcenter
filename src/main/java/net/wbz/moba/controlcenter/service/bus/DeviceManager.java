package net.wbz.moba.controlcenter.service.bus;

import com.fazecast.jSerialComm.SerialPort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import net.wbz.moba.controlcenter.EventBroadcaster;
import net.wbz.moba.controlcenter.persist.entity.DeviceInfoEntity;
import net.wbz.moba.controlcenter.persist.entity.DeviceInfoEntity.DEVICE_TYPE;
import net.wbz.moba.controlcenter.persist.repository.DeviceInfoRepository;
import net.wbz.moba.controlcenter.shared.device.AvailableDevice;
import net.wbz.moba.controlcenter.shared.device.DeviceDataChangedEvent;
import net.wbz.moba.controlcenter.shared.device.DeviceInfo;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Daniel Tuerk
 */
@ApplicationScoped
public class DeviceManager {

    @Inject
    DeviceInfoRepository deviceInfoRepository;
    @Inject
    DeviceInfoMapper deviceInfoMapper;
    @Inject
    EventBroadcaster eventBroadcaster;

    @Transactional
    public DeviceInfo create(DeviceInfo construction) {
        DeviceInfoEntity entity = new DeviceInfoEntity();
        entity.key = construction.getKey();
        entity.type = DEVICE_TYPE.valueOf(construction.getType().name());
        deviceInfoRepository.persist(entity);
        eventBroadcaster.fireEvent(new DeviceDataChangedEvent(entity.id));
        return deviceInfoMapper.toDto(entity);
    }

    @Transactional
    public void update(Long id, DeviceInfo updated) {
        DeviceInfoEntity existing = deviceInfoRepository.findById(id);
        if (existing == null) {
            throw new EntityNotFoundException();
        }
        existing.key = updated.getKey();
        existing.type = DEVICE_TYPE.valueOf(updated.getType().name());

        eventBroadcaster.fireEvent(new DeviceDataChangedEvent(id));
    }

    public List<DeviceInfo> load() {
        return deviceInfoRepository.streamAll()
            .map(deviceInfoMapper::toDto)
            .collect(Collectors.toList());
    }

    public Optional<DeviceInfo> getById(Long id) {
        return deviceInfoRepository.findByIdOptional(id).map(deviceInfoMapper::toDto);
    }

    @Transactional
    public boolean deleteById(Long id) {
        var state = deviceInfoRepository.deleteById(id);
        eventBroadcaster.fireEvent(new DeviceDataChangedEvent(id));
        return state;
    }

    public boolean existsById(Long id) {
        return deviceInfoRepository.findByIdOptional(id).isPresent();
    }

    public List<AvailableDevice> getAvailable() {
        return Stream.of(SerialPort.getCommPorts())
                .map(serialPort -> new AvailableDevice(serialPort.getSystemPortName(),
                        serialPort.getDescriptivePortName()))
                .toList();
    }
}
