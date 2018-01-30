package net.wbz.moba.controlcenter.web.server.web.editor.block;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Objects;

import net.wbz.moba.controlcenter.web.server.web.train.TrainManager;
import net.wbz.moba.controlcenter.web.shared.track.model.BusDataConfiguration;
import net.wbz.moba.controlcenter.web.shared.track.model.Signal.FUNCTION;
import net.wbz.moba.controlcenter.web.shared.viewer.TrackViewerService;

/**
 * <p>
 * React to a occupied monitoring block and switch the {@link net.wbz.moba.controlcenter.web.shared.track.model.Signal}
 * to {@link FUNCTION#HP0}.
 * </p>
 * 
 * @author Daniel Tuerk
 */
class SignalMonitoringBlockListener extends AbstractSignalBlockListener {

    private static final Logger LOG = LoggerFactory.getLogger(SignalMonitoringBlockListener.class);
    private final SignalBlock signalBlock;
    private final TrackViewerService trackViewerService;
    private Integer blockNrToMonitore;

    SignalMonitoringBlockListener(SignalBlock signalBlock, TrackViewerService trackViewerService,
            TrainManager trainManager) {
        super(signalBlock.getSignal().getMonitoringBlock(), trainManager);
        this.signalBlock = signalBlock;
        this.trackViewerService = trackViewerService;
        this.blockNrToMonitore = getMonitoringBlockFunction().getBit();
    }

    @Override
    public void trainEnterBlock(int blockNumber, int trainAddress, boolean forward) {
    }

    @Override
    public void trainLeaveBlock(int blockNumber, int trainAddress, boolean forward) {
    }

    @Override
    public void blockOccupied(int blockNumber) {
        if (blockNumber == blockNrToMonitore) {
            trackViewerService.switchSignal(signalBlock.getSignal(), FUNCTION.HP0);
        }
    }

    @Override
    public void blockFreed(int blockNr) {
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
