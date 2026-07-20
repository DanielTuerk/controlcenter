package io.github.danieltuerk.controlcenter.persist.repository;

import io.github.danieltuerk.controlcenter.persist.entity.ScenarioHistoryEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

/**
 * @author Daniel Tuerk
 */
@ApplicationScoped
public class ScenarioHistoryRepository implements PanacheRepository<ScenarioHistoryEntity> {

    public List<ScenarioHistoryEntity> listByScenario(long scenarioId) {
        return list("scenarioId=?1", Sort.by("startDateTime"), scenarioId);
    }

    public long deleteByScenario(Long scenarioId) {
        return delete("scenarioId=?1", scenarioId);
    }

}
