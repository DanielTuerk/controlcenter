package net.wbz.moba.controlcenter.web.server.web.editor.block;

import net.wbz.moba.controlcenter.web.server.web.train.TrainManager;
import net.wbz.moba.controlcenter.web.server.web.train.TrainService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract {@link AbstractSignalBlockListener} for the stop block of a {@link SignalBlock}.
 *
 * @author Daniel Tuerk
 */
abstract class AbstractSignalStopBlockListener extends AbstractSignalBlockListener {
    private static final Logger log = LoggerFactory.getLogger(AbstractSignalStopBlockListener.class);

    private final SignalBlock signalBlock;
    private final TrainService trainService;

    AbstractSignalStopBlockListener(SignalBlock signalBlock, TrainManager trainManager, TrainService trainService) {
        super(signalBlock.getSignal().getStopBlock(), trainManager);
        this.signalBlock = signalBlock;
        this.trainService = trainService;
    }

    @Override
    public void trainLeaveBlock(int blockNumber, int trainAddress, boolean forward) {
        // if (blockNumber == getTrackBlock().getBlockFunction().getBit()) {
        // log.debug("signal stop block {} - train leave {} (signal {})", new Object[] { blockNumber, trainAddress,
        // getSignalBlock().getSignal().getSignalConfigRed1() });
        //
        // getSignalBlock().setTrainInStopBlock(null);
        //
        // if (getTrain(trainAddress) == getSignalBlock().getWaitingTrain()) {
        // getSignalBlock().setWaitingTrain(null);
        // }
        // }
    }
    @Override
    public void blockOccupied(int blockNr) {
    }

    @Override
    public void blockFreed(int blockNr) {
    }

    protected SignalBlock getSignalBlock() {
        return signalBlock;
    }

    protected TrainService getTrainService() {
        return trainService;
    }
}
