package net.wbz.moba.controlcenter.web.server.web.editor.block;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.wbz.moba.controlcenter.web.server.web.train.TrainManager;
import net.wbz.moba.controlcenter.web.shared.train.Train;
import net.wbz.moba.controlcenter.web.shared.train.TrainService;

/**
 * Listener for the stop block of a exit signal.
 * Will only reset the waiting train to {@code null} because the start is triggered by setting the waiting train to the
 * stop block.
 * After that the signal will start the train as normal.
 * 
 * @author Daniel Tuerk
 */
class ExitSignalStopBlockListener extends AbstractSignalStopBlockListener {
    private static final Logger log = LoggerFactory.getLogger(ExitSignalStopBlockListener.class);

    ExitSignalStopBlockListener(SignalBlock signalBlock,
            TrainManager trainManager, TrainService trainService) {
        super(signalBlock, trainManager, trainService);
    }

    @Override
    public void trainEnterBlock(int blockNumber, int trainAddress, boolean forward) {
        // if (blockNumber == getTrackBlock().getBlockFunction().getBit()) {
        // log.debug("signal stop block {} - train enter {} (signal {})", new Object[] { blockNumber, trainAddress,
        // getSignalBlock().getSignal().getSignalConfigRed1() });
        // Train train = getTrain(trainAddress);
        //
        // getSignalBlock().setTrainInStopBlock(train);
        // }
        // nothing to do, entering trains are only for scenarios which will set the waiting train to the signal block
    }

    // @Override
    // public void trainLeaveBlock(int blockNumber, int trainAddress, boolean forward) {
    // if (blockNumber == getTrackBlock().getBlockFunction().getBit()) {
    // log.debug("exit signal stop block {} - train leave {} (signal {})", new Object[] { blockNumber,
    // trainAddress,
    // getSignalBlock().getSignal().getSignalConfigRed1() });
    //
    // if (getTrain(trainAddress) == getSignalBlock().getWaitingTrain()) {
    // getSignalBlock().setWaitingTrain(null);
    // }
    // }
    // }

}
