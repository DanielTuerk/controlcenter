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
public class RouteBlockDao extends AbstractDao<RouteBlockPartEntity> {
    private static final Logger LOG = LoggerFactory.getLogger(RouteBlockDao.class);

    @Inject
    public RouteBlockDao(Provider<EntityManager> entityManager) {
        super(entityManager, RouteBlockPartEntity.class);
    }

    public List<RouteBlockPartEntity> listAll() {
        return getEntityManager().createQuery("SELECT x FROM SCENARIO_ROUTE_BLOCK_PART x", RouteBlockPartEntity.class)
                .getResultList();
    }

}
