package io.github.danieltuerk.controlcenter.service.scenario;

import io.github.danieltuerk.controlcenter.EventBroadcaster;
import io.github.danieltuerk.controlcenter.persist.entity.ConstructionEntity;
import io.github.danieltuerk.controlcenter.persist.entity.RouteSequenceEntity;
import io.github.danieltuerk.controlcenter.persist.entity.ScenarioEntity;
import io.github.danieltuerk.controlcenter.persist.repository.ConstructionRepository;
import io.github.danieltuerk.controlcenter.persist.repository.RouteSequenceRepository;
import io.github.danieltuerk.controlcenter.persist.repository.ScenarioRepository;
import io.github.danieltuerk.controlcenter.persist.repository.TrainRepository;
import io.github.danieltuerk.controlcenter.service.constrution.ConstructionService;
import io.github.danieltuerk.controlcenter.shared.constrution.Construction;
import io.github.danieltuerk.controlcenter.shared.constrution.CurrentConstructionChangeEvent;
import io.github.danieltuerk.controlcenter.shared.scenario.RouteDataChangedEvent;
import io.github.danieltuerk.controlcenter.shared.scenario.RouteSequence;
import io.github.danieltuerk.controlcenter.shared.scenario.Scenario;
import io.github.danieltuerk.controlcenter.shared.scenario.ScenariosChangedEvent;
import io.github.danieltuerk.controlcenter.shared.train.TrainDataChangedEvent;
import io.quarkus.cache.CacheInvalidateAll;
import io.quarkus.cache.CacheResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@ApplicationScoped
public class ScenarioDataProvider {

    private static final String CACHE = "scenarios-cache";

    private final RouteSequenceRepository routeSequenceRepository;
    private final ScenarioRepository scenarioRepository;
    private final ScenarioStatisticManager scenarioStatisticManager;
    private final TrainRepository trainRepository;
    private final ConstructionService constructionService;
    private final ConstructionRepository constructionRepository;
    private final ScenarioMapper dataMapper;
    private final EventBroadcaster eventBroadcaster;

    public ScenarioDataProvider(RouteSequenceRepository routeSequenceRepository, ScenarioRepository scenarioRepository,
                                ScenarioStatisticManager scenarioStatisticManager, TrainRepository trainRepository,
                                ConstructionService constructionService, ConstructionRepository constructionRepository,
                                ScenarioMapper dataMapper, EventBroadcaster eventBroadcaster) {
        this.routeSequenceRepository = routeSequenceRepository;
        this.scenarioRepository = scenarioRepository;
        this.scenarioStatisticManager = scenarioStatisticManager;
        this.trainRepository = trainRepository;
        this.constructionService = constructionService;
        this.constructionRepository = constructionRepository;
        this.dataMapper = dataMapper;
        this.eventBroadcaster = eventBroadcaster;
    }

    @CacheInvalidateAll(cacheName = CACHE)
    public void onCurrentConstructionChanged(@Observes CurrentConstructionChangeEvent event) {
        log.info("current construction changed for ID: {}, clear cache...", event.construction().getId());
        fireScenariosChangedEvent();
    }

    @CacheInvalidateAll(cacheName = CACHE)
    public void onRoutesChanged(@Observes RouteDataChangedEvent event) {
        log.info("Route data changed for ID: {}, refreshing scenarios...", event.getItemId());
        fireScenariosChangedEvent();
    }

    @CacheInvalidateAll(cacheName = CACHE)
    public void onTrainDataChanged(@Observes TrainDataChangedEvent event) {
        log.info("Train data changed for ID: {}, refreshing scenarios...", event.trainId());
        fireScenariosChangedEvent();
    }

    /**
     * Load scenarios from the database and cache the result.
     *
     * @return list of {@link ScenarioEntity}s
     */
    @CacheResult(cacheName = CACHE)
    public List<Scenario> getScenarios() {
        log.debug("load scenarios from database");
        return constructionService.getCurrentConstruction()
            .map(construction -> scenarioRepository.listAll(construction.getId()))
            .orElse(List.of()).stream()
            .map(dataMapper::toDto)
            .collect(Collectors.toList());
    }

    /**
     * Create a new scenario with the given data and persist it. After creation the scenario is loaded and cached.
     *
     * @param scenario {@link Scenario}
     * @return persisted {@link ScenarioEntity}
     */
    @CacheInvalidateAll(cacheName = CACHE)
    @Transactional
    public Scenario createScenario(Scenario scenario) {
        // save scenario without route mapping
        var scenarioEntity = new ScenarioEntity();
        scenarioEntity.construction = currentConstructionEntity();
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

        return dataMapper.toDto(scenarioEntity);
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
     * @param scenario {@link Scenario} to update in the database
     * @return updated entity
     */
    @CacheInvalidateAll(cacheName = CACHE)
    @Transactional
    public Scenario updateScenario(Long scenarioId, Scenario scenario) {
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
        return dataMapper.toDto(scenarioEntity);
    }

    private void fireScenariosChangedEvent() {
        eventBroadcaster.fireEvent(new ScenariosChangedEvent(false));
    }

    private ConstructionEntity currentConstructionEntity() {
        return constructionRepository.findById(
            constructionService.getCurrentConstruction()
                .map(Construction::getId)
                .orElse(null));
    }


    private void createOrUpdateRouteSequences(List<RouteSequence> routeSequences, ScenarioEntity scenarioEntity) {
        final var ids = routeSequences.stream()
            .map(RouteSequence::getId)
            .collect(Collectors.toSet());
        routeSequenceRepository.findByScenario(scenarioEntity.id).stream()
            .filter(routeSequenceEntity -> !ids.contains(routeSequenceEntity.id))
            .forEach(entity -> {
                scenarioEntity.routeSequences.remove(entity);
                routeSequenceRepository.delete(entity);
            });

        routeSequences.forEach(routeSequence -> {
            RouteSequenceEntity routeEntity;
            if (routeSequence.getId() != null) {
                routeEntity = routeSequenceRepository.findById(routeSequence.getId());
                if (routeEntity == null) {
                    throw new IllegalStateException("RouteSequenceEntity not found: " + routeSequence.getId());
                }
            } else {
                routeEntity = new RouteSequenceEntity();
            }
            routeEntity.scenario = scenarioEntity;
            routeEntity.routeId = routeSequence.getRoute().getId();
            routeEntity.position = routeSequence.getPosition();
            routeEntity.endDelayInSeconds = routeSequence.getEndDelayInSeconds();
            routeSequenceRepository.persist(routeEntity);
            scenarioEntity.routeSequences.add(routeEntity);
        });

    }
}
