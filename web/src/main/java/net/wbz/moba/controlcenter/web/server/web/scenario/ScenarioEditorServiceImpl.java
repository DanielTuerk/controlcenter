package net.wbz.moba.controlcenter.web.server.web.scenario;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.Collection;
import net.wbz.moba.controlcenter.web.server.EventBroadcaster;
import net.wbz.moba.controlcenter.web.server.persist.scenario.TrackBuilder;
import net.wbz.moba.controlcenter.web.server.persist.scenario.TrackNotFoundException;
import net.wbz.moba.controlcenter.web.shared.scenario.Route;
import net.wbz.moba.controlcenter.web.shared.scenario.Scenario;
import net.wbz.moba.controlcenter.web.shared.scenario.ScenarioEditorService;
import net.wbz.moba.controlcenter.web.shared.scenario.ScenariosChangedEvent;
import net.wbz.moba.controlcenter.web.shared.scenario.Station;
import net.wbz.moba.controlcenter.web.shared.scenario.Track;
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

    /**
     * Broadcaster for client side event handling of state changes.
     */
    private final EventBroadcaster eventBroadcaster;

    @Inject
    public ScenarioEditorServiceImpl(ScenarioManager scenarioManager, TrackBuilder trackBuilder,
        EventBroadcaster eventBroadcaster) {
        this.scenarioManager = scenarioManager;
        this.trackBuilder = trackBuilder;
        this.eventBroadcaster = eventBroadcaster;
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
        return scenarioManager.getStations();
    }

    @Override
    public void createStation(Station station) {
        scenarioManager.createStation(station);
    }

    @Override
    public void updateStation(Station station) {
        scenarioManager.updateStation(station);
    }

    @Override
    public void deleteStation(long stationId) {
        scenarioManager.deleteStation(stationId);
    }

    @Override
    public Collection<Route> getRoutes() {
        return scenarioManager.getRoutes();
    }

    @Override
    public void createRoute(Route route) {
        scenarioManager.createRoute(route);
        fireChangeEvent();
    }

    @Override
    public void updateRoute(Route route) {
        scenarioManager.updateRoute(route);
        fireChangeEvent();
    }

    @Override
    public Track buildTrack(Route route) {
        try {
            return trackBuilder.build(route);
        } catch (TrackNotFoundException e) {
            LOG.error("can't build track of route:" + route, e);
        }
        return null;
    }

    private void fireChangeEvent() {
        eventBroadcaster.fireEvent(new ScenariosChangedEvent());
    }
}
