package io.github.danieltuerk.controlcenter.service.scenario.route;

import io.github.danieltuerk.controlcenter.EventBroadcaster;
import io.github.danieltuerk.controlcenter.shared.AbstractItemEvent;
import io.github.danieltuerk.controlcenter.shared.scenario.Route;
import io.github.danieltuerk.controlcenter.shared.scenario.RouteDataChangedEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

/**
 * @author Daniel Tuerk
 */
@Slf4j
@ApplicationScoped
public class RouteManager {

    private final EventBroadcaster eventBroadcaster;
    private final RouteDataProvider dataProvider;
    private final Event<RouteDataChangedEvent> routeDataChangedEvent;

    @Inject
    public RouteManager(EventBroadcaster eventBroadcaster, RouteDataProvider dataProvider,
                        Event<RouteDataChangedEvent> routeDataChangedEvent) {
        this.eventBroadcaster = eventBroadcaster;
        this.dataProvider = dataProvider;
        this.routeDataChangedEvent = routeDataChangedEvent;
    }

    public List<Route> getRoutes() {
        return dataProvider.getRoutes();
    }

    public Route createRoute(Route route) {
        final var id = dataProvider.createRoute(route);
        final var created = getRouteById(id).orElseThrow(() ->
            new IllegalStateException("Route with id " + id + " not found after create"));
        fireEvent(created.getId(), AbstractItemEvent.ACTION_TYPE.CREATE);
        return created;
    }

    public Route updateRoute(Long id, Route route) {
        dataProvider.updateRoute(id, route);
        fireEvent(id, AbstractItemEvent.ACTION_TYPE.UPDATE);
        return getRouteById(id).orElseThrow(() ->
            new IllegalStateException("Route with id " + id + " not found after update"));
    }

    /**
     * Delete the {@link Route} for the given id and reload the cached data.
     *
     * @param routeId id of {@link Route} to delete
     */
    public boolean deleteRoute(long routeId) {
        if (dataProvider.deleteRoute(routeId)) {
            fireEvent(routeId, AbstractItemEvent.ACTION_TYPE.DELETE);
            return true;
        }
        return false;
    }

    public Optional<Route> getRouteById(Long id) {
        return getRoutes().stream().filter(x -> id.equals(x.getId())).findFirst();
    }

    public boolean notExistsById(Long id) {
        return getRouteById(id).isEmpty();
    }

    private void fireEvent(Long id, AbstractItemEvent.ACTION_TYPE type) {
        final var event = new RouteDataChangedEvent(id, type);
        routeDataChangedEvent.fire(event);
        eventBroadcaster.fireEvent(event);
    }
}
