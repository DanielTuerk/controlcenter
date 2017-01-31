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
public class TrackBlockDao extends AbstractDao<TrackBlockEntity> {

    private static final Logger LOG = LoggerFactory.getLogger(TrackBlockDao.class);

    @Inject
    public TrackBlockDao(Provider<EntityManager> entityManager) {
        super(entityManager, TrackBlockEntity.class);
    }

    public List<TrackBlockEntity> findByConstructionId(Long constructionId) {
        return getEntityManager().createQuery("SELECT b FROM TRACK_BLOCK b"
                + " WHERE b.construction.id =:constructionId",
                TrackBlockEntity.class)
                .setParameter("constructionId", constructionId)
                .getResultList();
    }

}
