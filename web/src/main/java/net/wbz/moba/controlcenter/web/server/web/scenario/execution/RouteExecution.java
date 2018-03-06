package net.wbz.moba.controlcenter.web.server.web.scenario.execution;

import net.wbz.moba.controlcenter.web.shared.scenario.RouteSequence;
import net.wbz.moba.controlcenter.web.shared.track.model.Signal;
import net.wbz.moba.controlcenter.web.shared.train.Train;

/**
 * Model for the execution of a {@link RouteSequence}.
 * 
 * @author Daniel Tuerk
 */
class RouteExecution {

    private final RouteSequence routeSequence;
    private final RouteSequence previousRouteSequence;
    /**
     * {@link Train} for the route to drive.
     */
    private final Train train;
    /**
     * The start {@link Signal} of the route.
     */
    private final Signal signal;

    RouteExecution(RouteSequence routeSequence, RouteSequence previousRouteSequence, Train train, Signal signal) {
        this.routeSequence = routeSequence;
        this.previousRouteSequence = previousRouteSequence;
        this.train = train;
        this.signal = signal;
    }

    RouteSequence getRouteSequence() {
        return routeSequence;
    }

    Train getTrain() {
        return train;
    }

    Signal getSignal() {
        return signal;
    }

    RouteSequence getPreviousRouteSequence() {
        return previousRouteSequence;
    }
}
