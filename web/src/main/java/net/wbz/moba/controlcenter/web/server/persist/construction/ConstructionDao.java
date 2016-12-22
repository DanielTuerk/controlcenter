package net.wbz.moba.controlcenter.web.server.persist.construction;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;
import net.wbz.moba.controlcenter.web.server.persist.AbstractDao;
import net.wbz.moba.controlcenter.web.server.persist.device.DeviceInfoEntity;
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
public class ConstructionDao extends AbstractDao<ConstructionEntity> {
    private static final Logger LOG = LoggerFactory.getLogger(ConstructionDao.class);

    @Inject
    public ConstructionDao(Provider<EntityManager> entityManager) {
        super(entityManager);
    }

    @Transactional
    public synchronized List<ConstructionEntity> listConstructions() {
        LOG.info("load constructions");
        Query typedQuery = getEntityManager().createQuery(
                "SELECT x FROM Construction x");
        List<ConstructionEntity> resultList = typedQuery.getResultList();
        return resultList;
    }

    @Override
    public ConstructionEntity getById(Long id) {
        return (ConstructionEntity) getEntityManager().createQuery("select x  FROM Construction WHERE id = :id")
                .setParameter("id", id).getSingleResult();
    }
}
