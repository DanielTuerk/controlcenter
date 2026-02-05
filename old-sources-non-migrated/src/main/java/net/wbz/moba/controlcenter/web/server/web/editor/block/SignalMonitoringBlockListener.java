package net.wbz.moba.controlcenter.web.server.web.editor.block;

import net.wbz.moba.controlcenter.web.server.web.train.TrainManager;
import net.wbz.moba.controlcenter.web.server.web.viewer.TrackViewerService;
import net.wbz.moba.controlcenter.shared.track.model.BusDataConfiguration;
import net.wbz.moba.controlcenter.shared.track.model.Signal.FUNCTION;
import org.jboss.logging.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * React to a occupied monitoring block and switch the {@link net.wbz.moba.controlcenter.web.shared.track.model.Signal}
 * to {@link FUNCTION#HP0}.
 * </p>
 *
 * @author Daniel Tuerk
 */
class SignalMonitoringBlockListener extends AbstractSignalBlockListener {

    private static final Logger LOG = Logger.getLogger(SignalMonitoringBlockListener.class);
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
        final StringBuffer sb = new StringBuffer("SignalMonitoringBlockListener{");
        sb.append("signalBlock=").append(signalBlock);
        sb.append(", blockNrToMonitore=").append(blockNrToMonitore);
        sb.append('}');
        return sb.toString();
    }

    private BusDataConfiguration getMonitoringBlockFunction() {
        return signalBlock.getSignal().getMonitoringBlock().getBlockFunction();
    }

}
