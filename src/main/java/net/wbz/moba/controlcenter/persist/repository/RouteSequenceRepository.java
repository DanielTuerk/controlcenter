package net.wbz.moba.controlcenter.persist.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import net.wbz.moba.controlcenter.persist.entity.RouteSequenceEntity;

/**
 * @author Daniel Tuerk
 */
@ApplicationScoped
public class RouteSequenceRepository implements PanacheRepository<RouteSequenceEntity> {

    public List<RouteSequenceEntity> findByScenario(Long scenarioId) {
        // TODO
        return list("scenario.id=?1", scenarioId);
//        return getEntityManager().createQuery(
//            "SELECT x FROM SCENARIO_SEQUENCE x where x.scenario.id = :scenarioId", RouteSequenceEntity.class)
//            .setParameter("scenarioId", scenarioId)
//            .getResultList();
    }

    public void deleteByScenario(Long scenarioId) {
        // TODO migrate
        getEntityManager().createQuery(
            "DELETE FROM SCENARIO_SEQUENCE x where x.scenario.id = :scenarioId")
            .setParameter("scenarioId", scenarioId)
            .executeUpdate();
    }

    public boolean routeUsedInScenario(Long routeId) {
        // TODO migrate
        return getEntityManager().createQuery(
            "SELECT x.id FROM SCENARIO_SEQUENCE x where x.route.id = :routeId")
            .setParameter("routeId", routeId)
            .getResultList().size() > 0;
    }
}
