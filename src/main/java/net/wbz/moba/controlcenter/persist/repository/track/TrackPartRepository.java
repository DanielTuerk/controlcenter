package net.wbz.moba.controlcenter.persist.repository.track;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import net.wbz.moba.controlcenter.persist.entity.track.AbstractTrackPartEntity;

/**
 * @author Daniel Tuerk
 */
@ApplicationScoped
public class TrackPartRepository implements PanacheRepository<AbstractTrackPartEntity> {


    public List<AbstractTrackPartEntity> findByConstructionId(Long constructionId) {
        // TODO migrate
        return getEntityManager().createQuery("SELECT t FROM TRACK_PART t" + " WHERE t.construction.id = :construction",
            AbstractTrackPartEntity.class).setParameter("construction", constructionId).getResultList();
    }

    public List<AbstractTrackPartEntity> findByBlockId(Long blockId) {
        // TODO migrate
        return getEntityManager().createQuery("SELECT t FROM TRACK_PART t" + " WHERE t.trackBlock.id = :blockId",
            AbstractTrackPartEntity.class).setParameter("blockId", blockId).getResultList();
    }

}
