package net.wbz.moba.controlcenter.service.scenario.execution;

import net.wbz.moba.controlcenter.shared.scenario.RouteSequence;
import net.wbz.moba.controlcenter.shared.track.model.Signal;

/**
 * Model for the execution of a {@link RouteSequence}.
 * 
 * @author Daniel Tuerk
 */
class RouteExecution {

    private final RouteSequence routeSequence;
    private final RouteSequence previousRouteSequence;
    private final RouteSequence nextRouteSequence;
    /**
     * The start {@link Signal} of the route.
     */
    private final Signal signal;

    RouteExecution(RouteSequence routeSequence, RouteSequence previousRouteSequence, RouteSequence nextRouteSequence,
                   Signal signal) {
        this.routeSequence = routeSequence;
        this.previousRouteSequence = previousRouteSequence;
        this.nextRouteSequence = nextRouteSequence;
        this.signal = signal;
    }

    RouteSequence getRouteSequence() {
        return routeSequence;
    }


    Signal getSignal() {
        return signal;
    }

    RouteSequence getPreviousRouteSequence() {
        return previousRouteSequence;
    }

    RouteSequence getNextRouteSequence() {
        return nextRouteSequence;
    }
}
