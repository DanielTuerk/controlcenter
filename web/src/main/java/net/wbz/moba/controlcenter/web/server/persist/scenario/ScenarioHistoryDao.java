package net.wbz.moba.controlcenter.web.server.persist.scenario;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.List;
import java.util.Optional;
import javax.inject.Provider;
import javax.persistence.EntityManager;
import net.wbz.moba.controlcenter.web.server.persist.AbstractDao;
import net.wbz.moba.controlcenter.web.shared.scenario.ScenarioStatistic;
import org.hibernate.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Daniel Tuerk
 */
@Singleton
public class ScenarioHistoryDao extends AbstractDao<ScenarioHistoryEntity> {

    private static final Logger LOG = LoggerFactory.getLogger(ScenarioHistoryDao.class);
    public static final String QUERY =
        "SELECT x.scenario.id, count(x.id), max( x.endDateTime), avg(x.elapsedTimeMillis) FROM SCENARIO_HISTORY x"
            + " GROUP BY x.scenario.id";

    private final ScenarioStatisticResultTransformer resultTransformer;

    @Inject
    public ScenarioHistoryDao(Provider<EntityManager> entityManager,
        ScenarioStatisticResultTransformer resultTransformer) {
        super(entityManager, ScenarioHistoryEntity.class);
        this.resultTransformer = resultTransformer;
    }

    public Optional<ScenarioStatistic> listByScenario(long scenarioId) {
        return Optional.ofNullable((ScenarioStatistic)
            getEntityManager().createQuery(QUERY + " HAVING x.scenario.id = :scenarioId")
                .setParameter("scenarioId", scenarioId)
                .unwrap(Query.class)
                .setResultTransformer(resultTransformer)
                .uniqueResult());
    }

    public List<ScenarioStatistic> loadAll() {
        return (List<ScenarioStatistic>) createQuery(QUERY).list();
    }

    public void deleteByScenario(Long scenarioId) {
        getEntityManager().createQuery(
            "DELETE FROM SCENARIO_HISTORY x where x.scenario.id = :scenarioId")
            .setParameter("scenarioId", scenarioId)
            .executeUpdate();
    }

    private Query createQuery(String query) {
        return getEntityManager()
            .createQuery(QUERY)
            .unwrap(Query.class)
            .setResultTransformer(resultTransformer);

    }

}
