package net.wbz.moba.controlcenter.web.server.web.scenario.execution;

/**
 * Interrupt of the scenario execution.
 * 
 * @author Daniel Tuerk
 */
class ScenarioExecutionInterruptException extends Exception {

    ScenarioExecutionInterruptException(String message) {
        super(message);
    }

    ScenarioExecutionInterruptException(String message, Exception e) {
        super(message, e);
    }
}