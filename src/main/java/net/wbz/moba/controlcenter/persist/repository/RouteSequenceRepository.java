package net.wbz.moba.controlcenter.persist.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import net.wbz.moba.controlcenter.persist.entity.RouteSequenceEntity;

/**
 * @author Daniel Tuerk
 */
@ApplicationScoped
public class RouteSequenceRepository implements PanacheRepository<RouteSequenceEntity> {

    public void deleteByScenario(Long scenarioId) {
        delete("scenario.id=?1", scenarioId);
    }

    public boolean routeUsedInScenario(Long routeId) {
        return !list("route.id=?1", routeId).isEmpty();
    }
}
