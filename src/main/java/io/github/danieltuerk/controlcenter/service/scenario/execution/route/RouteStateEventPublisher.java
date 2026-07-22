package io.github.danieltuerk.controlcenter.service.scenario.execution.route;

import io.github.danieltuerk.controlcenter.EventBroadcaster;
import io.github.danieltuerk.controlcenter.shared.scenario.Route;
import io.github.danieltuerk.controlcenter.shared.scenario.RouteSequence;
import io.github.danieltuerk.controlcenter.shared.scenario.RouteStateEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;

@ApplicationScoped
public class RouteStateEventPublisher {

    private final Event<RouteStateEvent> routeStateHandler;
    private final EventBroadcaster eventBroadcaster;

    @Inject
    RouteStateEventPublisher(Event<RouteStateEvent> routeStateHandler, EventBroadcaster eventBroadcaster) {
        this.routeStateHandler = routeStateHandler;
        this.eventBroadcaster = eventBroadcaster;
    }

    void fireEvent(long scenarioId, RouteSequence routeSequence, Route.ROUTE_RUN_STATE state) {
        fireEvent(scenarioId, routeSequence, state, null);
    }

    void fireEvent(long scenarioId, RouteSequence routeSequence, Route.ROUTE_RUN_STATE state, String message) {
        final var routeStateEvent = new RouteStateEvent(scenarioId, routeSequence.getId(), state, message);
        routeStateHandler.fire(routeStateEvent);
        eventBroadcaster.fireEvent(routeStateEvent);
    }
}
