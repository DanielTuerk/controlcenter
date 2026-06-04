package net.wbz.moba.controlcenter.service.scenario.execution.route;


import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import lombok.extern.slf4j.Slf4j;
import net.wbz.moba.controlcenter.shared.scenario.Route;
import net.wbz.moba.controlcenter.shared.scenario.RouteSequence;
import net.wbz.moba.controlcenter.shared.scenario.RouteStateEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@ApplicationScoped
public class RouteReservationCoordinator {

    private final Set<RouteSequence> reservedRouteSequences = new HashSet<>();

    private final RouteStateEventPublisher routeStateEventPublisher;

    RouteReservationCoordinator(RouteStateEventPublisher routeStateEventPublisher) {
        this.routeStateEventPublisher = routeStateEventPublisher;
    }

    void cleanupOnRouteStateChanged(@Observes RouteStateEvent evt) {
        reservedRouteSequences.removeAll(reservedRouteSequences.stream()
            .filter(routeSequence -> routeSequence.getId().equals(evt.getRouteSequenceId()))
            .filter(routeSequence -> switch (evt.getState()) {
                case PREPARED, RESERVED -> false;
                default -> true;
            }).collect(Collectors.toSet()));
    }

    boolean isAlreadyReserved(RouteSequence routeSequence) {
        log.debug("check for already reserved: {}; reserved route sequences: {}", routeSequence.getId(),
            reservedRouteSequences.stream().map(RouteSequence::getId).collect(Collectors.toSet()));
        return reservedRouteSequences.contains(routeSequence)
            || checkIfRouteIsAlreadyReserved(routeSequence.getRoute());
    }

    synchronized boolean reserve(Long scenarioId, RouteSequence routeSequence) {
        if (!reservedRouteSequences.contains(routeSequence)) {
            reservedRouteSequences.add(routeSequence);
            routeStateEventPublisher.fireEvent(scenarioId, routeSequence, Route.ROUTE_RUN_STATE.RESERVED);
            return true;
        } else {
            log.warn("route sequence {} ({}) already reserved", routeSequence.getId(), routeSequence.getRoute().getName());
            return false;
        }
    }

    private boolean checkIfRouteIsAlreadyReserved(Route route) {
        return reservedRouteSequences.stream().map(RouteSequence::getRoute)
            .anyMatch(r -> r.getId().equals(route.getId()));
    }

}

