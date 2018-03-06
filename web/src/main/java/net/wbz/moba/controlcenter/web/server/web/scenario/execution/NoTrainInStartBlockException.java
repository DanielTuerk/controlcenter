package net.wbz.moba.controlcenter.web.server.web.scenario.execution;

import net.wbz.moba.controlcenter.web.shared.track.model.TrackBlock;
import net.wbz.moba.controlcenter.web.shared.train.Train;

/**
 * Exception for a route start which haven't the excepted train in the start block.
 * 
 * @author Daniel Tuerk
 */
class NoTrainInStartBlockException extends RouteExecutionInterruptException {

    NoTrainInStartBlockException(Train train, TrackBlock trackBlock) {
        super(String.format("train '%s' (%d) not in start block '%s'!", train.getName(), train.getAddress(),
                trackBlock.getDisplayValue()));
    }
}
