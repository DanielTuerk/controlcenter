package net.wbz.moba.controlcenter.web.server.web.scenario;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.Collection;
import net.wbz.moba.controlcenter.web.server.persist.scenario.TrackBuilder;
import net.wbz.moba.controlcenter.web.server.web.station.StationManager;
import net.wbz.moba.controlcenter.web.shared.scenario.Route;
import net.wbz.moba.controlcenter.web.shared.scenario.Scenario;
import net.wbz.moba.controlcenter.web.shared.scenario.ScenarioEditorService;
import net.wbz.moba.controlcenter.web.shared.station.Station;
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
    private final RouteManager routeManager;

    @Inject
    public ScenarioEditorServiceImpl(ScenarioManager scenarioManager, TrackBuilder trackBuilder,
        StationManager stationManager, RouteManager routeManager) {
        this.scenarioManager = scenarioManager;
        this.trackBuilder = trackBuilder;
        this.stationManager = stationManager;
        this.routeManager = routeManager;
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
    public Collection<Route> getRoutes() {
        return routeManager.getRoutes();
    }

    @Override
    public void createRoute(Route route) {
        routeManager.createRoute(route);
    }

    @Override
    public void updateRoute(Route route) {
        routeManager.updateRoute(route);
    }

    @Override
    public void deleteRoute(long routeId) {
        routeManager.deleteRoute(routeId);
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
