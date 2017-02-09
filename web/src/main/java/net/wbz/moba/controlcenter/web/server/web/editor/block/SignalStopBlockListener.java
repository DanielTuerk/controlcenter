package net.wbz.moba.controlcenter.web.server.web.editor.block;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.wbz.moba.controlcenter.web.server.web.train.TrainManager;
import net.wbz.moba.controlcenter.web.shared.train.Train;
import net.wbz.moba.controlcenter.web.shared.train.TrainService;
import net.wbz.moba.controlcenter.web.shared.viewer.TrackViewerService;

/**
 * @author Daniel Tuerk
 */
public class SignalStopBlockListener extends AbstractSignalBlockListener {
    private static final Logger log = LoggerFactory.getLogger(SignalStopBlockListener.class);

    private final SignalBlock signalBlock;
    private final TrackViewerService trackViewerService;
    private final TrainManager trainManager;
    private final TrainService trainService;

    public SignalStopBlockListener(SignalBlock signalBlock, TrackViewerService trackViewerService,
            TrainManager trainManager, TrainService trainService) {
        super(signalBlock.getSignal().getStopBlock());
        this.signalBlock = signalBlock;
        this.trackViewerService = trackViewerService;
        this.trainManager = trainManager;
        this.trainService = trainService;
    }

    @Override
    public void trainEnterBlock(int blockNumber, int trainAddress, boolean forward) {
        if (!signalBlock.isMonitoringBlockFree() && blockNumber == getTrackBlock().getBlockFunction()
                .getBit()) {
            log.debug("signal stop block {} - train enter {} (signal {})", new Object[] { blockNumber, trainAddress,
                    signalBlock.getSignal().getSignalConfigRed1() });
            Train train = trainManager.getTrain(trainAddress);
            if (train != null) {
                signalBlock.setWaitingTrain(train);
                trainService.updateDrivingLevel(train.getId(), 0);
            }
        }
    }

    @Override
    public void trainLeaveBlock(int blockNumber, int trainAddress, boolean forward) {
        if (blockNumber == getTrackBlock().getBlockFunction().getBit()) {
            log.debug("signal stop block {} - train leave {} (signal {})", new Object[] { blockNumber, trainAddress,
                    signalBlock.getSignal().getSignalConfigRed1() });
            signalBlock.setWaitingTrain(null);
            // Train train = signalBlock.getWaitingTrains();
            // if (train != null) {
            // getTrainService().updateDrivingLevel(train.getId(), DRIVING_LEVEL_START);
            // }
        }
    }

    @Override
    public void blockOccupied(int blockNr) {
        if (blockNr == getTrackBlock().getBlockFunction().getBit()) {
            // signal.setMonitoringBlockFree(false);
        }
    }

    @Override
    public void blockFreed(int blockNr) {
        if (blockNr == getTrackBlock().getBlockFunction().getBit()) {
            // signal.setMonitoringBlockFree(true);
        }
    }
}
