package net.wbz.moba.controlcenter.web.shared.scenario;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import java.util.Collection;
import net.wbz.moba.controlcenter.web.guice.MyGuiceServletConfig;

/**
 * @author Daniel Tuerk
 */
@RemoteServiceRelativePath(MyGuiceServletConfig.SERVICE_SCENARIO_EDITOR)
public interface ScenarioEditorService extends RemoteService {

    Collection<Scenario> getScenarios();

    void createScenario(Scenario scenario);

    void updateScenario(Scenario scenario);

    void deleteScenario(long scenarioId);

    Collection<Station> getStations();

    void createStation(Station station);

    void updateStation(Station station);

    void deleteStation(long stationId);

    Collection<Route> getRoutes();

    void createRoute(Route route);

    void updateRoute(Route route);

    void deleteRoute(long routeId);

    Track buildTrack(Route route) throws TrackNotFoundException;
}
