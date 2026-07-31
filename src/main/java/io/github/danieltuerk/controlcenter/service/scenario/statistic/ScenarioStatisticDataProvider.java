package io.github.danieltuerk.controlcenter.service.scenario.statistic;

import io.github.danieltuerk.controlcenter.persist.entity.ScenarioHistoryEntity;
import io.github.danieltuerk.controlcenter.persist.repository.ScenarioHistoryRepository;
import io.github.danieltuerk.controlcenter.shared.scenario.ScenarioStatistic;
import io.github.danieltuerk.controlcenter.shared.scenario.ScenarioStatisticRun;
import io.quarkus.cache.CacheInvalidate;
import io.quarkus.cache.CacheInvalidateAll;
import io.quarkus.cache.CacheKey;
import io.quarkus.cache.CacheResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@ApplicationScoped
public class ScenarioStatisticDataProvider {

    private static final String CACHE = "scenarios-cache";
    private final ScenarioHistoryRepository repository;
    private final ScenarioStatisticsMapper mapper;

    public ScenarioStatisticDataProvider(ScenarioHistoryRepository repository, ScenarioStatisticsMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @CacheResult(cacheName = CACHE)
    public Optional<ScenarioStatistic> fetch(@CacheKey long scenarioId) {
        log.debug("load scenario statistics for scenario: {}", scenarioId);
        final var runs = repository.listByScenario(scenarioId).stream()
                .map(mapper::toDto)
                .toList();

        if (runs.isEmpty()) {
            return Optional.empty();
        }

        final var successfulRuns = runsByState(runs, ScenarioStatisticRun.STATE.SUCCESS);
        return Optional.of(new ScenarioStatistic(scenarioId,
                runs.getLast().end(),
                runs.size(),
                runsByState(runs, ScenarioStatisticRun.STATE.FAILED).size(),
                successfulRuns.size(),
                averageRuntime(successfulRuns),
                runs));
    }

    private static Set<ScenarioStatisticRun> runsByState(List<ScenarioStatisticRun> runs,
                                                         ScenarioStatisticRun.STATE success) {
        return runs.stream()
                .filter(x -> x.state() == success)
                .collect(Collectors.toSet());
    }

    private long averageRuntime(Set<ScenarioStatisticRun> successfulRuns) {
        // TODO get rid of min and max values
        // TODO or new field for computedRuntime
        return successfulRuns.stream()
                .map(ScenarioStatisticRun::averageRunTimeInMillis)
                .collect(Collectors.averagingLong(Long::longValue))
                .longValue();
    }

    @CacheInvalidate(cacheName = CACHE)
    @Transactional
    public void addRun(@CacheKey long scenarioId, LocalDateTime startDateTime, LocalDateTime endDateTime,
                       ScenarioStatisticRun.STATE state) {
        final var entity = new ScenarioHistoryEntity();
        entity.scenarioId = scenarioId;
        entity.startDateTime = startDateTime;
        entity.endDateTime = endDateTime;
        entity.elapsedTimeMillis = ChronoUnit.MILLIS.between(startDateTime, endDateTime);
        entity.type = switch (state) {
            case SUCCESS -> ScenarioHistoryEntity.TYPE.SUCCESS;
            case FAILED -> ScenarioHistoryEntity.TYPE.FAILED;
        };
        repository.persist(entity);
    }

    @CacheInvalidate(cacheName = CACHE)
    @Transactional
    public void deleteByScenario(@CacheKey long scenarioId) {
        log.info("history ({} items) for scenario {} cleared", scenarioId,
                repository.deleteByScenario(scenarioId));
    }

    @CacheInvalidateAll(cacheName = CACHE)
    @Transactional
    public void deleteOldestEntriesExceeding(int maxEntriesPerScenario) {
        final var deleted = repository.deleteOldestExceedingPerScenario(maxEntriesPerScenario);
        if (deleted > 0) {
            log.info("cleaned up {} old history entries (keeping {} per scenario)", deleted, maxEntriesPerScenario);
        }
    }
}
