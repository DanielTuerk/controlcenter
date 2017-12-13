package net.wbz.moba.controlcenter.web.server.web.editor.block;

import java.util.concurrent.Callable;

import org.apache.commons.lang.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;

import net.wbz.moba.controlcenter.web.server.web.scenario.ScenarioServiceImpl;
import net.wbz.moba.controlcenter.web.shared.scenario.Scenario;
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
    /**
     * TODO move to config
     */
    private static final int DRIVING_LEVEL_START = 4;
    private static final Logger log = LoggerFactory.getLogger(FreeBlockTask.class);
    /**
     * TODO move to config
     */
    private static final long SIGNAL_DELAY = 3000L;

    private final SignalBlock signalBlock;
    private final TrainService trainService;
    private final TrackViewerService trackViewerService;
    private final Optional<Scenario> scenario;
    private final ScenarioServiceImpl scenarioService;

    FreeBlockTask(SignalBlock signalBlock, TrainService trainService, TrackViewerService trackViewerService,
            Optional<Scenario> scenario, ScenarioServiceImpl scenarioService) {
        this.signalBlock = signalBlock;
        this.trainService = trainService;
        this.trackViewerService = trackViewerService;
        this.scenario = scenario;
        this.scenarioService = scenarioService;
    }

    @Override
    public Void call() throws Exception {
        // switch signal to drive
        throw new NotImplementedException("no more");
        // log.debug("switch signal to HP1 {}", signalBlock.getSignal());
        // trackViewerService.switchSignal(signalBlock.getSignal(), FUNCTION.HP1);
        //
        // int startDrivingLevel = FreeBlockTask.DRIVING_LEVEL_START;
        //
        // // check for scenario to run
        // if (scenario.isPresent()) {
        // // TODO allocate route or track necessary?
        // scenarioService.updateTrack(scenario.get(), signalBlock.getSignal());
        //
        // if (scenario.get().getStartDrivingLevel() != null) {
        // startDrivingLevel = scenario.get().getStartDrivingLevel();
        // }
        // }
        //
        // // start waiting train on signal
        // Train train = signalBlock.getWaitingTrain();
        // if (train != null) {
        // // delay the start of the train
        // Thread.sleep(SIGNAL_DELAY);
        // // start train
        // log.debug("start train to drive {}", train);
        // trainService.updateDrivingLevel(train.getId(), startDrivingLevel);
        // }
        // // no more action, clearing the signal and train is handled by the block listeners
        // return null;
    }
}
