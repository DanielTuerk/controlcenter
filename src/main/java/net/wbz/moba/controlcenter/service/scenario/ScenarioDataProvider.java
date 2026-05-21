package net.wbz.moba.controlcenter.service.scenario;

import io.quarkus.cache.CacheInvalidateAll;
import io.quarkus.cache.CacheResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import net.wbz.moba.controlcenter.persist.entity.ScenarioEntity;
import net.wbz.moba.controlcenter.persist.repository.RouteSequenceRepository;
import net.wbz.moba.controlcenter.persist.repository.ScenarioRepository;
import net.wbz.moba.controlcenter.persist.repository.TrainRepository;
import net.wbz.moba.controlcenter.shared.scenario.RouteDataChangedEvent;
import net.wbz.moba.controlcenter.shared.scenario.RouteSequence;
import net.wbz.moba.controlcenter.shared.scenario.Scenario;
import net.wbz.moba.controlcenter.shared.train.TrainDataChangedEvent;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@ApplicationScoped
public class ScenarioDataProvider {

    private static final String CACHE = "scenarios-cache";

    private final RouteSequenceRepository routeSequenceRepository;
    private final ScenarioRepository scenarioRepository;
    private final ScenarioStatisticManager scenarioStatisticManager;
    private final TrainRepository trainRepository;

    public ScenarioDataProvider(RouteSequenceRepository routeSequenceRepository, ScenarioRepository scenarioRepository,
                                ScenarioStatisticManager scenarioStatisticManager, TrainRepository trainRepository) {
        this.routeSequenceRepository = routeSequenceRepository;
        this.scenarioRepository = scenarioRepository;
        this.scenarioStatisticManager = scenarioStatisticManager;
        this.trainRepository = trainRepository;
    }

    @CacheInvalidateAll(cacheName = CACHE)
    public void onRoutesChanged(@Observes RouteDataChangedEvent event) {
        log.info("Route data changed for ID: {}, refreshing scenarios...", event.getItemId());
    }

    @CacheInvalidateAll(cacheName = CACHE)
    public void onTrainDataChanged(@Observes TrainDataChangedEvent event) {
        log.info("Train data changed for ID: {}, refreshing scenarios...", event.getItemId());
    }

    /**
     * Load scenarios from database and cache the result.
     *
     * @return list of {@link ScenarioEntity}s
     */
    @CacheResult(cacheName = CACHE)
    public List<ScenarioEntity> getScenarios() {
        log.debug("load scenarios from database");
        return scenarioRepository.listAll();
    }

    /**
     * Create a new scenario with the given data and persist it. After creation the scenario is loaded and cached.
     *
     * @param scenario {@link Scenario}
     * @return persisted {@link ScenarioEntity}
     */
    @CacheInvalidateAll(cacheName = CACHE)
    @Transactional
    public ScenarioEntity createScenario(Scenario scenario) {
        // save scenario without route mapping
        var scenarioEntity = new ScenarioEntity();
        scenarioEntity.name = scenario.getName();
        scenarioEntity.cron = scenario.getCron();
        if (scenario.getTrain() != null) {
            trainRepository.getTrainById(scenario.getTrain().getId())
                .ifPresent(train -> scenarioEntity.train = train);
        }
        scenarioEntity.startDrivingLevel = scenario.getStartDrivingLevel();
        scenarioEntity.stationPlatformStartId = scenario.getStationPlatformStartId();
        scenarioEntity.stationPlatformEndId = scenario.getStationPlatformEndId();
        scenarioEntity.trainDrivingDirection = scenario.getTrainDrivingDirection();

        scenarioEntity.routeSequences = new ArrayList<>();

        scenarioRepository.persist(scenarioEntity);
        // create routes for scenario
        createOrUpdateRouteSequences(scenario.getRouteSequences(), scenarioEntity);
        // update scenario for created routes
        scenarioRepository.persist(scenarioEntity);

        return scenarioEntity;
    }

    /**
     * Delete the {@link Scenario} for the given id and reload the cached data.
     *
     * @param scenarioId id of {@link Scenario} to delete
     * @return {@code true} if deleted, otherwise {@code false}
     */
    @CacheInvalidateAll(cacheName = CACHE)
    @Transactional
    public boolean deleteScenario(Long scenarioId) {
        if (scenarioRepository.findByIdOptional(scenarioId).isPresent()) {
            routeSequenceRepository.deleteByScenario(scenarioId);
            scenarioStatisticManager.deleteEntriesOfScenario(scenarioId);
            scenarioRepository.deleteById(scenarioId);
            return true;
        }
        return false;
    }

    /**
     * Update the given {@link Scenario} and reload the cached data.
     *
     * @param scenario {@link Scenario} to update in database
     * @return updated entity
     */
    @CacheInvalidateAll(cacheName = CACHE)
    @Transactional
    public ScenarioEntity updateScenario(Long scenarioId, Scenario scenario) {
        ScenarioEntity scenarioEntity = scenarioRepository.findById(scenarioId);

        scenarioEntity.name = scenario.getName();
        scenarioEntity.cron = scenario.getCron();
        trainRepository.getTrainById(scenario.getTrain().getId()).ifPresent(train -> scenarioEntity.train = train);
        scenarioEntity.startDrivingLevel = scenario.getStartDrivingLevel();
        scenarioEntity.stationPlatformStartId = scenario.getStationPlatformStartId();
        scenarioEntity.stationPlatformEndId = scenario.getStationPlatformEndId();
        scenarioEntity.trainDrivingDirection = scenario.getTrainDrivingDirection();

        createOrUpdateRouteSequences(scenario.getRouteSequences(), scenarioEntity);

        scenarioRepository.persist(scenarioEntity);
        return scenarioEntity;
    }

    private void createOrUpdateRouteSequences(List<RouteSequence> routeSequences, ScenarioEntity scenarioEntity) {
        // create or update route sequences
        // TODO migrate (RESTful)
//        List<RouteSequenceEntity> entities = new ArrayList<>();
//        for (RouteSequence routeBlockPart : routeSequences) {
//            RouteSequenceEntity routeEntity = routeSequenceDataMapper.transformTarget(routeBlockPart);
//            routeEntity.scenario = scenarioEntity;
//            if (routeBlockPart.getId() == null) {
//                routeEntity = routeSequenceRepository.create(routeEntity);
//            } else {
//                routeSequenceRepository.update(routeEntity);
//            }
//            entities.add(routeEntity);
//        }
//        // delete removed route sequences
//        List<RouteSequenceEntity> routeSequenceEntities = new ArrayList<>(
//            routeSequenceRepository.findByScenario(scenarioEntity.id));
//        routeSequenceEntities.removeAll(entities);
//        for (RouteSequenceEntity routeSequenceEntity : routeSequenceEntities) {
//            routeSequenceRepository.delete(routeSequenceEntity);
//        }
//
//        // set to actual merged entities
//        scenarioEntity.routeSequences = entities;
    }
}
