package net.wbz.moba.controlcenter.service.bus;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import net.wbz.moba.controlcenter.persist.entity.DeviceInfoEntity;
import net.wbz.moba.controlcenter.persist.entity.DeviceInfoEntity.DEVICE_TYPE;
import net.wbz.moba.controlcenter.persist.repository.DeviceInfoRepository;
import net.wbz.moba.controlcenter.shared.bus.DeviceInfo;

/**
 * @author Daniel Tuerk
 */
@ApplicationScoped
public class DeviceManager {

    @Inject
    DeviceInfoRepository deviceInfoRepository;
    @Inject
    DeviceInfoMapper deviceInfoMapper;

    @Transactional
    public DeviceInfo create(DeviceInfo construction) {
        DeviceInfoEntity entity = new DeviceInfoEntity();
        entity.key = construction.getKey();
        entity.type = DEVICE_TYPE.valueOf(construction.getType().name());
        deviceInfoRepository.persist(entity);
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
        // TODO throw change event
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
        return deviceInfoRepository.deleteById(id);
    }

    public boolean existsById(Long id) {
        return deviceInfoRepository.findByIdOptional(id).isPresent();
    }
}
