package io.github.danieltuerk.controlcenter.persist.repository;

import io.github.danieltuerk.controlcenter.persist.entity.ConstructionEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * @author Daniel Tuerk
 */
@ApplicationScoped
public class ConstructionRepository implements PanacheRepository<ConstructionEntity> {

}
