package net.wbz.moba.controlcenter.service.scenario.execution.route;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import net.wbz.moba.controlcenter.shared.scenario.Route;
import net.wbz.moba.controlcenter.shared.scenario.RouteStateEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class RouteStateChangedObserver {

    private final Map<Long, Route.ROUTE_RUN_STATE> currentRouteSequenceState = new ConcurrentHashMap<>();

    public void onRouteStateChanged(@Observes RouteStateEvent evt) {
        currentRouteSequenceState.put(evt.routeSequenceId(), evt.state());
    }

    Route.ROUTE_RUN_STATE getRouteState(long routeSequenceId) {
        return currentRouteSequenceState.get(routeSequenceId);
    }
}
