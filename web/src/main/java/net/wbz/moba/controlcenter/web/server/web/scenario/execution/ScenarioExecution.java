package net.wbz.moba.controlcenter.web.server.web.scenario.execution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;

import net.wbz.moba.controlcenter.web.server.SelectrixHelper;
import net.wbz.moba.controlcenter.web.server.web.editor.block.BusAddressIdentifier;
import net.wbz.moba.controlcenter.web.server.web.editor.block.SignalBlockRegistry;
import net.wbz.moba.controlcenter.web.server.web.train.TrainManager;
import net.wbz.moba.controlcenter.web.shared.scenario.Route;
import net.wbz.moba.controlcenter.web.shared.scenario.RouteSequence;
import net.wbz.moba.controlcenter.web.shared.scenario.Scenario;
import net.wbz.moba.controlcenter.web.shared.scenario.Scenario.MODE;
import net.wbz.moba.controlcenter.web.shared.scenario.Scenario.RUN_STATE;
import net.wbz.moba.controlcenter.web.shared.track.model.BusDataConfiguration;
import net.wbz.moba.controlcenter.web.shared.track.model.Signal;
import net.wbz.moba.controlcenter.web.shared.track.model.Signal.FUNCTION;
import net.wbz.moba.controlcenter.web.shared.track.model.TrackBlock;
import net.wbz.moba.controlcenter.web.shared.train.Train;
import net.wbz.moba.controlcenter.web.shared.train.Train.DRIVING_DIRECTION;
import net.wbz.moba.controlcenter.web.shared.train.TrainService;
import net.wbz.moba.controlcenter.web.shared.viewer.TrackViewerService;
import net.wbz.selectrix4java.block.FeedbackBlockListener;
import net.wbz.selectrix4java.block.FeedbackBlockModule;
import net.wbz.selectrix4java.device.Device;
import net.wbz.selectrix4java.device.DeviceAccessException;
import net.wbz.selectrix4java.device.DeviceManager;

/**
 * TODO doc
 * 
 * @author Daniel Tuerk
 */
public abstract class ScenarioExecution {
    private static final Logger LOG = LoggerFactory.getLogger(ScenarioExecution.class);
    private static final int START_DELAY_MILLIS = 3000;
    private static final int DEFAULT_START_DRIVING_LEVEL = 4;

    private final Scenario scenario;
    private final TrackViewerService trackViewerService;
    private final TrainService trainService;
    private final SignalBlockRegistry signalBlockRegistry;
    private final DeviceManager deviceManager;
    private final Map<FeedbackBlockModule, List<FeedbackBlockListener>> feedbackBlockListeners =
            new ConcurrentHashMap<>();
    private final TrainManager trainManager;
    /**
     * Flag for stop called to scenario.
     */
    private volatile boolean stopped = false;
    /**
     * Flag for a execution of a {@link Route} in the {@link RouteSequence}.
     */
    private volatile boolean blockRunning = false;

    public ScenarioExecution(Scenario scenario, TrackViewerService trackViewerService, TrainService trainService,
            SignalBlockRegistry signalBlockRegistry, DeviceManager deviceManager, TrainManager trainManager) {
        this.scenario = scenario;
        this.trackViewerService = trackViewerService;
        this.trainService = trainService;
        this.signalBlockRegistry = signalBlockRegistry;
        this.deviceManager = deviceManager;
        this.trainManager = trainManager;
    }

    /**
     * Start the scenario and run all route sequence one after another by the order of the position.
     */
    public void start() throws InterruptedException {

        scenario.setRunState(RUN_STATE.RUNNING);
        fireScenarioStateChangeEvent(scenario);

        stopped = false;
        List<RouteSequence> routeSequences = scenario.getRouteSequences();
        Collections.sort(routeSequences, new Comparator<RouteSequence>() {
            @Override
            public int compare(RouteSequence o1, RouteSequence o2) {
                return Integer.compare(o1.getPosition(), o2.getPosition());
            }
        });

        LOG.info("start scenario {}", scenario);
        for (RouteSequence routeSequence : routeSequences) {
            if (stopped) {
                LOG.info("stop scenario {}", scenario);
                finishScenarioExecution();
                return;
            }
            LOG.info("start route sequence {}", routeSequence);

            blockRunning = true;
            try {
                prepare(routeSequence.getRoute());
            } catch (InterruptedException e) {
                LOG.error("execution error of route sequence at pos: " + routeSequence.getPosition(), e);
            } catch (DeviceAccessException e) {
                LOG.error("no device", e);
            }

            // wait TODO doc
            while (!stopped && blockRunning) {
                Thread.sleep(500L);

            }
            // currentPosition++;
            LOG.info("finished route sequence {}", routeSequence);
        }
        finishScenarioExecution();
    }

    private void finishScenarioExecution() {
        LOG.info("finished scenario {}", scenario);

        scenario.setMode(MODE.OFF);
        scenario.setRunState(RUN_STATE.STOPPED);

        fireScenarioStateChangeEvent(scenario);
    }

    /**
     * Stop the execution by the next route block in the route sequence.
     */
    public void stop() {
        stopped = true;
        stopTrain();

        tearDown();
    }

    /**
     * Callback for the run state change of the execution.
     *
     * @param scenario {@link Scenario}
     */
    protected abstract void fireScenarioStateChangeEvent(Scenario scenario);

    private void prepare(final Route route) throws InterruptedException, DeviceAccessException {
        // check for available train
        Train train = getTrain();
        if (!train.isCurrentlyInBlock(route.getStart())) {
            LOG.error("train {} not in start block {}!", new Object[] { train,
                    route.getStart() });
            stop();
            return;
        }

        Optional<Signal> signal = findStartSignal(route);
        if (!signal.isPresent()) {
            LOG.error("no signal found for start block {}!", new Object[] { route.getStart() });
            stop();
            return;
        }

        initRouteEndListener(route, train);

        prepareTrainToStart(train);

        requestFreeTrack(route, signal.get(), train);
    }

    private Train getTrain() {
        return trainManager.getTrain(scenario.getTrain().getId());
    }

    private void execute(Route route, Train train, Signal signal) throws InterruptedException {
        updateTrackForRoute(route);

        switchSignalToDrive(signal);

        startTrain(train, scenario.getStartDrivingLevel());
    }

    private Optional<Signal> findStartSignal(Route route) {
        for (Signal availableSignal : signalBlockRegistry.getSignals()) {
            // TODO why equals doesnt work?
            if (availableSignal.getStopBlock() != null && route.getStart() != null) {
                if (availableSignal.getStopBlock().getName().equals(route.getStart().getName())) {
                    return Optional.of(availableSignal);
                }
            }
        }
        return Optional.absent();
    }

    private void initRouteEndListener(final Route route, Train train) throws DeviceAccessException {
        Device device = deviceManager.getConnectedDevice();

        FeedbackBlockModule feedbackBlockModule = SelectrixHelper.getFeedbackBlockModule(device,
                new BusAddressIdentifier(route.getEnd().getBlockFunction()));

        feedbackBlockListeners.put(feedbackBlockModule, new ArrayList<FeedbackBlockListener>());

        FeedbackBlockListener feedbackBlockListener = new RouteEndFeedbackBlockListener(route, train) {
            @Override
            protected void trainEnterRouteEnd() {
                routeFinished();
            }
        };
        feedbackBlockListeners.get(feedbackBlockModule).add(feedbackBlockListener);

        feedbackBlockModule.addFeedbackBlockListener(feedbackBlockListener);
    }

    private void prepareTrainToStart(Train train) {
        trainService.toggleDrivingDirection(train.getId(),
                scenario.getTrainDrivingDirection() == DRIVING_DIRECTION.FORWARD);
        trainService.toggleLight(train.getId(), true);
    }

    private void requestFreeTrack(final Route route, final Signal signal, final Train train)
            throws DeviceAccessException, InterruptedException {

        TrackBlock startBlock = route.getStart();
        TrackBlock monitoringBlock = signal.getMonitoringBlock();
        TrackBlock endBlock = route.getEnd();

        // TODO implement stop
        FreeTrackListener freeTrackListener = new FreeTrackListener(monitoringBlock, endBlock,
                deviceManager.getConnectedDevice()) {
            @Override
            void ready() {
                try {
                    execute(route, train, signal);
                } catch (InterruptedException e) {
                    LOG.error("can't execute", e);
                }
            }
        };
        freeTrackListener.listen();
    }

    private void updateTrackForRoute(Route route) {
        // TODO allocate route or track necessary?
        updateTrack(scenario, route);
    }

    private void switchSignalToDrive(Signal signal) {
        LOG.info("switch signal to HP1 {}", signal);
        trackViewerService.switchSignal(signal, FUNCTION.HP1);
    }

    private void startTrain(Train train, Integer startDrivingLevel) throws InterruptedException {
        // delay the start of the train
        Thread.sleep(START_DELAY_MILLIS);
        // start train
        LOG.info("start train to drive {}", train);
        trainService.updateDrivingLevel(train.getId(), startDrivingLevel != null ? startDrivingLevel
                : DEFAULT_START_DRIVING_LEVEL);
    }

    private void routeFinished() {
        stopTrain();

        tearDown();

        blockRunning = false;
    }

    private void stopTrain() {
        trainService.updateDrivingLevel(getTrain(), 0);
    }

    private void tearDown() {
        for (FeedbackBlockModule feedbackBlockModule : feedbackBlockListeners.keySet()) {
            for (FeedbackBlockListener feedbackBlockListener : feedbackBlockListeners.get(feedbackBlockModule)) {
                feedbackBlockModule.removeFeedbackBlockListener(feedbackBlockListener);
            }
        }

    }

    /**
     * Update the track of the {@link Route} for the given {@link Scenario}.
     *
     * @param scenario running {@link Scenario}
     * @param route current {@link Route}
     */
    private void updateTrack(Scenario scenario, Route route) {
        LOG.info("update the track for scenario {} with start route: {}", scenario, route);
        Map<BusDataConfiguration, Boolean> trackPartStates = new HashMap<>();
        throw new NotImplementedException("track from route");
        // for (RouteBlockPart routeBlockPart : route.getRouteBlockParts()) {
        // if (routeBlockPart.getSwitchTrackPart() != null && routeBlockPart.getSwitchTrackPart()
        // .getToggleFunction() != null) {
        // trackPartStates.put(routeBlockPart.getSwitchTrackPart().getToggleFunction(), routeBlockPart
        // .isState());
        // }
        // }

        // trackViewerService.toggleTrackParts(trackPartStates);
    }
}
