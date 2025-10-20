package net.wbz.moba.controlcenter.service.scenario.execution;

/**
 * Interrupt of a single route in the scenario execution.
 * 
 * @author Daniel Tuerk
 */
class RouteExecutionInterruptException extends Exception {

    RouteExecutionInterruptException(String message) {
        super(message);
    }

    RouteExecutionInterruptException(String message, Exception e) {
        super(message, e);
    }

}
