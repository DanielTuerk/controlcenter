package net.wbz.moba.controlcenter.web.server.web.scenario;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.Collection;
import net.wbz.moba.controlcenter.web.server.persist.scenario.TrackBuilder;
import net.wbz.moba.controlcenter.web.server.web.station.StationManager;
import net.wbz.moba.controlcenter.shared.scenario.Route;
import net.wbz.moba.controlcenter.shared.scenario.Scenario;
import net.wbz.moba.controlcenter.shared.scenario.Track;
import net.wbz.moba.controlcenter.shared.scenario.TrackNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Daniel Tuerk
 */
@Singleton
public class ScenarioEditorService {

    private static final Logger LOG = LoggerFactory.getLogger(ScenarioEditorService.class);

    private final ScenarioManager scenarioManager;
    private final TrackBuilder trackBuilder;
    private final StationManager stationManager;
    private final RouteManager routeManager;

    @Inject
    public ScenarioEditorService(ScenarioManager scenarioManager, TrackBuilder trackBuilder,
        StationManager stationManager, RouteManager routeManager) {
        this.scenarioManager = scenarioManager;
        this.trackBuilder = trackBuilder;
        this.stationManager = stationManager;
        this.routeManager = routeManager;
    }

    public Collection<Scenario> getScenarios() {
        return scenarioManager.getScenarios();
    }

    public void createScenario(Scenario scenario) {
        scenarioManager.createScenario(scenario);
    }

    public void updateScenario(Scenario scenario) {
        scenarioManager.updateScenario(scenario);
    }

    public void deleteScenario(long scenarioId) {
        scenarioManager.deleteScenario(scenarioId);
    }

    public Collection<Route> getRoutes() {
        return routeManager.getRoutes();
    }

    public void createRoute(Route route) {
        routeManager.createRoute(route);
    }

    public void updateRoute(Route route) {
        routeManager.updateRoute(route);
    }

    public void deleteRoute(long routeId) {
        routeManager.deleteRoute(routeId);
    }

    public Track buildTrack(Route route) throws TrackNotFoundException {
        try {
            return trackBuilder.build(route);
        } catch (TrackNotFoundException e) {
            LOG.error("can't build track for route {}", route, e);
            throw e;
        }
    }

}
