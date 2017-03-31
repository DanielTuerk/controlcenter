package net.wbz.moba.controlcenter.web.server.web.editor.block;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Objects;

import net.wbz.moba.controlcenter.web.server.web.train.TrainManager;
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
    private final TrainManager trainManager;

    SignalMonitoringBlockListener(SignalBlock signalBlock, TrackViewerService trackViewerService,
            TrainManager trainManager) {
        super(signalBlock.getSignal().getMonitoringBlock(), trainManager);
        this.signalBlock = signalBlock;
        this.trackViewerService = trackViewerService;
        this.trainManager = trainManager;
    }

    public abstract void trackClear();

    @Override
    public void trainEnterBlock(int blockNumber, int trainAddress, boolean forward) {
        if (blockNumber == getMonitoringBlockFunction().getBit()) {
            log.debug("signal monitoring block {} - train enter {} ", new Object[] { blockNumber, trainAddress });
            signalBlock.setTrainInMonitoringBlock(trainManager.getTrain(trainAddress));
        }
    }

    @Override
    public void trainLeaveBlock(int blockNumber, int trainAddress, boolean forward) {
        if (blockNumber == getTrackBlock().getBlockFunction().getBit()) {
            log.debug("signal: {} monitoring block {} - train leave {}", new Object[] { signalBlock.getSignal().getId(),
                    blockNumber, trainAddress });
            signalBlock.setTrainInMonitoringBlock(null);
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

            trackClear();
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

    protected SignalBlock getSignalBlock() {
        return signalBlock;
    }
}
