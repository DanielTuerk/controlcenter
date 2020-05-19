package net.wbz.moba.controlcenter.web.server.web.scenario;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;
import java.util.Collection;
import java.util.Optional;
import net.wbz.moba.controlcenter.web.server.persist.scenario.ScenarioEntity;
import net.wbz.moba.controlcenter.web.server.persist.scenario.ScenarioHistoryDao;
import net.wbz.moba.controlcenter.web.server.persist.scenario.ScenarioHistoryEntity;
import net.wbz.moba.controlcenter.web.server.web.DataMapper;
import net.wbz.moba.controlcenter.web.shared.scenario.Scenario;
import net.wbz.moba.controlcenter.web.shared.scenario.ScenarioStatistic;
import org.joda.time.DateTime;
import org.joda.time.Interval;

/**
 * @author Daniel Tuerk
 */
@Singleton
public class ScenarioStatisticManager {

    private final ScenarioHistoryDao scenarioHistoryDao;
    private final DataMapper<Scenario, ScenarioEntity> scenarioEntityDataMapper = new DataMapper<>(Scenario.class,
        ScenarioEntity.class);

    @Inject
    public ScenarioStatisticManager(ScenarioHistoryDao scenarioHistoryDao) {
        this.scenarioHistoryDao = scenarioHistoryDao;
    }

    public Optional<ScenarioStatistic> load(long scenarioId) {
        return scenarioHistoryDao.listByScenario(scenarioId);
    }

    public Collection<ScenarioStatistic> loadAll() {
        return scenarioHistoryDao.loadAll();
    }

    @Transactional
    public void createHistoryEntry(Scenario scenario, DateTime startDate, DateTime endDate) {
        ScenarioHistoryEntity entity = new ScenarioHistoryEntity();
        entity.setScenario(scenarioEntityDataMapper.transformTarget(scenario));
        entity.setStartDate(startDate);
        entity.setEndDate(endDate);
        entity.setElapsedTime(new Interval(startDate, endDate).toDurationMillis());
        scenarioHistoryDao.create(entity);
    }
}
