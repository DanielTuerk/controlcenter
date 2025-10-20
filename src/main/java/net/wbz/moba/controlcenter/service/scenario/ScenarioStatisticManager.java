package net.wbz.moba.controlcenter.service.scenario;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;
import net.wbz.moba.controlcenter.persist.entity.ScenarioHistoryEntity;
import net.wbz.moba.controlcenter.persist.repository.ScenarioHistoryRepository;
import net.wbz.moba.controlcenter.persist.repository.ScenarioRepository;
import net.wbz.moba.controlcenter.shared.scenario.ScenarioStatistic;

/**
 * @author Daniel Tuerk
 */
@ApplicationScoped
public class ScenarioStatisticManager {

    private final ScenarioHistoryRepository scenarioHistoryRepository;
    private final ScenarioRepository scenarioRepository;

    @Inject
    public ScenarioStatisticManager(ScenarioHistoryRepository scenarioHistoryRepository,
        ScenarioRepository scenarioRepository) {
        this.scenarioHistoryRepository = scenarioHistoryRepository;
        this.scenarioRepository = scenarioRepository;
    }

    public Optional<ScenarioStatistic> load(long scenarioId) {
        return scenarioHistoryRepository.listByScenario(scenarioId);
    }

    public Collection<ScenarioStatistic> loadAll() {
        return scenarioHistoryRepository.loadAll()
            .stream()
            .sorted(Comparator.comparing(x -> x.getScenario().getName()))
            .collect(Collectors.toList());
    }

    @Transactional
    public void createHistoryEntry(Long scenarioId, LocalDateTime startDate, LocalDateTime endDate) {
        ScenarioHistoryEntity entity = new ScenarioHistoryEntity();
        entity.scenario = scenarioRepository.findById(scenarioId);
        entity.startDateTime = startDate;
        entity.endDateTime = endDate;
        entity.elapsedTimeMillis = ChronoUnit.MILLIS.between(startDate, endDate);
        scenarioHistoryRepository.persist(entity);
    }

    @Transactional
    public void deleteEntriesOfScenario(Long scenarioId) {
        scenarioHistoryRepository.deleteByScenario(scenarioId);
    }
}
