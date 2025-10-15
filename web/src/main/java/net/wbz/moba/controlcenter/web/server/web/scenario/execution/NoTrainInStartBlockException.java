package net.wbz.moba.controlcenter.web.server.web.scenario.execution;

import net.wbz.moba.controlcenter.shared.track.model.BlockStraight;
import net.wbz.moba.controlcenter.shared.track.model.TrackBlock;
import net.wbz.moba.controlcenter.shared.train.Train;

/**
 * Exception for a route start which haven't the excepted train in the start block.
 * 
 * @author Daniel Tuerk
 */
class NoTrainInStartBlockException extends Exception {

    NoTrainInStartBlockException(Train train, BlockStraight blockStraight) {
        super(String.format("train '%s' (%d) not in one of the start blocks: left='%s', middle='%s', right='%s'!",
            train.getName(), train.getAddress(),
            getDisplayValue(blockStraight.getLeftTrackBlock()),
            getDisplayValue(blockStraight.getMiddleTrackBlock()),
            getDisplayValue(blockStraight.getRightTrackBlock())));
    }

    private static String getDisplayValue(TrackBlock trackBlock) {
        return trackBlock != null ? trackBlock.getDisplayValue() : "";
    }
}
