package net.wbz.moba.controlcenter.web.server.web.scenario;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;
import net.wbz.moba.controlcenter.web.server.persist.scenario.ScenarioEntity;
import net.wbz.moba.controlcenter.web.server.persist.scenario.ScenarioHistoryDao;
import net.wbz.moba.controlcenter.web.server.persist.scenario.ScenarioHistoryEntity;
import net.wbz.moba.controlcenter.web.server.web.DataMapper;
import net.wbz.moba.controlcenter.web.shared.scenario.Scenario;
import net.wbz.moba.controlcenter.web.shared.scenario.ScenarioStatistic;

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
        return scenarioHistoryDao.loadAll()
            .stream()
            .sorted(Comparator.comparing(x -> x.getScenario().getName()))
            .collect(Collectors.toList());
    }

    @Transactional
    public void createHistoryEntry(Scenario scenario, LocalDateTime startDate, LocalDateTime endDate) {
        ScenarioHistoryEntity entity = new ScenarioHistoryEntity();
        entity.setScenario(scenarioEntityDataMapper.transformTarget(scenario));
        entity.setStartDateTime(startDate);
        entity.setEndDateTime(endDate);
        entity.setElapsedTimeMillis(ChronoUnit.MILLIS.between(startDate, endDate));
        scenarioHistoryDao.create(entity);
    }
}
