package net.wbz.moba.controlcenter.service.scenario.execution;

/**
 * Interrupt of the scenario execution.
 * 
 * @author Daniel Tuerk
 */
public class ScenarioExecutionInterruptException extends RuntimeException {

    public ScenarioExecutionInterruptException(String message, Throwable e) {
        super(message, e);
    }
}