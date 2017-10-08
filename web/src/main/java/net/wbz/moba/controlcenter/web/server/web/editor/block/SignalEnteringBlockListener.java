package net.wbz.moba.controlcenter.web.server.web.editor.block;

import net.wbz.moba.controlcenter.web.shared.train.Train;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.wbz.moba.controlcenter.web.server.web.train.TrainManager;

/**
 * @author Daniel Tuerk
 */
public abstract class SignalEnteringBlockListener extends AbstractSignalBlockListener {
    private static final Logger log = LoggerFactory.getLogger(SignalEnteringBlockListener.class);

    private final SignalBlock signalBlock;

    public SignalEnteringBlockListener(SignalBlock signalBlock, TrainManager trainManager) {
        super(signalBlock.getSignal().getEnteringBlock(), trainManager);
        this.signalBlock = signalBlock;
    }

    /**
     * Called to request a free track by switching signal and let the train drive trough. Other trains which have the
     * same monitoring block in the {@link SignalBlock}s need to be stopped because the track is reserved by the
     * entering train of this {@link SignalBlock}.
     */
    protected abstract void requestFreeTrack();

    @Override
    public void blockOccupied(int blockNr) {

    }

    @Override
    public void blockFreed(int blockNr) {

    }

    @Override
    public void trainEnterBlock(int blockNumber, int trainAddress, boolean forward) {
        // if (blockNumber == getTrackBlock().getBlockFunction().getBit()) {
        // log.debug("signal entering block {} - train enter {} (signal {})", new Object[] { blockNumber, trainAddress,
        // signalBlock.getSignal().getSignalConfigRed1() });
        //
        // Train train = getTrain(trainAddress);
        // if (!signalBlock.isTrainInAnyBlock()) {
        // requestFreeTrack();
        // }
        // signalBlock.setTrainInEnteringBlock(train);
        // }
    }


    @Override
    public void trainLeaveBlock(int blockNumber, int trainAddress, boolean forward) {
        // if (blockNumber == getTrackBlock().getBlockFunction().getBit()) {
        // log.debug("signal entering block {} - train leave {} (signal {})", new Object[] { blockNumber, trainAddress,
        // signalBlock.getSignal().getSignalConfigRed1() });
        // signalBlock.setTrainInEnteringBlock(null);
        // }
    }

    protected SignalBlock getSignalBlock() {
        return signalBlock;
    }
}
