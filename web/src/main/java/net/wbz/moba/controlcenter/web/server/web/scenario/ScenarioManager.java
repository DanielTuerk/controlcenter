package net.wbz.moba.controlcenter.web.server.web.scenario;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.wbz.moba.controlcenter.web.server.EventBroadcaster;
import net.wbz.moba.controlcenter.web.server.persist.scenario.RouteDao;
import net.wbz.moba.controlcenter.web.server.persist.scenario.RouteDataMapper;
import net.wbz.moba.controlcenter.web.server.persist.scenario.RouteEntity;
import net.wbz.moba.controlcenter.web.server.persist.scenario.RouteSequenceDao;
import net.wbz.moba.controlcenter.web.server.persist.scenario.RouteSequenceDataMapper;
import net.wbz.moba.controlcenter.web.server.persist.scenario.RouteSequenceEntity;
import net.wbz.moba.controlcenter.web.server.persist.scenario.ScenarioDao;
import net.wbz.moba.controlcenter.web.server.persist.scenario.ScenarioDataMapper;
import net.wbz.moba.controlcenter.web.server.persist.scenario.ScenarioEntity;
import net.wbz.moba.controlcenter.web.server.persist.scenario.TrackBuilder;
import net.wbz.moba.controlcenter.web.server.persist.scenario.TrackNotFoundException;
import net.wbz.moba.controlcenter.web.shared.scenario.Route;
import net.wbz.moba.controlcenter.web.shared.scenario.RouteSequence;
import net.wbz.moba.controlcenter.web.shared.scenario.RoutesChangedEvent;
import net.wbz.moba.controlcenter.web.shared.scenario.Scenario;
import net.wbz.moba.controlcenter.web.shared.scenario.ScenarioDataChangedEvent;
import net.wbz.moba.controlcenter.web.shared.scenario.Station;
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
    /**
     * Cached routes from persistence.
     */
    private final List<Route> routes = new ArrayList<>();

    private final ScenarioDao scenarioDao;
    private final RouteDao routeDao;
    private final RouteSequenceDao routeSequenceDao;
    private final EventBroadcaster eventBroadcaster;
    private final ScenarioDataMapper dataMapper;
    private final RouteDataMapper routeDataMapper;
    private final RouteSequenceDataMapper routeSequenceDataMapper;
    private final TrackBuilder trackBuilder;

    @Inject
    public ScenarioManager(ScenarioDao scenarioDao, RouteDao routeDao, RouteSequenceDao routeSequenceDao,
        EventBroadcaster eventBroadcaster, RouteDataMapper routeDataMapper, ScenarioDataMapper dataMapper,
        RouteSequenceDataMapper routeSequenceDataMapper, TrackBuilder trackBuilder) {
        this.scenarioDao = scenarioDao;
        this.routeDao = routeDao;
        this.routeSequenceDao = routeSequenceDao;
        this.eventBroadcaster = eventBroadcaster;
        this.routeDataMapper = routeDataMapper;
        this.dataMapper = dataMapper;
        this.routeSequenceDataMapper = routeSequenceDataMapper;
        this.trackBuilder = trackBuilder;
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
        fireRoutesChanged();
    }

    synchronized Collection<Route> getRoutes() {
        if (routes.isEmpty()) {
            loadRoutesFromDatabase();
        }
        return routes;
    }

    @Transactional
    void updateRoute(Route route) {
        RouteEntity entity = routeDataMapper.transformTarget(route);
        routeDao.update(entity);
        loadRoutesFromDatabase();
        fireRoutesChanged();
    }

    @Transactional
    void createRoute(Route route) {
        RouteEntity entity = routeDataMapper.transformTarget(route);
        routeDao.create(entity);
        loadRoutesFromDatabase();
        fireRoutesChanged();
    }

    /**
     * Delete the {@link Route} for the given id and reload the cached data.
     *
     * @param routeId id of {@link Route} to delete
     */
    @Transactional
    void deleteRoute(long routeId) {
        if (!routeSequenceDao.routeUsedInScenario(routeId)) {
            routeSequenceDao.delete(routeId);
            loadRoutesFromDatabase();
            fireRoutesChanged();
        } else {
            LOG.error("can't delete route, still in use of scenario");
        }
    }

    private void loadScenariosFromDatabase() {
        LOG.debug("load scenarios from database");
        scenarios.clear();
        scenarios.addAll(dataMapper.transformSource(scenarioDao.listAll()));
    }

    private void loadRoutesFromDatabase() {
        LOG.debug("load routes from database");

        routes.clear();
        routes.addAll(routeDataMapper.transformSource(routeDao.listAll()));
        LOG.debug("build tracks");

        for (Route route : routes) {
            try {
                route.setTrack(trackBuilder.build(route));
            } catch (TrackNotFoundException e) {
                LOG.error("can't build track of route: {} ({})", new Object[]{route, e.getMessage()});
            }
        }
        LOG.debug("tracks finished");
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

    private void fireRoutesChanged() {
        // TODO split to create, update, delete event; to prevent reload and rebuild all tracks
        eventBroadcaster.fireEvent(new RoutesChangedEvent());
    }

}
