package net.wbz.moba.controlcenter.service.scenario.execution;

import net.wbz.moba.controlcenter.shared.track.model.Signal;

/**
 * Exception for a route start which haven't an available {@link Signal} in the start block.
 * 
 * @author Daniel Tuerk
 */
class NoStartSignalAvailableException extends ScenarioExecutionInterruptException {

    NoStartSignalAvailableException(String msg) {
        super(msg);
    }
}
