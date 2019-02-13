package net.wbz.moba.controlcenter.web.server.web.scenario;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.Collection;
import net.wbz.moba.controlcenter.web.server.persist.scenario.TrackBuilder;
import net.wbz.moba.controlcenter.web.shared.scenario.Route;
import net.wbz.moba.controlcenter.web.shared.scenario.Scenario;
import net.wbz.moba.controlcenter.web.shared.scenario.ScenarioEditorService;
import net.wbz.moba.controlcenter.web.shared.scenario.Station;
import net.wbz.moba.controlcenter.web.shared.scenario.Track;
import net.wbz.moba.controlcenter.web.shared.scenario.TrackNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Daniel Tuerk
 */
@Singleton
public class ScenarioEditorServiceImpl extends RemoteServiceServlet implements ScenarioEditorService {

    private static final Logger LOG = LoggerFactory.getLogger(ScenarioEditorServiceImpl.class);

    private final ScenarioManager scenarioManager;
    private final TrackBuilder trackBuilder;
    private final StationManager stationManager;

    @Inject
    public ScenarioEditorServiceImpl(ScenarioManager scenarioManager, TrackBuilder trackBuilder,
        StationManager stationManager) {
        this.scenarioManager = scenarioManager;
        this.trackBuilder = trackBuilder;
        this.stationManager = stationManager;
    }

    @Override
    public Collection<Scenario> getScenarios() {
        return scenarioManager.getScenarios();
    }

    @Override
    public void createScenario(Scenario scenario) {
        scenarioManager.createScenario(scenario);
    }

    @Override
    public void updateScenario(Scenario scenario) {
        scenarioManager.updateScenario(scenario);
    }

    @Override
    public void deleteScenario(long scenarioId) {
        scenarioManager.deleteScenario(scenarioId);
    }

    @Override
    public Collection<Station> getStations() {
        return stationManager.getStations();
    }

    @Override
    public void createStation(Station station) {
        stationManager.createStation(station);
    }

    @Override
    public void updateStation(Station station) {
        stationManager.updateStation(station);
    }

    @Override
    public void deleteStation(long stationId) {
        stationManager.deleteStation(stationId);
    }

    @Override
    public Collection<Route> getRoutes() {
        return scenarioManager.getRoutes();
    }

    @Override
    public void createRoute(Route route) {
        scenarioManager.createRoute(route);
    }

    @Override
    public void updateRoute(Route route) {
        scenarioManager.updateRoute(route);
    }

    @Override
    public void deleteRoute(long routeId) {
        scenarioManager.deleteRoute(routeId);
    }

    @Override
    public Track buildTrack(Route route) throws TrackNotFoundException {
        try {
            return trackBuilder.build(route);
        } catch (TrackNotFoundException e) {
            LOG.error("can't build track for route {}", route, e);
            throw e;
        }
    }

}
