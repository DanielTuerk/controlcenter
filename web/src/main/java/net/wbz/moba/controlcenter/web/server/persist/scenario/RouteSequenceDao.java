package net.wbz.moba.controlcenter.web.server.persist.scenario;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.List;
import javax.inject.Provider;
import javax.persistence.EntityManager;
import net.wbz.moba.controlcenter.web.server.persist.AbstractDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Daniel Tuerk
 */
@Singleton
public class RouteSequenceDao extends AbstractDao<RouteSequenceEntity> {

    private static final Logger LOG = LoggerFactory.getLogger(RouteSequenceDao.class);

    @Inject
    public RouteSequenceDao(Provider<EntityManager> entityManager) {
        super(entityManager, RouteSequenceEntity.class);
    }

    public List<RouteSequenceEntity> listAll() {
        return getEntityManager().createQuery("SELECT x FROM SCENARIO_SEQUENCE x", RouteSequenceEntity.class)
            .getResultList();
    }

    public List<RouteSequenceEntity> findByScenario(Long scenarioId) {
        return getEntityManager().createQuery(
            "SELECT x FROM SCENARIO_SEQUENCE x where x.scenario.id = :scenarioId", RouteSequenceEntity.class)
            .setParameter("scenarioId", scenarioId)
            .getResultList();
    }

    public void deleteByScenario(Long scenarioId) {
        getEntityManager().createQuery(
            "DELETE FROM SCENARIO_SEQUENCE x where x.scenario.id = :scenarioId")
            .setParameter("scenarioId", scenarioId)
            .executeUpdate();
    }

    public boolean routeUsedInScenario(Long routeId) {
        return getEntityManager().createQuery(
            "SELECT x.id FROM SCENARIO_SEQUENCE x where x.route.id = :routeId")
            .setParameter("routeId", routeId)
            .getResultList().size() > 0;
    }
}
