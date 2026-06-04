package net.wbz.moba.controlcenter.service.scenario.execution.route;

import net.wbz.moba.controlcenter.shared.scenario.Route;

/**
 * Exception for a route without a track.
 *
 * @author Daniel Tuerk
 */
class NoTrackForRouteException extends Exception {

    NoTrackForRouteException(Route route) {
        super(String.format("no track for route: '%s'",
            route.getName()));
    }

}
