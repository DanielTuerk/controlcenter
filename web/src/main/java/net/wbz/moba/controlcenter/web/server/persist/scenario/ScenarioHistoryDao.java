package net.wbz.moba.controlcenter.web.server.persist.scenario;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import javax.inject.Provider;
import javax.persistence.EntityManager;
import net.wbz.moba.controlcenter.web.server.DateUtil;
import net.wbz.moba.controlcenter.web.server.persist.AbstractDao;
import net.wbz.moba.controlcenter.web.server.web.scenario.ScenarioManager;
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
    public static final String QUERY =
        "SELECT x.scenario.id, count(x.id), max( x.endDateTime), avg(x.elapsedTimeMillis) FROM SCENARIO_HISTORY x"
            + " GROUP BY x.scenario.id";

    private final ScenarioManager scenarioManager;

    @Inject
    public ScenarioHistoryDao(Provider<EntityManager> entityManager,
        ScenarioManager scenarioManager) {
        super(entityManager, ScenarioHistoryEntity.class);
        this.scenarioManager = scenarioManager;
    }

    public Optional<ScenarioStatistic> listByScenario(long scenarioId) {
        return Optional.ofNullable((ScenarioStatistic)
            getEntityManager().createQuery(QUERY + " HAVING x.scenario.id = :scenarioId")
                .setParameter("scenarioId", scenarioId)
                .unwrap(Query.class)
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
            scenarioStatistic.setScenario(scenarioManager.getScenarioById((Long) objects[0]));
            scenarioStatistic.setRuns((Long) objects[1]);
            scenarioStatistic.setLastRun(DateUtil.convertToDate((LocalDateTime) objects[2]));
            scenarioStatistic.setAverageRunTimeInMillis((Double) objects[3]);
            return scenarioStatistic;
        }

        @Override
        public List transformList(List list) {
            return list;
        }
    }
}
