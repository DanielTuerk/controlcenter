package net.wbz.moba.controlcenter.service.scenario;

import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.wbz.moba.controlcenter.service.scenario.execution.ScenarioExecutor;
import net.wbz.moba.controlcenter.shared.scenario.Scenario;
import net.wbz.moba.controlcenter.shared.scenario.ScenarioStatistic;

/**
 *
 * @author Daniel Tuerk
 */
@ApplicationScoped
public class ScenarioStatisticService {

    private final ScenarioStatisticManager scenarioStatisticManager;

    @jakarta.inject.Inject
    public ScenarioStatisticService(ScenarioStatisticManager scenarioStatisticManager,
        ScenarioExecutor scenarioExecutor) {
        this.scenarioStatisticManager = scenarioStatisticManager;

        trackScenarioExecution(scenarioStatisticManager, scenarioExecutor);
    }

    /**
     * Create the history entries for the {@link Scenario} executions. It record the time between start and finish.
     *
     * @param scenarioStatisticManager {@link ScenarioStatisticManager}
     * @param scenarioExecutor {@link ScenarioExecutor}
     */
    private void trackScenarioExecution(ScenarioStatisticManager scenarioStatisticManager,
        ScenarioExecutor scenarioExecutor) {
        scenarioExecutor.addScenarioStateListener(new ScenarioStateListener() {
            private final Map<Long, LocalDateTime> scenarioStartDateTimes = new ConcurrentHashMap<>();

            @Override
            public void scenarioStarted(Scenario scenario) {
                scenarioStartDateTimes.put(scenario.getId(), LocalDateTime.now());
            }

            @Override
            public void scenarioStopped(Scenario scenario) {
                scenarioStartDateTimes.remove(scenario.getId());
            }

            @Override
            public void scenarioExecuteWithError(Scenario scenario) {

            }

            @Override
            public void scenarioSuccessfullyExecuted(Scenario scenario) {
                Long scenarioId = scenario.getId();
                if (scenarioStartDateTimes.containsKey(scenarioId)) {
                    LocalDateTime startDate = scenarioStartDateTimes.get(scenarioId);
                    scenarioStatisticManager.createHistoryEntry(scenarioId, startDate, LocalDateTime.now());
                }
            }

            @Override
            public void scenarioFinished(Scenario scenario) {
                scenarioStartDateTimes.remove(scenario.getId());
            }

            @Override
            public void scenarioQueued(Scenario scenario) {
            }

            @Override
            public void scenarioPaused(Scenario scenario) {
            }
        });
    }

    public ScenarioStatistic load(long scenarioId) {
        return scenarioStatisticManager.load(scenarioId).orElse(null);
    }

    public Collection<ScenarioStatistic> loadAll() {
        return scenarioStatisticManager.loadAll();
    }

}
