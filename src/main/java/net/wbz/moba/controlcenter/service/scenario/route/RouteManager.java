package net.wbz.moba.controlcenter.service.scenario.route;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.wbz.moba.controlcenter.EventBroadcaster;
import net.wbz.moba.controlcenter.service.scenario.TrackBuilder;
import net.wbz.moba.controlcenter.shared.AbstractItemEvent;
import net.wbz.moba.controlcenter.shared.scenario.Route;
import net.wbz.moba.controlcenter.shared.scenario.RouteDataChangedEvent;
import net.wbz.moba.controlcenter.shared.scenario.Scenario;
import net.wbz.moba.controlcenter.shared.scenario.TrackNotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Manager to access the {@link Scenario}s from database. The data is cached. TODO cache Stations
 *
 * @author Daniel Tuerk
 */
@Slf4j
@ApplicationScoped
public class RouteManager {

    private final EventBroadcaster eventBroadcaster;
    private final RouteMapper routeMapper;

    private final RouteDataProvider dataProvider;
    private final Event<RouteDataChangedEvent> routeDataChangedEvent;
    private final TrackBuilder trackBuilder;

    @Inject
    public RouteManager(EventBroadcaster eventBroadcaster, RouteMapper routeMapper,
                        RouteDataProvider dataProvider, Event<RouteDataChangedEvent> routeDataChangedEvent, TrackBuilder trackBuilder) {
        this.eventBroadcaster = eventBroadcaster;
        this.routeMapper = routeMapper;
        this.dataProvider = dataProvider;
        this.routeDataChangedEvent = routeDataChangedEvent;
        this.trackBuilder = trackBuilder;
    }

    public List<Route> getRoutes() {
        return dataProvider.getRoutes().stream()
            .map(routeMapper::toDto)
            .peek(route -> {
                try {
                    route.setTrack(trackBuilder.build(route));
                } catch (TrackNotFoundException e) {
                    log.error("can't build track of route: {} ({})", route, e.getMessage());
                }
            })
            .collect(Collectors.toList());
    }

    public Route createRoute(Route route) {
        final var entity = dataProvider.createRoute(route);
        fireEvent(entity.id, AbstractItemEvent.ACTION_TYPE.CREATE);
        return routeMapper.toDto(entity);
    }

    public Route updateRoute(Long id, Route route) {
        final var routeEntity = dataProvider.updateRoute(id, route);
        fireEvent(routeEntity.id, AbstractItemEvent.ACTION_TYPE.UPDATE);
        return routeMapper.toDto(routeEntity);
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

    private void fireEvent(Long id, AbstractItemEvent.ACTION_TYPE type) {
        final var event = new RouteDataChangedEvent(id, type);
        routeDataChangedEvent.fire(event);
        eventBroadcaster.fireEvent(event);
    }

    public Optional<Route> getRouteById(Long id) {
        return getRoutes().stream().filter(x -> id.equals(x.getId())).findFirst();
    }

    public boolean notExistsById(Long id) {
        return getRouteById(id).isEmpty();
    }
}
