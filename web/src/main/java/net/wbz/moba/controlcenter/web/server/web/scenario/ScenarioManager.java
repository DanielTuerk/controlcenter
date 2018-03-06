package net.wbz.moba.controlcenter.web.server.web.scenario;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;

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
import net.wbz.moba.controlcenter.web.server.persist.scenario.StationDao;
import net.wbz.moba.controlcenter.web.server.persist.scenario.StationDataMapper;
import net.wbz.moba.controlcenter.web.server.persist.scenario.TrackBuilder;
import net.wbz.moba.controlcenter.web.server.persist.scenario.TrackBuilder.TrackNotFoundException;
import net.wbz.moba.controlcenter.web.shared.scenario.Route;
import net.wbz.moba.controlcenter.web.shared.scenario.RouteSequence;
import net.wbz.moba.controlcenter.web.shared.scenario.RoutesChangedEvent;
import net.wbz.moba.controlcenter.web.shared.scenario.Scenario;
import net.wbz.moba.controlcenter.web.shared.scenario.ScenariosChangedEvent;
import net.wbz.moba.controlcenter.web.shared.scenario.Station;

/**
 * Manager to access the {@link Scenario}s from database.
 * The data is cached.
 * TODO cache Stations
 * 
 * @author Daniel Tuerk
 */
@Singleton
public class ScenarioManager {

    private static final Logger LOG = LoggerFactory.getLogger(ScenarioManager.class);

    /**
     * Cached scenarios from persistence.
     */
    private final List<Scenario> scenarios = Lists.newArrayList();
    /**
     * Cached routes from persistence.
     */
    private final List<Route> routes = Lists.newArrayList();

    private final ScenarioDao scenarioDao;
    private final StationDao stationDao;
    private final RouteDao routeDao;
    private final RouteSequenceDao routeSequenceDao;
    private final EventBroadcaster eventBroadcaster;
    private final ScenarioDataMapper dataMapper;
    private final RouteDataMapper routeDataMapper;
    private final StationDataMapper stationDataMapper;
    private final RouteSequenceDataMapper routeSequenceDataMapper;
    private final TrackBuilder trackBuilder;

    @Inject
    public ScenarioManager(ScenarioDao scenarioDao, StationDao stationDao, RouteDao routeDao,
            RouteSequenceDao routeSequenceDao, EventBroadcaster eventBroadcaster,
            RouteDataMapper routeDataMapper, ScenarioDataMapper dataMapper, StationDataMapper stationDataMapper,
            RouteSequenceDataMapper routeSequenceDataMapper, TrackBuilder trackBuilder) {
        this.scenarioDao = scenarioDao;
        this.stationDao = stationDao;
        this.routeDao = routeDao;
        this.routeSequenceDao = routeSequenceDao;
        this.eventBroadcaster = eventBroadcaster;
        this.routeDataMapper = routeDataMapper;
        this.dataMapper = dataMapper;
        this.stationDataMapper = stationDataMapper;
        this.routeSequenceDataMapper = routeSequenceDataMapper;
        this.trackBuilder = trackBuilder;
    }

    public synchronized List<Scenario> getScenarios() {
        if (scenarios.isEmpty()) {
            loadScenariosFromDatabase();
        }
        return scenarios;
    }

    public List<Station> getStations() {
        return Lists.newArrayList(stationDataMapper.transformSource(stationDao.listAll()));
    }

    public Scenario getScenarioById(long scenarioId) {
        for (Scenario scenario : scenarios) {
            if (scenarioId == scenario.getId()) {
                return scenario;
            }
        }
        throw new RuntimeException(String.format("no scenario found for id: %d", scenarioId));
    }

    @Transactional
    public ScenarioEntity createScenario(Scenario scenario) {
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
        return createdEntity;
    }

    /**
     * Delete the {@link Scenario} for the given id and reload the cached data.
     *
     * @param scenarioId id of {@link Scenario} to delete
     */
    @Transactional
    public void deleteScenario(long scenarioId) {
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
    public void updateScenario(Scenario scenario) {
        ScenarioEntity scenarioEntity = dataMapper.transformTarget(scenario);

        createOrUpdateRouteSequences(scenario.getRouteSequences(), scenarioEntity);

        scenarioDao.update(scenarioEntity);
        loadScenariosFromDatabase();
        fireScenariosChanged();
    }

    @Transactional
    public void createStation(Station station) {
        stationDao.create(stationDataMapper.transformTarget(station));
    }

    @Transactional
    public void updateStation(Station station) {
        stationDao.update(stationDataMapper.transformTarget(station));
    }

    @Transactional
    public void deleteStation(long stationId) {
        stationDao.delete(stationDao.findById(stationId));
    }

    public synchronized Collection<Route> getRoutes() {
        if (routes.isEmpty()) {
            loadRoutesFromDatabase();
        }
        return routes;
    }

    @Transactional
    public void updateRoute(Route route) {
        RouteEntity entity = routeDataMapper.transformTarget(route);
        routeDao.update(entity);
        loadRoutesFromDatabase();
        fireRoutesChanged();
    }

    @Transactional
    public void createRoute(Route route) {
        RouteEntity entity = routeDataMapper.transformTarget(route);
        routeDao.create(entity);
        loadRoutesFromDatabase();
        fireRoutesChanged();
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
                LOG.error("can't build track of route: {} ({})", new Object[] { route, e.getMessage() });
            }
        }
        LOG.debug("tracks finished");
    }

    private void createOrUpdateRouteSequences(List<RouteSequence> routeSequences,
            ScenarioEntity scenarioEntity) {
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
        eventBroadcaster.fireEvent(new ScenariosChangedEvent());
    }

    private void fireRoutesChanged() {
        // TODO split to create, update, delete event; to prevent reload and rebuild all tracks
        eventBroadcaster.fireEvent(new RoutesChangedEvent());
    }

}
