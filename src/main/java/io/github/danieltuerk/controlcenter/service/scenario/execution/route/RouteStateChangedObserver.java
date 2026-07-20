package io.github.danieltuerk.controlcenter.service.scenario.execution.route;

import io.github.danieltuerk.controlcenter.shared.scenario.Route;
import io.github.danieltuerk.controlcenter.shared.scenario.RouteStateEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;

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
