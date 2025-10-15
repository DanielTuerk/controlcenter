package net.wbz.moba.controlcenter.persist.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;
import net.wbz.moba.controlcenter.persist.entity.TrainEntity;

/**
 * @author Daniel Tuerk
 */
@ApplicationScoped
public class TrainRepository implements PanacheRepository<TrainEntity> {

    public List<TrainEntity> getTrains() {
        return listAll(Sort.by("name"));
    }

    public Optional<TrainEntity> getTrainByAddress(int address) {
        return find("address=?1", address).firstResultOptional();
    }

    public Optional<TrainEntity> getTrainById(long trainId) {
        return findByIdOptional(trainId);
    }

}
