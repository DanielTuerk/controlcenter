package io.github.danieltuerk.controlcenter.persist.repository;

import io.github.danieltuerk.controlcenter.persist.entity.RouteSequenceEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

/**
 * @author Daniel Tuerk
 */
@ApplicationScoped
public class RouteSequenceRepository implements PanacheRepository<RouteSequenceEntity> {

    public void deleteByScenario(Long scenarioId) {
        delete("scenario.id=?1", scenarioId);
    }

    public boolean routeUsedInScenario(Long routeId) {
        return !list("routeId=?1", routeId).isEmpty();
    }

    public List<RouteSequenceEntity> findByScenario(Long scenarioId) {
        return list("scenario.id=?1", scenarioId);
    }
}
