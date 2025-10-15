package net.wbz.moba.controlcenter.persist.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.wbz.moba.controlcenter.persist.entity.ScenarioHistoryEntity;
import net.wbz.moba.controlcenter.shared.scenario.ScenarioStatistic;

/**
 * TODO: foo foo foo, not working anymore on rasperry; maybe bug also local, but queries to fast
 * TODO: migrate
 * @author Daniel Tuerk
 */
@ApplicationScoped
public class ScenarioHistoryDao implements PanacheRepository<ScenarioHistoryEntity> {

    public static final String QUERY =
        "SELECT x.scenario.id, count(x.id), max( x.endDateTime), avg(x.elapsedTimeMillis) FROM SCENARIO_HISTORY x"
            + " GROUP BY x.scenario.id";

    public Optional<ScenarioStatistic> listByScenario(long scenarioId) {
        return Optional.empty();
//        return Optional.ofNullable((ScenarioStatistic)
//            getEntityManager().createQuery(QUERY + " HAVING x.scenario.id = :scenarioId")
//                .setParameter("scenarioId", scenarioId)
//                .unwrap(Query.class)
//                .setResultTransformer(resultTransformer)
//                .uniqueResult());
    }

    public List<ScenarioStatistic> loadAll() {
        return new ArrayList<>();
        // TODO, just disabled to avoid the derby error
//        return (List<ScenarioStatistic>) createQuery(QUERY).list();
    }

    public void deleteByScenario(Long scenarioId) {
//        getEntityManager().createQuery(
//            "DELETE FROM SCENARIO_HISTORY x where x.scenario.id = :scenarioId")
//            .setParameter("scenarioId", scenarioId)
//            .executeUpdate();
    }

//    private Query createQuery(String query) {
//        return getEntityManager()
//            .createQuery(QUERY)
//            .unwrap(Query.class)
//            .setResultTransformer(resultTransformer);
//    }

}
