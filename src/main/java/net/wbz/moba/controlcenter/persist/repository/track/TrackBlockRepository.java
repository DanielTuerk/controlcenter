package net.wbz.moba.controlcenter.persist.repository.track;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import net.wbz.moba.controlcenter.persist.entity.track.TrackBlockEntity;

/**
 * @author Daniel Tuerk
 */
@ApplicationScoped
public class TrackBlockRepository implements PanacheRepository<TrackBlockEntity> {

    public List<TrackBlockEntity> findByConstructionId(Long constructionId) {
        return list("construction.id=?1", constructionId);
    }

}
