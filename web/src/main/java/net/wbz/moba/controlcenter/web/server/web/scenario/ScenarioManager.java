package net.wbz.moba.controlcenter.web.server.web.scenario;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.wbz.moba.controlcenter.web.server.event.EventBroadcaster;
import net.wbz.moba.controlcenter.web.server.persist.scenario.RouteSequenceDao;
import net.wbz.moba.controlcenter.web.server.persist.scenario.RouteSequenceDataMapper;
import net.wbz.moba.controlcenter.web.server.persist.scenario.RouteSequenceEntity;
import net.wbz.moba.controlcenter.web.server.persist.scenario.ScenarioDao;
import net.wbz.moba.controlcenter.web.server.persist.scenario.ScenarioDataMapper;
import net.wbz.moba.controlcenter.web.server.persist.scenario.ScenarioEntity;
import net.wbz.moba.controlcenter.web.shared.scenario.RouteSequence;
import net.wbz.moba.controlcenter.web.shared.scenario.Scenario;
import net.wbz.moba.controlcenter.web.shared.scenario.ScenarioDataChangedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manager to access the {@link Scenario}s from database. The data is cached. TODO cache Stations
 *
 * @author Daniel Tuerk
 */
@Singleton
public class ScenarioManager {

    private static final Logger LOG = LoggerFactory.getLogger(ScenarioManager.class);

    /**
     * Cached scenarios from persistence.
     */
    private final List<Scenario> scenarios = new ArrayList<>();

    private final ScenarioDao scenarioDao;
    private final RouteSequenceDao routeSequenceDao;
    private final EventBroadcaster eventBroadcaster;
    private final ScenarioDataMapper dataMapper;
    private final RouteSequenceDataMapper routeSequenceDataMapper;
    private final RouteManager routeManager;

    @Inject
    public ScenarioManager(ScenarioDao scenarioDao, RouteSequenceDao routeSequenceDao,
        EventBroadcaster eventBroadcaster, ScenarioDataMapper dataMapper,
        RouteSequenceDataMapper routeSequenceDataMapper, RouteManager routeManager) {
        this.scenarioDao = scenarioDao;
        this.routeSequenceDao = routeSequenceDao;
        this.eventBroadcaster = eventBroadcaster;
        this.dataMapper = dataMapper;
        this.routeSequenceDataMapper = routeSequenceDataMapper;
        this.routeManager = routeManager;

        routeManager.addListener(() -> updateRoutesOfScenarios(getScenarios()));
    }

    synchronized List<Scenario> getScenarios() {
        if (scenarios.isEmpty()) {
            loadScenariosFromDatabase();
        }
        return scenarios;
    }

    Scenario getScenarioById(long scenarioId) {
        for (Scenario scenario : scenarios) {
            if (scenarioId == scenario.getId()) {
                return scenario;
            }
        }
        throw new RuntimeException(String.format("no scenario found for id: %d", scenarioId));
    }

    @Transactional
    void createScenario(Scenario scenario) {
        // save scenario without route mapping
        ScenarioEntity transformedEntity = dataMapper.transformTarget(scenario);
        transformedEntity.getRouteSequences().clear();
        ScenarioEntity createdEntity = scenarioDao.create(transformedEntity);
        // create routes for scenario
        createOrUpdateRouteSequences(scenario.getRouteSequences(), transformedEntity);
        // update scenario for created routes
        scenarioDao.update(createdEntity);

        loadScenariosFromDatabase();
        fireScenariosChanged();
    }

    /**
     * Delete the {@link Scenario} for the given id and reload the cached data.
     *
     * @param scenarioId id of {@link Scenario} to delete
     */
    @Transactional
    void deleteScenario(long scenarioId) {
        routeSequenceDao.deleteByScenario(scenarioId);
        scenarioDao.delete(scenarioId);
        loadScenariosFromDatabase();
        fireScenariosChanged();
    }

    /**
     * Update the given {@link Scenario} and reload the cached data.
     *
     * @param scenario {@link Scenario} to update in database
     */
    @Transactional
    void updateScenario(Scenario scenario) {
        ScenarioEntity scenarioEntity = dataMapper.transformTarget(scenario);

        createOrUpdateRouteSequences(scenario.getRouteSequences(), scenarioEntity);

        scenarioDao.update(scenarioEntity);
        loadScenariosFromDatabase();
        fireScenariosChanged();
    }

    private void loadScenariosFromDatabase() {
        LOG.debug("load scenarios from database");
        scenarios.clear();
        Collection<Scenario> scenarios = dataMapper.transformSource(scenarioDao.listAll());
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
        List<RouteSequenceEntity> entities = new ArrayList<>();
        for (RouteSequence routeBlockPart : routeSequences) {
            RouteSequenceEntity routeEntity = routeSequenceDataMapper.transformTarget(routeBlockPart);
            routeEntity.setScenario(scenarioEntity);
            if (routeBlockPart.getId() == null) {
                routeEntity = routeSequenceDao.create(routeEntity);
            } else {
                routeSequenceDao.update(routeEntity);
            }
            entities.add(routeEntity);
        }
        // delete removed route sequences
        List<RouteSequenceEntity> routeSequenceEntities = new ArrayList<>(
            routeSequenceDao.findByScenario(scenarioEntity.getId()));
        routeSequenceEntities.removeAll(entities);
        for (RouteSequenceEntity routeSequenceEntity : routeSequenceEntities) {
            routeSequenceDao.delete(routeSequenceEntity);
        }

        // set to actual merged entities
        scenarioEntity.setRouteSequences(entities);
    }

    private void fireScenariosChanged() {
        eventBroadcaster.fireEvent(new ScenarioDataChangedEvent());
    }

}
