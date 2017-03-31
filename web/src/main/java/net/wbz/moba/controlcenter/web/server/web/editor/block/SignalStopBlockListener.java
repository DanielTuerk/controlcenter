package net.wbz.moba.controlcenter.web.server.web.editor.block;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.wbz.moba.controlcenter.web.server.web.train.TrainManager;
import net.wbz.moba.controlcenter.web.shared.train.Train;
import net.wbz.moba.controlcenter.web.shared.train.TrainService;

/**
 * @author Daniel Tuerk
 */
class SignalStopBlockListener extends AbstractSignalStopBlockListener {
    private static final Logger log = LoggerFactory.getLogger(SignalStopBlockListener.class);

    SignalStopBlockListener(SignalBlock signalBlock,
            TrainManager trainManager, TrainService trainService) {
        super(signalBlock, trainManager, trainService);
    }

    @Override
    public void trainEnterBlock(int blockNumber, int trainAddress, boolean forward) {
        if (!getSignalBlock().isMonitoringBlockFree() && blockNumber == getTrackBlock().getBlockFunction()
                .getBit()) {
            log.debug("signal stop block {} - train enter {} (signal {})", new Object[] { blockNumber, trainAddress,
                    getSignalBlock().getSignal().getSignalConfigRed1() });
            Train train = getTrain(trainAddress);

            getSignalBlock().setTrainInStopBlock(train);

            if (train != null && getSignalBlock().getTrainInMonitoringBlock() != train) {
                getSignalBlock().setWaitingTrain(train);
                getTrainService().updateDrivingLevel(train.getId(), 0);
            }
        }
    }

    @Override
    public void trainLeaveBlock(int blockNumber, int trainAddress, boolean forward) {
        if (blockNumber == getTrackBlock().getBlockFunction().getBit()) {
            log.debug("signal stop block {} - train leave {} (signal {})", new Object[] { blockNumber, trainAddress,
                    getSignalBlock().getSignal().getSignalConfigRed1() });

            getSignalBlock().setTrainInStopBlock(null);

            if (getTrain(trainAddress) == getSignalBlock().getWaitingTrain()) {
                getSignalBlock().setWaitingTrain(null);
            }
        }
    }

}
