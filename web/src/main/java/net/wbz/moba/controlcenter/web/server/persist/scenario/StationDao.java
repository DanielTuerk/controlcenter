package net.wbz.moba.controlcenter.web.server.persist.scenario;

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
public class StationDao extends AbstractDao<StationEntity> {
    private static final Logger LOG = LoggerFactory.getLogger(StationDao.class);

    @Inject
    public StationDao(Provider<EntityManager> entityManager) {
        super(entityManager, StationEntity.class);
    }

    public List<StationEntity> listAll() {
        return getEntityManager().createQuery("SELECT x FROM STATION x", StationEntity.class).getResultList();
    }

}
