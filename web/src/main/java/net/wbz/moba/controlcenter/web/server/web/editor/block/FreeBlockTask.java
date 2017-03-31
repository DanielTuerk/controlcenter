package net.wbz.moba.controlcenter.web.server.web.editor.block;

import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.wbz.moba.controlcenter.web.shared.track.model.Signal.FUNCTION;
import net.wbz.moba.controlcenter.web.shared.train.Train;
import net.wbz.moba.controlcenter.web.shared.train.TrainService;
import net.wbz.moba.controlcenter.web.shared.viewer.TrackViewerService;

/**
 * {@link Callable} to switch the signal to
 * {@link net.wbz.moba.controlcenter.web.shared.track.model.Signal.FUNCTION#HP1} and start the waiting train on a
 * {@link SignalBlock}.
 * 
 * @author Daniel Tuerk
 */
final class FreeBlockTask implements Callable<Void> {
    private static final Logger log = LoggerFactory.getLogger(FreeBlockTask.class);

    /**
     * TODO move to config
     */
    public static final int DRIVING_LEVEL_START = 4;
    /**
     * TODO move to config
     */
    private static final long SIGNAL_DELAY = 3000L;

    private final SignalBlock signalBlock;
    private final TrainService trainService;
    private final TrackViewerService trackViewerService;
    private final int startDrivingLevel;

    FreeBlockTask(SignalBlock signalBlock, TrainService trainService, TrackViewerService trackViewerService,
            int startDrivingLevel) {
        this.signalBlock = signalBlock;
        this.trainService = trainService;
        this.trackViewerService = trackViewerService;
        this.startDrivingLevel = startDrivingLevel;
    }

    @Override
    public Void call() throws Exception {
        // switch signal to drive
        log.debug("switch signal to HP1 {}", signalBlock.getSignal());
        trackViewerService.switchSignal(signalBlock.getSignal(), FUNCTION.HP1);

        Train train = signalBlock.getWaitingTrain();
        if (train != null) {
            // delay the start of the train
            Thread.sleep(SIGNAL_DELAY);
            // start train
            log.debug("start train to drive {}", train);
            trainService.updateDrivingLevel(train.getId(), startDrivingLevel);
        }
        // no more action, clearing the signal and train is handled by the block listeners
        return null;
    }
}
