package io.github.danieltuerk.controlcenter.persist.repository;

import io.github.danieltuerk.controlcenter.persist.entity.TrainEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

/**
 * @author Daniel Tuerk
 */
@ApplicationScoped
public class TrainRepository implements PanacheRepository<TrainEntity> {

    public List<TrainEntity> getTrains() {
        return listAll(Sort.by("name"));
    }

    public Optional<TrainEntity> getTrainById(long trainId) {
        return findByIdOptional(trainId);
    }

}
