package net.wbz.moba.controlcenter.web.server.persist.construction.track;

import java.util.List;

import javax.inject.Provider;
import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import net.wbz.moba.controlcenter.web.server.persist.AbstractDao;

/**
 * @author Daniel Tuerk
 */
@Singleton
public class TrackPartDao extends AbstractDao<AbstractTrackPartEntity> {
    private static final Logger LOG = LoggerFactory.getLogger(TrackPartDao.class);

    private final GridPositionDao gridPositionDao;

    @Inject
    public TrackPartDao(Provider<EntityManager> entityManager, GridPositionDao gridPositionDao) {
        super(entityManager, AbstractTrackPartEntity.class);
        this.gridPositionDao = gridPositionDao;
    }

    public List<AbstractTrackPartEntity> findByConstructionId(Long constructionId) {
        return getEntityManager().createQuery("SELECT t FROM TRACK_PART t"
                + " WHERE t.construction.id = :construction",
                AbstractTrackPartEntity.class)
                .setParameter("construction", constructionId)
                .getResultList();
    }

    public List<AbstractTrackPartEntity> findByBlockId(Long blockId) {
        return getEntityManager().createQuery("SELECT t FROM TRACK_PART t"
                + " WHERE t.trackBlock.id = :blockId", AbstractTrackPartEntity.class)
                .setParameter("blockId", blockId)
                .getResultList();
    }

    public boolean deleteById(AbstractTrackPartEntity id) {
        // TODO gridpos delete by id
        return getEntityManager().createQuery("DELETE FROM TRACK_PART t WHERE t.id = :id")
                .setParameter("id", id)
                .executeUpdate() == 1;
    }
}
