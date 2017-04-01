package net.wbz.moba.controlcenter.web.server.web.scenario;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.base.Stopwatch;
import com.google.inject.Inject;

import net.wbz.moba.controlcenter.web.server.persist.scenario.ScenarioEntity;
import net.wbz.moba.controlcenter.web.server.persist.scenario.ScenarioHistoryDao;
import net.wbz.moba.controlcenter.web.server.persist.scenario.ScenarioHistoryEntity;
import net.wbz.moba.controlcenter.web.server.web.DataMapper;
import net.wbz.moba.controlcenter.web.shared.scenario.Scenario;
import net.wbz.moba.controlcenter.web.shared.scenario.Scenario.RUN_STATE;
import org.joda.time.DateTime;

/**
 * @author Daniel Tuerk
 */
public class ScenarioHistoryService implements ScenarioStateListener {

    private final ScenarioHistoryDao scenarioHistoryDao;
    private final Map<Scenario, Stopwatch> scenarioStopwatch = new ConcurrentHashMap<>();
    private final DataMapper<Scenario, ScenarioEntity> scenarioEntityDataMapper = new DataMapper<>(Scenario.class,
            ScenarioEntity.class);

    @Inject
    public ScenarioHistoryService(final ScenarioHistoryDao scenarioHistoryDao) {
        this.scenarioHistoryDao = scenarioHistoryDao;
    }

    @Override
    public void scenarioStarted(Scenario scenario) {
        scenarioStopwatch.put(scenario, new Stopwatch().start());
    }

    @Override
    public void scenarioStopped(Scenario scenario) {
        if (scenarioStopwatch.containsKey(scenario)) {
            Stopwatch stopwatch = scenarioStopwatch.get(scenario).stop();
            if (scenario.getRunState() == RUN_STATE.IDLE) {
                ScenarioHistoryEntity entity = new ScenarioHistoryEntity();
                entity.setScenario(scenarioEntityDataMapper.transformTarget(scenario));
                entity.setRunDate(DateTime.now());
                entity.setElapsedTime(stopwatch.elapsedMillis());
                scenarioHistoryDao.create(entity);
            }
        }
    }
}
