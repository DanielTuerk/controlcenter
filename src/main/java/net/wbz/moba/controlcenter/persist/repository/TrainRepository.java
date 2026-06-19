package net.wbz.moba.controlcenter.persist.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import net.wbz.moba.controlcenter.persist.entity.TrainEntity;

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
