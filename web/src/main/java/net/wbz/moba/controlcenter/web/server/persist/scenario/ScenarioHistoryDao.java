package net.wbz.moba.controlcenter.web.server.persist.scenario;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.inject.Provider;
import javax.persistence.EntityManager;
import net.wbz.moba.controlcenter.web.server.persist.AbstractDao;
import net.wbz.moba.controlcenter.web.shared.scenario.Scenario;
import net.wbz.moba.controlcenter.web.shared.scenario.ScenarioStatistic;
import org.hibernate.Query;
import org.hibernate.transform.ResultTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Daniel Tuerk
 */
@Singleton
public class ScenarioHistoryDao extends AbstractDao<ScenarioHistoryEntity> {
    private static final Logger LOG = LoggerFactory.getLogger(ScenarioHistoryDao.class);
    public static final String QUERY = "SELECT x.scenario.id, count(x.id), max( x.endDate) FROM SCENARIO_HISTORY x"
        + " GROUP BY x.scenario";

    @Inject
    public ScenarioHistoryDao(Provider<EntityManager> entityManager) {
        super(entityManager, ScenarioHistoryEntity.class);
    }

    public Optional<ScenarioStatistic> listByScenario(long scenarioId) {
        return Optional.ofNullable((ScenarioStatistic)
            createQuery(QUERY + " HAVING x.scenario.id = :scenarioId").setParameter("scenarioId", scenarioId)
                .uniqueResult());
    }

    public List<ScenarioStatistic> loadAll() {
        return (List<ScenarioStatistic>) createQuery(QUERY).list();
    }

    private Query createQuery(String query) {
        return getEntityManager()
            .createQuery(QUERY)
            .unwrap(Query.class)
            .setResultTransformer(new Transformer());

    }

    private final class Transformer implements ResultTransformer {

        @Override
        public Object transformTuple(Object[] objects, String[] strings) {
            ScenarioStatistic scenarioStatistic = new ScenarioStatistic();
            scenarioStatistic.setScenario((Scenario) objects[0]);
            scenarioStatistic.setRuns((Integer) objects[1]);
            scenarioStatistic.setLastRun((Date) objects[2]);
            return scenarioStatistic;
        }

        @Override
        public List transformList(List list) {
            return list;
        }
    }
}
