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
public class RouteDao extends AbstractDao<RouteEntity> {
    private static final Logger LOG = LoggerFactory.getLogger(RouteDao.class);

    @Inject
    public RouteDao(Provider<EntityManager> entityManager) {
        super(entityManager, RouteEntity.class);
    }

    public List<RouteEntity> listAll() {
        return getEntityManager().createQuery("SELECT x FROM SCENARIO_ROUTE x", RouteEntity.class).getResultList();
    }

}
