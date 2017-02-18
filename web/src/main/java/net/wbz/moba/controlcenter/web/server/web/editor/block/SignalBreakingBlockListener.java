package net.wbz.moba.controlcenter.web.server.web.editor.block;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.wbz.moba.controlcenter.web.server.web.train.TrainManager;
import net.wbz.moba.controlcenter.web.shared.train.Train;

/**
 * // TODO breaking block
 * 
 * @author Daniel Tuerk
 */
public class SignalBreakingBlockListener extends AbstractSignalBlockListener {
    private static final Logger log = LoggerFactory.getLogger(SignalBreakingBlockListener.class);

    private final SignalBlock signalBlock;

    public SignalBreakingBlockListener(SignalBlock signalBlock, TrainManager trainManager) {
        super(signalBlock.getSignal().getBreakingBlock(), trainManager);
        this.signalBlock = signalBlock;
    }

    @Override
    public void trainEnterBlock(int blockNumber, int trainAddress, boolean forward) {

        if (!signalBlock.isMonitoringBlockFree() && blockNumber == getTrackBlock().getBlockFunction()
                .getBit()) {
            Train train = getTrain(trainAddress);

            signalBlock.setTrainInBreakingBlock(train);

        }
        // TODO break

    }

    @Override
    public void trainLeaveBlock(int blockNumber, int trainAddress, boolean forward) {
        if (blockNumber == getTrackBlock().getBlockFunction().getBit()) {
            signalBlock.setTrainInBreakingBlock(null);
        }
    }

    @Override
    public void blockOccupied(int blockNr) {
    }

    @Override
    public void blockFreed(int blockNr) {
    }
}
