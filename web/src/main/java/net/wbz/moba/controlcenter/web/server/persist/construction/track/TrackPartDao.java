package net.wbz.moba.controlcenter.web.server.persist.construction.track;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;
import net.wbz.moba.controlcenter.web.server.persist.AbstractDao;
import net.wbz.moba.controlcenter.web.server.persist.construction.ConstructionEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Provider;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

/**
 * @author Daniel Tuerk
 */
@Singleton
public class TrackPartDao extends AbstractDao<TrackPartEntity> {
    private static final Logger LOG = LoggerFactory.getLogger(TrackPartDao.class);
    @Inject
    public TrackPartDao(Provider<EntityManager> entityManager) {
        super(entityManager);
    }

    public List<ConstructionEntity> listAll() {
        Query typedQuery = getEntityManager().createQuery(
                "SELECT x FROM TrackPartEntity x");
        List<ConstructionEntity> resultList = typedQuery.getResultList();
        return resultList;
    }

    @Override
    public TrackPartEntity getById(Long id) {
        return (TrackPartEntity) getEntityManager().createQuery("select x  FROM TrackPartEntity WHERE id = :id")
                .setParameter("id", id).getSingleResult();
    }
}
