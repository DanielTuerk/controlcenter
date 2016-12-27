package net.wbz.moba.controlcenter.web.server.persist.construction.track;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.wbz.moba.controlcenter.web.server.persist.AbstractDao;
import net.wbz.moba.controlcenter.web.shared.constrution.Construction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Provider;
import javax.persistence.EntityManager;
import java.util.List;

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
        return  getEntityManager().createQuery("SELECT t FROM track_part t"
                        +" WHERE t.construction.id = :construction",
                AbstractTrackPartEntity.class)
                .setParameter("construction", constructionId)
                .getResultList();
    }

}
