package io.github.danieltuerk.controlcenter.service.scenario.statistic;

import io.github.danieltuerk.controlcenter.service.config.ConfigService;
import io.github.danieltuerk.controlcenter.shared.scenario.ScenarioStateEvent;
import io.github.danieltuerk.controlcenter.shared.scenario.ScenarioStatistic;
import io.github.danieltuerk.controlcenter.shared.scenario.ScenarioStatisticRun;
import io.quarkus.runtime.Startup;
import io.quarkus.runtime.StartupEvent;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author Daniel Tuerk
 */
@Slf4j
@ApplicationScoped
@Startup
public class ScenarioStatisticManager {

    private final Map<Long, LocalDateTime> startedScenarios = new ConcurrentHashMap<>();

    private final ScenarioStatisticDataProvider statisticDataProvider;
    private final ConfigService configService;

    @Inject
    public ScenarioStatisticManager(ScenarioStatisticDataProvider statisticDataProvider, ConfigService configService) {
        this.statisticDataProvider = statisticDataProvider;
        this.configService = configService;
    }

    void onStart(@Observes StartupEvent event) {
        cleanupOldEntries();
    }

    @Scheduled(cron = "0 0 3 * * ?")
    void cleanupOldEntries() {
        final var maxEntriesPerScenario = configService.getScenarioStatisticMaxEntriesPerScenario();
        log.debug("cleanup scenario statistic history, keeping {} entries per scenario", maxEntriesPerScenario);
        statisticDataProvider.deleteOldestEntriesExceeding(maxEntriesPerScenario);
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

    public long averageExecutionTimeInMs(Long scenarioId) {
        return statisticDataProvider.fetch(scenarioId)
                .map(ScenarioStatistic::averageRunTimeInMillis)
                .orElse(TimeUnit.MINUTES.toMillis(2));
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
