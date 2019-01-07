package net.wbz.moba.controlcenter.web.server.persist.construction.track;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.List;
import javax.inject.Provider;
import javax.persistence.EntityManager;
import net.wbz.moba.controlcenter.web.server.persist.AbstractDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Daniel Tuerk
 */
@Singleton
public class TrackPartDao extends AbstractDao<AbstractTrackPartEntity> {

    private static final Logger LOG = LoggerFactory.getLogger(TrackPartDao.class);

    @Inject
    public TrackPartDao(Provider<EntityManager> entityManager) {
        super(entityManager, AbstractTrackPartEntity.class);
    }

    public List<AbstractTrackPartEntity> findByConstructionId(Long constructionId) {
        return getEntityManager().createQuery("SELECT t FROM TRACK_PART t" + " WHERE t.construction.id = :construction",
            AbstractTrackPartEntity.class).setParameter("construction", constructionId).getResultList();
    }

    public List<AbstractTrackPartEntity> findByBlockId(Long blockId) {
        return getEntityManager().createQuery("SELECT t FROM TRACK_PART t" + " WHERE t.trackBlock.id = :blockId",
            AbstractTrackPartEntity.class).setParameter("blockId", blockId).getResultList();
    }

    public boolean deleteById(AbstractTrackPartEntity id) {
        // TODO gridpos delete by id
        return getEntityManager().createQuery("DELETE FROM TRACK_PART t WHERE t.id = :id").setParameter("id", id)
            .executeUpdate() == 1;
    }
}
