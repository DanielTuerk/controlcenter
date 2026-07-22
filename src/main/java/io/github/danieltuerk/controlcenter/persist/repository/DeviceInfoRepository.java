package io.github.danieltuerk.controlcenter.persist.repository;

import io.github.danieltuerk.controlcenter.persist.entity.DeviceInfoEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * @author Daniel Tuerk
 */
@ApplicationScoped
public class DeviceInfoRepository implements PanacheRepository<DeviceInfoEntity> {

}
