package net.wbz.moba.controlcenter.persist.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import net.wbz.moba.controlcenter.persist.entity.ScenarioHistoryEntity;

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
