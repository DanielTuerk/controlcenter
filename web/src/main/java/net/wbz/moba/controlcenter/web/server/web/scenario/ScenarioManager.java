package net.wbz.moba.controlcenter.web.server.web.scenario;

import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import net.wbz.moba.controlcenter.web.server.persist.scenario.RouteDao;
import net.wbz.moba.controlcenter.web.server.persist.scenario.RouteEntity;
import net.wbz.moba.controlcenter.web.server.persist.scenario.ScenarioDao;
import net.wbz.moba.controlcenter.web.server.persist.scenario.ScenarioEntity;
import net.wbz.moba.controlcenter.web.server.persist.scenario.StationDao;
import net.wbz.moba.controlcenter.web.server.persist.scenario.StationEntity;
import net.wbz.moba.controlcenter.web.server.web.DataMapper;
import net.wbz.moba.controlcenter.web.shared.scenario.Route;
import net.wbz.moba.controlcenter.web.shared.scenario.Scenario;
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

    private final ScenarioDao scenarioDao;
    private final StationDao stationDao;
    private final RouteDao routeDao;

    @Inject
    public ScenarioManager(ScenarioDao scenarioDao, StationDao stationDao, RouteDao routeDao) {
        this.scenarioDao = scenarioDao;
        this.stationDao = stationDao;
        this.routeDao = routeDao;
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

    public ScenarioEntity createScenario(Scenario scenario) {
        return scenarioDao.create(dataMapper.transformTarget(scenario));
    }

    /**
     * Delete the {@link Scenario} for the given id and reload the cached data.
     *
     * @param scenarioId id of {@link Scenario} to delete
     */
    public void deleteScenario(long scenarioId) {
        scenarioDao.delete(scenarioDao.findById(scenarioId));
        loadScenariosFromDatabase();
    }

    /**
     * Update the given {@link Scenario} and reload the cached data.
     *
     * @param scenario {@link Scenario} to update in database
     */
    public void updateScenario(Scenario scenario) {
        scenarioDao.update(dataMapper.transformTarget(scenario));
        loadScenariosFromDatabase();
    }

    public void createStation(Station station) {
        stationDao.create(stationDataMapper.transformTarget(station));
    }

    public void updateStation(Station station) {
        stationDao.update(stationDataMapper.transformTarget(station));
    }

    public void deleteStation(long stationId) {
        stationDao.delete(stationDao.findById(stationId));
    }

    public Collection<Route> getRoutes() {
        return routeDataMapper.transformSource(routeDao.listAll());
    }

    public void updateRoute(Route route) {
        routeDao.update(routeDataMapper.transformTarget(route));
    }

    public void createRoute(Route route) {
        routeDao.create(routeDataMapper.transformTarget(route));
    }
}
