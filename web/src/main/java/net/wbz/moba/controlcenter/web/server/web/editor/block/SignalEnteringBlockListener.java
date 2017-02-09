package net.wbz.moba.controlcenter.web.server.web.editor.block;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.wbz.moba.controlcenter.web.shared.track.model.Signal.FUNCTION;
import net.wbz.moba.controlcenter.web.shared.viewer.TrackViewerService;

/**
 * @author Daniel Tuerk
 */
public class SignalEnteringBlockListener extends AbstractSignalBlockListener {
    private static final Logger log = LoggerFactory.getLogger(SignalEnteringBlockListener.class);

    private final SignalBlock signalBlock;
    private final TrackViewerService trackViewerService;

    public SignalEnteringBlockListener(SignalBlock signalBlock, TrackViewerService trackViewerService) {
        super(signalBlock.getSignal().getEnteringBlock());
        this.signalBlock = signalBlock;
        this.trackViewerService = trackViewerService;
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
            log.debug("signal entering block {} - train enter {} (signal {})", new Object[] { blockNumber, trainAddress,
                    signalBlock.getSignal().getSignalConfigRed1() });

            if (signalBlock.isMonitoringBlockFree()) {
                trackViewerService.switchSignal(signalBlock.getSignal(), FUNCTION.HP1);
            }
        }
    }

    @Override
    public void trainLeaveBlock(int blockNumber, int trainAddress, boolean forward) {
        if (blockNumber == getTrackBlock().getBlockFunction().getBit()) {
            log.debug("signal entering block {} - train leave {} (signal {})", new Object[] { blockNumber, trainAddress,
                    signalBlock.getSignal().getSignalConfigRed1() });
        }
    }
}
