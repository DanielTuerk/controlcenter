package net.wbz.moba.controlcenter.web.server.persist.construction;

import java.util.List;

import javax.inject.Provider;
import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;

import net.wbz.moba.controlcenter.web.server.persist.AbstractDao;

/**
 * @author Daniel Tuerk
 */
@Singleton
public class ConstructionDao extends AbstractDao<ConstructionEntity> {
    private static final Logger LOG = LoggerFactory.getLogger(ConstructionDao.class);

    @Inject
    public ConstructionDao(Provider<EntityManager> entityManager) {
        super(entityManager, ConstructionEntity.class);
    }

    @Transactional
    public synchronized List<ConstructionEntity> listConstructions() {
        LOG.info("load constructions");
        return getEntityManager().createQuery("SELECT x FROM CONSTRUCTION x", ConstructionEntity.class).getResultList();
    }

}
