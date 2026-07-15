package net.wbz.moba.controlcenter.service.scenario.execution.route;


import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import lombok.extern.slf4j.Slf4j;
import net.wbz.moba.controlcenter.shared.scenario.Route;
import net.wbz.moba.controlcenter.shared.scenario.RouteSequence;
import net.wbz.moba.controlcenter.shared.scenario.RouteStateEvent;
import net.wbz.moba.controlcenter.shared.track.model.AbstractDto;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@ApplicationScoped
public class RouteReservationCoordinator {

    private final Set<RouteSequence> reservedRouteSequences = new HashSet<>();

    private final RouteStateEventPublisher routeStateEventPublisher;
    private final SwitchStartSignalOfRouteToDrive switchStartSignalOfRouteToDrive;

    RouteReservationCoordinator(RouteStateEventPublisher routeStateEventPublisher, SwitchStartSignalOfRouteToDrive switchStartSignalOfRouteToDrive) {
        this.routeStateEventPublisher = routeStateEventPublisher;
        this.switchStartSignalOfRouteToDrive = switchStartSignalOfRouteToDrive;
    }

    void cleanupOnRouteStateChanged(@Observes RouteStateEvent evt) {
        final var toRemove = reservedRouteSequences.stream()
                .filter(routeSequence -> routeSequence.getId().equals(evt.routeSequenceId()))
                .filter(routeSequence -> switch (evt.state()) {
                    case PREPARED, RESERVED -> false;
                    default -> true;
                }).collect(Collectors.toSet());
        if (!toRemove.isEmpty()) {
            log.debug("cleanup route sequences ({}) after event: {}", toRemove.stream()
                            .map(AbstractDto::getId)
                            .toList(),
                    evt);
            reservedRouteSequences.removeAll(toRemove);
        }
    }

    boolean isAlreadyReserved(RouteSequence routeSequence) {
        log.debug("check for already reserved: {} ({}, routeSequenceId: {}); reserved route sequences: {}",
                routeSequence.getRoute().getName(), routeSequence.getRoute().getId(), routeSequence.getId(),
                reservedRouteSequences.stream().map(RouteSequence::getId).collect(Collectors.toSet()));
        return reservedRouteSequences.contains(routeSequence)
                || checkIfRouteIsAlreadyReserved(routeSequence.getRoute());
    }

    synchronized void reserve(Long scenarioId, RouteSequence routeSequence) {
        if (reservedRouteSequences.contains(routeSequence)) {
            log.warn("route sequence {} ({}, routeSequenceId: {}) already reserved",
                    routeSequence.getRoute().getName(), routeSequence.getRoute().getId(), routeSequence.getId());
        } else {
            final var route = routeSequence.getRoute();
            log.debug("reserve route: {} ({}, routeSequenceId: {})",
                    route.getName(), route.getId(), routeSequence.getId());
            reservedRouteSequences.add(routeSequence);
            routeStateEventPublisher.fireEvent(scenarioId, routeSequence, Route.ROUTE_RUN_STATE.RESERVED);

            switchStartSignalOfRouteToDrive.call(route);
        }
    }

    private boolean checkIfRouteIsAlreadyReserved(Route route) {
        return reservedRouteSequences.stream().map(RouteSequence::getRoute)
                .anyMatch(r -> r.getId().equals(route.getId()));
    }

}

