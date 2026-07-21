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

    /**
     * Deletes the oldest entries exceeding {@code maxEntries} per scenario, keeping the most recent ones for
     * every scenario. Runs as a single bulk delete in the database (using a window function to rank entries
     * per scenario), without loading any rows into memory or issuing one query per scenario.
     */
    public long deleteOldestExceedingPerScenario(int maxEntries) {
        return getEntityManager()
            .createNativeQuery("""
                delete from SCENARIO_HISTORY
                where id in (
                    select id from (
                        select id, row_number() over (partition by scenario_id order by startDateTime desc) as rn
                        from SCENARIO_HISTORY
                    ) ranked
                    where rn > ?1
                )
                """)
            .setParameter(1, maxEntries)
            .executeUpdate();
    }

}
