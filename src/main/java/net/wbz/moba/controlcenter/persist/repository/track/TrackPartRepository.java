package net.wbz.moba.controlcenter.persist.repository.track;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import net.wbz.moba.controlcenter.persist.entity.track.AbstractTrackPartEntity;

import java.util.List;

/**
 * @author Daniel Tuerk
 */
@ApplicationScoped
public class TrackPartRepository implements PanacheRepository<AbstractTrackPartEntity> {

    public List<AbstractTrackPartEntity> findByConstructionId(Long constructionId) {
        return find("constructionId=?1", constructionId).stream().toList();
    }

//    public Optional<AbstractTrackPartEntity> findByGridPositionId(long gridPositionId) {
//        return find("gridPosition.id=?1", gridPositionId).firstResultOptional();
//    }

}
