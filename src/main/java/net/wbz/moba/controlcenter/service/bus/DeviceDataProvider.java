package net.wbz.moba.controlcenter.service.bus;

import io.quarkus.cache.CacheInvalidateAll;
import io.quarkus.cache.CacheResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import net.wbz.moba.controlcenter.persist.entity.DeviceInfoEntity;
import net.wbz.moba.controlcenter.persist.entity.DeviceInfoEntity.DEVICE_TYPE;
import net.wbz.moba.controlcenter.persist.repository.DeviceInfoRepository;
import net.wbz.moba.controlcenter.shared.device.DeviceInfo;
import org.jboss.logging.Logger;

import java.util.List;

@ApplicationScoped
public class DeviceDataProvider {

    private static final Logger LOG = Logger.getLogger(DeviceDataProvider.class);
    private static final String CACHE = "devices-cache";

    private final DeviceInfoRepository deviceInfoRepository;

    public DeviceDataProvider(DeviceInfoRepository deviceInfoRepository) {
        this.deviceInfoRepository = deviceInfoRepository;
    }

    /**
     * Load all devices from database and cache the result.
     */
    @CacheResult(cacheName = CACHE)
    public List<DeviceInfoEntity> getDevices() {
        LOG.debug("load devices from database");
        return deviceInfoRepository.listAll();
    }

    /**
     * Create a new device and invalidate the cache.
     */
    @CacheInvalidateAll(cacheName = CACHE)
    @Transactional
    public DeviceInfoEntity createDevice(DeviceInfo device) {
        DeviceInfoEntity entity = new DeviceInfoEntity();
        entity.key = device.getKey();
        entity.type = DEVICE_TYPE.valueOf(device.getType().name());
        deviceInfoRepository.persist(entity);
        return entity;
    }

    /**
     * Update an existing device and invalidate the cache.
     */
    @CacheInvalidateAll(cacheName = CACHE)
    @Transactional
    public DeviceInfoEntity updateDevice(Long id, DeviceInfo updated) {
        DeviceInfoEntity existing = deviceInfoRepository.findById(id);
        if (existing == null) {
            throw new EntityNotFoundException();
        }
        existing.key = updated.getKey();
        existing.type = DEVICE_TYPE.valueOf(updated.getType().name());
        return existing;
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
