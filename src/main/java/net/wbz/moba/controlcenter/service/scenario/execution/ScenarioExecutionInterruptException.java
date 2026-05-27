package net.wbz.moba.controlcenter.service.scenario.execution;

/**
 * Interrupt of the scenario execution.
 * 
 * @author Daniel Tuerk
 */
class ScenarioExecutionInterruptException extends RuntimeException {

    ScenarioExecutionInterruptException(String message) {
        super(message);
    }

    ScenarioExecutionInterruptException(String message, Throwable e) {
        super(message, e);
    }
}