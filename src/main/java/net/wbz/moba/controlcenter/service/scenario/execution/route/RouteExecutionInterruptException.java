package net.wbz.moba.controlcenter.service.scenario.execution.route;

/**
 * Interrupt of a single route in the scenario execution.
 * 
 * @author Daniel Tuerk
 */
class RouteExecutionInterruptException extends Exception {

    RouteExecutionInterruptException(String message) {
        super(message);
    }

}
