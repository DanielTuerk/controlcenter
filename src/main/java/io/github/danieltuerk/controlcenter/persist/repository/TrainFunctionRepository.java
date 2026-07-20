package io.github.danieltuerk.controlcenter.persist.repository;

import io.github.danieltuerk.controlcenter.persist.entity.TrainFunctionEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * @author Daniel Tuerk
 */
@ApplicationScoped
public class TrainFunctionRepository implements PanacheRepository<TrainFunctionEntity> {

}
