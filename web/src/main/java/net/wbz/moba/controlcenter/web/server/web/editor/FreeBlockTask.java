package net.wbz.moba.controlcenter.web.server.web.editor;

import java.util.concurrent.Callable;
import net.wbz.moba.controlcenter.web.server.web.train.TrainServiceImpl;
import net.wbz.moba.controlcenter.web.shared.track.model.Signal.FUNCTION;
import net.wbz.moba.controlcenter.web.shared.train.Train;
import net.wbz.moba.controlcenter.web.shared.viewer.TrackViewerService;

/**
 * @author Daniel Tuerk
 */
final class FreeBlockTask implements Callable<Void> {

    public static final long SIGNAL_DELAY = 3000L;
    private final SignalBlock signalBlock;
    private final TrainServiceImpl trainService;
    private final TrackViewerService trackViewerService;

    FreeBlockTask(SignalBlock signalBlock, TrainServiceImpl trainService,
        TrackViewerService trackViewerService) {
        this.signalBlock = signalBlock;
        this.trainService = trainService;
        this.trackViewerService = trackViewerService;
    }

    @Override
    public Void call() throws Exception {
        // switch signal to drive
        trackViewerService.switchSignal(signalBlock.getSignal(), FUNCTION.HP1);

        Train train = signalBlock.getWaitingTrain();
        if (train != null) {
            // delay the start of the train
            Thread.sleep(SIGNAL_DELAY);
            // start train
            trainService.updateDrivingLevel(train.getId(), SignalBlockRegistry.DRIVING_LEVEL_START);
        }
        return null;
    }
}
