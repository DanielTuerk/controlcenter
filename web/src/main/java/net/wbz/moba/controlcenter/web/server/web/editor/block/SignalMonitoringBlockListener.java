package net.wbz.moba.controlcenter.web.server.web.editor.block;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Objects;

import net.wbz.moba.controlcenter.web.shared.track.model.BusDataConfiguration;
import net.wbz.moba.controlcenter.web.shared.track.model.Signal.FUNCTION;
import net.wbz.moba.controlcenter.web.shared.viewer.TrackViewerService;

/**
 * @author Daniel Tuerk
 */
abstract class SignalMonitoringBlockListener extends AbstractSignalBlockListener {

    private static final Logger log = LoggerFactory.getLogger(SignalMonitoringBlockListener.class);

    private final SignalBlock signalBlock;
    private final TrackViewerService trackViewerService;

    SignalMonitoringBlockListener(SignalBlock signalBlock, TrackViewerService trackViewerService) {
        super(signalBlock.getSignal().getMonitoringBlock());
        this.signalBlock = signalBlock;
        this.trackViewerService = trackViewerService;
    }

    public abstract void trackClear(BusDataConfiguration monitoringBlockFunction);

    @Override
    public void trainEnterBlock(int blockNumber, int trainAddress, boolean forward) {

        // remove waiting train from block which start the train
        if (blockNumber == getMonitoringBlockFunction().getBit()) {

            log.debug("signal monitoring block {} - train enter {} ", new Object[] { blockNumber, trainAddress });

            // if (signalBlock.getWaitingTrain() != null && signalBlock.getWaitingTrain()
            // .getAddress() == trainAddress) {
            // signalBlock.setWaitingTrain(null);
            // }
        }

    }

    @Override
    public void trainLeaveBlock(int blockNumber, int trainAddress, boolean forward) {

        if (blockNumber == getTrackBlock().getBlockFunction().getBit()) {
            log.debug("signal monitoring block {} - train leave {}", new Object[] { blockNumber, trainAddress });
        }

    }

    @Override
    public void blockOccupied(int blockNr) {
        if (blockNr == getMonitoringBlockFunction().getBit()) {
            log.debug("signal monitoring block {} - occupied (signal {})", blockNr, signalBlock
                    .getSignal().getSignalConfigRed1());
            signalBlock.setMonitoringBlockFree(false);
            trackViewerService.switchSignal(signalBlock.getSignal(), FUNCTION.HP0);
        }
    }

    @Override
    public void blockFreed(int blockNr) {
        if (blockNr == getMonitoringBlockFunction().getBit()) {
            log.debug("signal monitoring (block {}) - freed (signal: {})", blockNr, signalBlock
                    .getSignal().getSignalConfigRed1());

            trackClear(getMonitoringBlockFunction());

            // clear - for each one
            signalBlock.setMonitoringBlockFree(true);

        }
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("signalBlock", signalBlock)
                .toString();
    }

    private BusDataConfiguration getMonitoringBlockFunction() {
        return signalBlock.getSignal().getMonitoringBlock().getBlockFunction();
    }
}
