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
import net.wbz.moba.controlcenter.web.server.persist.scenario.RouteBlockDao;
import net.wbz.moba.controlcenter.web.server.persist.scenario.RouteBlockPartEntity;
import net.wbz.moba.controlcenter.web.server.persist.scenario.RouteDao;
import net.wbz.moba.controlcenter.web.server.persist.scenario.RouteEntity;
import net.wbz.moba.controlcenter.web.server.persist.scenario.RouteSequenceDao;
import net.wbz.moba.controlcenter.web.server.persist.scenario.RouteSequenceEntity;
import net.wbz.moba.controlcenter.web.server.persist.scenario.ScenarioDao;
import net.wbz.moba.controlcenter.web.server.persist.scenario.ScenarioEntity;
import net.wbz.moba.controlcenter.web.server.persist.scenario.StationDao;
import net.wbz.moba.controlcenter.web.server.persist.scenario.StationEntity;
import net.wbz.moba.controlcenter.web.server.web.DataMapper;
import net.wbz.moba.controlcenter.web.shared.scenario.Route;
import net.wbz.moba.controlcenter.web.shared.scenario.RouteBlockPart;
import net.wbz.moba.controlcenter.web.shared.scenario.RouteSequence;
import net.wbz.moba.controlcenter.web.shared.scenario.Scenario;
import net.wbz.moba.controlcenter.web.shared.scenario.ScenariosChangedEvent;
import net.wbz.moba.controlcenter.web.shared.scenario.Station;

/**
 * Manager to access the {@link Scenario}s from database.
 * The data is cached.
 *
 * @author Daniel Tuerk
 */
@Singleton
public class ScenarioManager {

    private static final Logger LOG = LoggerFactory.getLogger(ScenarioManager.class);

    private final List<Scenario> scenarios = Lists.newArrayList();

    private final DataMapper<Scenario, ScenarioEntity> dataMapper = new DataMapper<>(Scenario.class,
            ScenarioEntity.class);
    private final DataMapper<Station, StationEntity> stationDataMapper = new DataMapper<>(Station.class,
            StationEntity.class);
    private final DataMapper<Route, RouteEntity> routeDataMapper = new DataMapper<>(Route.class,
            RouteEntity.class);
    private final DataMapper<RouteBlockPart, RouteBlockPartEntity> routeBlockDataMapper = new DataMapper<>(
            RouteBlockPart.class,
            RouteBlockPartEntity.class);
    private final DataMapper<RouteSequence, RouteSequenceEntity> routeSequenceDataMapper = new DataMapper<>(
            RouteSequence.class,
            RouteSequenceEntity.class);

    private final ScenarioDao scenarioDao;
    private final StationDao stationDao;
    private final RouteDao routeDao;
    private final RouteBlockDao routeBlockDao;
    private final RouteSequenceDao routeSequenceDao;
    private final EventBroadcaster eventBroadcaster;

    @Inject
    public ScenarioManager(ScenarioDao scenarioDao, StationDao stationDao, RouteDao routeDao,
            RouteBlockDao routeBlockDao, RouteSequenceDao routeSequenceDao, EventBroadcaster eventBroadcaster) {
        this.scenarioDao = scenarioDao;
        this.stationDao = stationDao;
        this.routeDao = routeDao;
        this.routeBlockDao = routeBlockDao;
        this.routeSequenceDao = routeSequenceDao;
        this.eventBroadcaster = eventBroadcaster;
    }

    private void loadScenariosFromDatabase() {
        LOG.debug("load scenarios from database");
        scenarios.clear();
        scenarios.addAll(dataMapper.transformSource(scenarioDao.listAll()));
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

    private void fireScenariosChanged() {
        eventBroadcaster.fireEvent(new ScenariosChangedEvent());
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

    public Collection<Route> getRoutes() {
        return routeDataMapper.transformSource(routeDao.listAll());
    }

    @Transactional
    public void updateRoute(Route route) {
        RouteEntity entity = routeDataMapper.transformTarget(route);
        routeDao.update(entity);
        createOrUpdateRouteBlocks(route.getRouteBlockParts(), entity);
    }

    @Transactional
    public void createRoute(Route route) {
        RouteEntity entity = routeDataMapper.transformTarget(route);
        routeDao.create(entity);
        createOrUpdateRouteBlocks(route.getRouteBlockParts(), entity);
    }

    private void createOrUpdateRouteBlocks(List<RouteBlockPart> routeBlockParts, RouteEntity route) {
        for (RouteBlockPart routeBlockPart : routeBlockParts) {
            RouteBlockPartEntity entity = routeBlockDataMapper.transformTarget(routeBlockPart);
            entity.setRoute(route);
            if (routeBlockPart.getId() == null) {
                routeBlockDao.create(entity);
            } else {
                routeBlockDao.update(entity);
            }
        }
        routeBlockDao.flush();
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
}
