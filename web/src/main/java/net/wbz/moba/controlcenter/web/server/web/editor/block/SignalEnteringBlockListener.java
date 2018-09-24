package net.wbz.moba.controlcenter.web.server.web.editor.block;

import net.wbz.moba.controlcenter.web.server.web.train.TrainManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO detect enter to switch signal for free or occupied route TODO necessary to have extra block?
 *
 * @author Daniel Tuerk
 */
public class SignalEnteringBlockListener extends AbstractSignalBlockListener {

    private static final Logger log = LoggerFactory.getLogger(SignalEnteringBlockListener.class);

    private final SignalBlock signalBlock;

    public SignalEnteringBlockListener(SignalBlock signalBlock, TrainManager trainManager) {
        super(signalBlock.getSignal().getEnteringBlock(), trainManager);
        this.signalBlock = signalBlock;
    }

    @Override
    public void blockOccupied(int blockNr) {

    }

    @Override
    public void blockFreed(int blockNr) {

    }

    @Override
    public void trainEnterBlock(int blockNumber, int trainAddress, boolean forward) {
        if (blockNumber == getTrackBlock().getBlockFunction().getBit()) {
            log.debug("signal entering block {} - train enter {} (signal {})", blockNumber, trainAddress,
                signalBlock.getSignal().getSignalConfigRed1());
            //
            // Train train = getTrain(trainAddress);
            // if (!signalBlock.isTrainInAnyBlock()) {
            // requestFreeTrack();
            // }
            // signalBlock.setTrainInEnteringBlock(train);
        }
    }

    @Override
    public void trainLeaveBlock(int blockNumber, int trainAddress, boolean forward) {
        if (blockNumber == getTrackBlock().getBlockFunction().getBit()) {
            log.debug("signal entering block {} - train leave {} (signal {})", blockNumber, trainAddress,
                signalBlock.getSignal().getSignalConfigRed1());
            // signalBlock.setTrainInEnteringBlock(null);
        }
    }

}
