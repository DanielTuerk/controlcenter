package io.github.danieltuerk.controlcenter.persist.repository;

import io.github.danieltuerk.controlcenter.persist.entity.StationPlatformEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * @author Daniel Tuerk
 */
@ApplicationScoped
public class StationPlatformRepository implements PanacheRepository<StationPlatformEntity> {
}
