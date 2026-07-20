package io.github.danieltuerk.controlcenter.service.bus;

import io.github.danieltuerk.controlcenter.persist.entity.DeviceInfoEntity;
import io.github.danieltuerk.controlcenter.persist.entity.DeviceInfoEntity.DEVICE_TYPE;
import io.github.danieltuerk.controlcenter.persist.repository.DeviceInfoRepository;
import io.github.danieltuerk.controlcenter.shared.device.DeviceInfo;
import io.quarkus.cache.CacheInvalidateAll;
import io.quarkus.cache.CacheResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@ApplicationScoped
public class DeviceDataProvider {

    private static final String CACHE = "devices-cache";

    private final DeviceInfoRepository deviceInfoRepository;
    private final DeviceInfoMapper deviceInfoMapper;

    public DeviceDataProvider(DeviceInfoRepository deviceInfoRepository, DeviceInfoMapper deviceInfoMapper) {
        this.deviceInfoRepository = deviceInfoRepository;
        this.deviceInfoMapper = deviceInfoMapper;
    }

    /**
     * Load all devices from the database and cache the result.
     */
    @CacheResult(cacheName = CACHE)
    public List<DeviceInfo> getDevices() {
        log.debug("load devices from database");
        return deviceInfoRepository.listAll().stream()
            .map(deviceInfoMapper::toDto)
            .collect(Collectors.toList());
    }

    /**
     * Create a new device and invalidate the cache.
     */
    @CacheInvalidateAll(cacheName = CACHE)
    @Transactional
    public Long createDevice(DeviceInfo device) {
        DeviceInfoEntity entity = new DeviceInfoEntity();
        entity.key = device.getKey();
        entity.type = DEVICE_TYPE.valueOf(device.getType().name());
        deviceInfoRepository.persist(entity);
        return entity.id;
    }

    /**
     * Update an existing device and invalidate the cache.
     */
    @CacheInvalidateAll(cacheName = CACHE)
    @Transactional
    public void updateDevice(Long id, DeviceInfo updated) {
        DeviceInfoEntity existing = deviceInfoRepository.findById(id);
        if (existing == null) {
            throw new EntityNotFoundException();
        }
        existing.key = updated.getKey();
        existing.type = DEVICE_TYPE.valueOf(updated.getType().name());
        deviceInfoRepository.persist(existing);
    }

    /**
     * Delete a device by id and invalidate the cache.
     */
    @CacheInvalidateAll(cacheName = CACHE)
    @Transactional
    public boolean deleteDevice(Long id) {
        return deviceInfoRepository.deleteById(id);
    }
}
