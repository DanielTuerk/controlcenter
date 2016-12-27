package net.wbz.moba.controlcenter.web.server.persist.construction;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;
import net.wbz.moba.controlcenter.web.server.persist.AbstractDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Provider;
import javax.persistence.EntityManager;
import java.util.List;

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
        return getEntityManager().createQuery("SELECT x FROM construction x", ConstructionEntity.class).getResultList();
    }

//    @Override
//    @Transactional
//    public ConstructionEntity getById(Long id) {
//        return getEntityManager().createQuery("SELECT x FROM construction x WHERE x.id = :id",ConstructionEntity.class)
//                .setParameter("id", id).getSingleResult();
//    }
}
