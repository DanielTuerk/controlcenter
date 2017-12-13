package net.wbz.moba.controlcenter.web.server.web.scenario;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.joda.time.DateTime;
import org.joda.time.Interval;

import com.google.inject.Inject;
import com.google.inject.persist.Transactional;

import net.wbz.moba.controlcenter.web.server.persist.scenario.ScenarioEntity;
import net.wbz.moba.controlcenter.web.server.persist.scenario.ScenarioHistoryDao;
import net.wbz.moba.controlcenter.web.server.persist.scenario.ScenarioHistoryEntity;
import net.wbz.moba.controlcenter.web.server.web.DataMapper;
import net.wbz.moba.controlcenter.web.shared.scenario.Scenario;

/**
 * Service to create the history entries for the {@link Scenario} executions.
 * It record the time between start and finish.
 *
 * @author Daniel Tuerk
 */
public class ScenarioHistoryService implements ScenarioStateListener {

    private final ScenarioHistoryDao scenarioHistoryDao;
    private final Map<Long, DateTime> scenarioStartDateTimes = new ConcurrentHashMap<>();
    private final DataMapper<Scenario, ScenarioEntity> scenarioEntityDataMapper = new DataMapper<>(Scenario.class,
            ScenarioEntity.class);

    @Inject
    public ScenarioHistoryService(final ScenarioHistoryDao scenarioHistoryDao) {
        this.scenarioHistoryDao = scenarioHistoryDao;
    }

    @Override
    public void scenarioStarted(Scenario scenario) {
        scenarioStartDateTimes.put(scenario.getId(), DateTime.now());
    }

    @Override
    public void scenarioStopped(Scenario scenario) {
        scenarioStartDateTimes.remove(scenario.getId());
    }

    @Override
    public void scenarioFinished(Scenario scenario) {
        Long scenarioId = scenario.getId();
        if (scenarioStartDateTimes.containsKey(scenarioId)) {
            DateTime startDate = scenarioStartDateTimes.get(scenarioId);
            createHistoryEntry(scenario, startDate);
        }
    }

    @Override
    public void scenarioQueued(Scenario scenario) {
    }

    @Transactional
    private void createHistoryEntry(Scenario scenario, DateTime startDate) {
        ScenarioHistoryEntity entity = new ScenarioHistoryEntity();
        entity.setScenario(scenarioEntityDataMapper.transformTarget(scenario));
        entity.setStartDate(DateTime.now());
        DateTime endDate = DateTime.now();
        entity.setEndDate(endDate);
        entity.setElapsedTime(new Interval(startDate, endDate).toDurationMillis());
        scenarioHistoryDao.create(entity);
    }
}
