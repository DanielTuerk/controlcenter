package net.wbz.moba.controlcenter.service.scenario;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.wbz.moba.controlcenter.service.scenario.statistic.ScenarioStatisticDataProvider;
import net.wbz.moba.controlcenter.shared.scenario.ScenarioStateEvent;
import net.wbz.moba.controlcenter.shared.scenario.ScenarioStatistic;
import net.wbz.moba.controlcenter.shared.scenario.ScenarioStatisticRun;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Daniel Tuerk
 */
@Slf4j
@ApplicationScoped
public class ScenarioStatisticManager {

    private final Map<Long, LocalDateTime> startedScenarios = new ConcurrentHashMap<>();

    private final ScenarioStatisticDataProvider statisticDataProvider;

    @Inject
    public ScenarioStatisticManager(ScenarioStatisticDataProvider statisticDataProvider) {
        this.statisticDataProvider = statisticDataProvider;
    }

    public void onScenarioStateEvent(@Observes ScenarioStateEvent event) {
        log.info("scenario state changed for ID: {} to {}", event.getItemId(), event.getState());

        switch (event.getState()) {
            case RUNNING:
                startedScenarios.put(event.getItemId(), LocalDateTime.now());
                break;
            case FAILED:
                addRun(event, ScenarioStatisticRun.STATE.FAILED);
                break;
            case SUCCESS:
                addRun(event, ScenarioStatisticRun.STATE.SUCCESS);
                break;
            case NONE:
            case SCHEDULED:
            case PAUSED:
            case STOPPED:
        }
    }

    public Optional<ScenarioStatistic> load(long scenarioId) {
        return statisticDataProvider.fetch(scenarioId);
    }

    public void deleteEntriesOfScenario(Long scenarioId) {
        statisticDataProvider.deleteByScenario(scenarioId);
    }

    private void addRun(ScenarioStateEvent event, ScenarioStatisticRun.STATE state) {
        if (!startedScenarios.containsKey(event.getItemId())) {
            throw new RuntimeException("scenario not found for ID: " + event.getItemId());
        }
        statisticDataProvider.addRun(event.getItemId(),
                startedScenarios.get(event.getItemId()),
                LocalDateTime.now(),
                state);

        startedScenarios.remove(event.getItemId());
    }
}
