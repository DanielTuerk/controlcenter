package net.wbz.moba.controlcenter.persist.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import net.wbz.moba.controlcenter.persist.entity.DeviceInfoEntity;

/**
 * @author Daniel Tuerk
 */
@ApplicationScoped
public class DeviceInfoRepository implements PanacheRepository<DeviceInfoEntity> {

}
