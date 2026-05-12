package net.wbz.moba.controlcenter.service.scenario;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import net.wbz.moba.controlcenter.EventBroadcaster;
import net.wbz.moba.controlcenter.persist.entity.ScenarioEntity;
import net.wbz.moba.controlcenter.persist.repository.RouteSequenceRepository;
import net.wbz.moba.controlcenter.persist.repository.ScenarioRepository;
import net.wbz.moba.controlcenter.persist.repository.TrainRepository;
import net.wbz.moba.controlcenter.shared.scenario.RouteSequence;
import net.wbz.moba.controlcenter.shared.scenario.Scenario;
import net.wbz.moba.controlcenter.shared.scenario.ScenarioDataChangedEvent;
import net.wbz.moba.controlcenter.shared.train.TrainDataChangedEvent;
import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Manager to access the {@link Scenario}s from database. The data is cached. TODO cache Stations
 *
 * @author Daniel Tuerk
 */
@ApplicationScoped
public class ScenarioManager {

    private static final Logger LOG = Logger.getLogger(ScenarioManager.class);

    /**
     * Cached scenarios from persistence.
     */
    private final List<Scenario> scenarios = new ArrayList<>();

    private final ScenarioRepository scenarioRepository;
    private final RouteSequenceRepository routeSequenceRepository;
    private final ScenarioStatisticManager scenarioStatisticManager;
    private final EventBroadcaster eventBroadcaster;
    private final ScenarioMapper dataMapper;
    private final ScenarioMapper scenarioMapper;
    private final TrainRepository trainRepository;
    private final RouteManager routeManager;
    private final Event<ScenarioDataChangedEvent> scenarioDataChangedEvent;

    @Inject
    public ScenarioManager(ScenarioRepository scenarioRepository, RouteSequenceRepository routeSequenceRepository,
                           ScenarioStatisticManager scenarioStatisticManager, EventBroadcaster eventBroadcaster,
                           ScenarioMapper dataMapper, ScenarioMapper scenarioMapper,
                           TrainRepository trainRepository, RouteManager routeManager,
                           Event<ScenarioDataChangedEvent> scenarioDataChangedEvent) {
        this.scenarioRepository = scenarioRepository;
        this.routeSequenceRepository = routeSequenceRepository;
        this.scenarioStatisticManager = scenarioStatisticManager;
        this.eventBroadcaster = eventBroadcaster;
        this.dataMapper = dataMapper;
        this.scenarioMapper = scenarioMapper;
        this.trainRepository = trainRepository;
        this.routeManager = routeManager;
        this.scenarioDataChangedEvent = scenarioDataChangedEvent;

        routeManager.addListener(() -> updateRoutesOfScenarios(getScenarios()));
    }

    public void onTrainDataChanged(@Observes TrainDataChangedEvent event) {
        LOG.infof("Train data changed for ID: %s, refreshing scenarios...", event.getItemId());
        loadScenariosFromDatabase();
    }

    public synchronized List<Scenario> getScenarios() {
        if (scenarios.isEmpty()) {
            loadScenariosFromDatabase();
        }
        return scenarios;
    }


    public Scenario getScenarioById(long scenarioId) {
        for (Scenario scenario : getScenarios()) {
            if (scenarioId == scenario.getId()) {
                return scenario;
            }
        }
        throw new RuntimeException(String.format("no scenario found for id: %d", scenarioId));
    }

    @Transactional
    public Scenario createScenario(Scenario scenario) {
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

        loadScenariosFromDatabaseAndThrowEvent();

        return scenarioMapper.toDto(scenarioEntity);
    }

    /**
     * Delete the {@link Scenario} for the given id and reload the cached data.
     *
     * @param scenarioId id of {@link Scenario} to delete
     * @return {@code true} if deleted, otherwise {@code false}
     */
    @Transactional
    public boolean deleteScenario(long scenarioId) {
        if (scenarioRepository.findByIdOptional(scenarioId).isPresent()) {
            routeSequenceRepository.deleteByScenario(scenarioId);
            scenarioStatisticManager.deleteEntriesOfScenario(scenarioId);
            scenarioRepository.deleteById(scenarioId);
            loadScenariosFromDatabaseAndThrowEvent();
            return true;
        }
        return false;
    }

    /**
     * Update the given {@link Scenario} and reload the cached data.
     *
     * @param scenario {@link Scenario} to update in database
     */
    @Transactional
    public void updateScenario(Long scenarioId, Scenario scenario) {
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
        loadScenariosFromDatabaseAndThrowEvent();
    }

    private void loadScenariosFromDatabaseAndThrowEvent() {
        loadScenariosFromDatabase();
        final var event = new ScenarioDataChangedEvent();
        scenarioDataChangedEvent.fire(event);
        eventBroadcaster.fireEvent(event);
    }

    private void loadScenariosFromDatabase() {
        LOG.debug("load scenarios from database");
        scenarios.clear();
        Collection<Scenario> scenarios = scenarioRepository.listAll().stream()
            .map(dataMapper::toDto)
            .collect(Collectors.toList());
        updateRoutesOfScenarios(scenarios);

        this.scenarios.addAll(scenarios);
    }

    /**
     * Replace mapped route with cached route of route manager (with build tracks).
     *
     * @param scenarios scenarios to update
     */
    private void updateRoutesOfScenarios(Collection<Scenario> scenarios) {
        scenarios.forEach(scenario -> scenario.getRouteSequences().forEach(
            routeSequence -> routeManager.getRouteById(routeSequence.getRoute().getId())
                .ifPresent(routeSequence::setRoute)));
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

    public Optional<Scenario> getById(Long id) {
        return scenarioRepository.findByIdOptional(id)
            .map(scenarioMapper::toDto);
    }

    public boolean existsById(Long id) {
        return scenarioRepository.findByIdOptional(id).isPresent();
    }
}
