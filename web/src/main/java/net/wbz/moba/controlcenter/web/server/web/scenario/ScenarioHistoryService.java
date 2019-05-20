package net.wbz.moba.controlcenter.web.server.web.scenario;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.wbz.moba.controlcenter.web.server.persist.scenario.ScenarioEntity;
import net.wbz.moba.controlcenter.web.server.persist.scenario.ScenarioHistoryDao;
import net.wbz.moba.controlcenter.web.server.persist.scenario.ScenarioHistoryEntity;
import net.wbz.moba.controlcenter.web.server.web.DataMapper;
import net.wbz.moba.controlcenter.web.server.web.scenario.execution.ScenarioExecutor;
import net.wbz.moba.controlcenter.web.shared.scenario.Scenario;
import org.joda.time.DateTime;
import org.joda.time.Interval;

/**
 * Service to create the history entries for the {@link Scenario} executions. It record the time between start and
 * finish.
 *
 * @author Daniel Tuerk
 */
@Singleton
public class ScenarioHistoryService {

    private final ScenarioHistoryDao scenarioHistoryDao;
    private final Map<Long, DateTime> scenarioStartDateTimes = new ConcurrentHashMap<>();
    private final DataMapper<Scenario, ScenarioEntity> scenarioEntityDataMapper = new DataMapper<>(Scenario.class,
        ScenarioEntity.class);
    private final ScenarioExecutor scenarioExecutor;

    @Inject
    public ScenarioHistoryService(final ScenarioHistoryDao scenarioHistoryDao,
        ScenarioExecutor scenarioExecutor) {
        this.scenarioHistoryDao = scenarioHistoryDao;
        this.scenarioExecutor = scenarioExecutor;

        scenarioExecutor.addScenarioStateListener(new ScenarioStateListener() {
            @Override
            public void scenarioStarted(Scenario scenario) {
                scenarioStartDateTimes.put(scenario.getId(), DateTime.now());
            }

            @Override
            public void scenarioStopped(Scenario scenario) {
                scenarioStartDateTimes.remove(scenario.getId());
            }

            @Override
            @Transactional
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

            @Override
            public void scenarioPaused(Scenario scenario) {
            }
        });
    }


    private void createHistoryEntry(Scenario scenario, DateTime startDate) {
        ScenarioHistoryEntity entity = new ScenarioHistoryEntity();
        entity.setScenario(scenarioEntityDataMapper.transformTarget(scenario));
        entity.setStartDate(startDate);
        DateTime endDate = DateTime.now();
        entity.setEndDate(endDate);
        entity.setElapsedTime(new Interval(startDate, endDate).toDurationMillis());
        scenarioHistoryDao.create(entity);
    }
}
