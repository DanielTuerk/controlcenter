package net.wbz.moba.controlcenter.web.server.web.scenario.execution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;

import net.wbz.moba.controlcenter.web.server.SelectrixHelper;
import net.wbz.moba.controlcenter.web.server.persist.scenario.TrackBuilder;
import net.wbz.moba.controlcenter.web.server.persist.scenario.TrackBuilder.TrackNotFoundException;
import net.wbz.moba.controlcenter.web.server.web.editor.block.BusAddressIdentifier;
import net.wbz.moba.controlcenter.web.server.web.editor.block.SignalBlockRegistry;
import net.wbz.moba.controlcenter.web.server.web.scenario.ScenarioManager;
import net.wbz.moba.controlcenter.web.server.web.train.TrainManager;
import net.wbz.moba.controlcenter.web.shared.scenario.Route;
import net.wbz.moba.controlcenter.web.shared.scenario.RouteSequence;
import net.wbz.moba.controlcenter.web.shared.scenario.Scenario;
import net.wbz.moba.controlcenter.web.shared.scenario.Scenario.MODE;
import net.wbz.moba.controlcenter.web.shared.scenario.Scenario.RUN_STATE;
import net.wbz.moba.controlcenter.web.shared.scenario.Track;
import net.wbz.moba.controlcenter.web.shared.track.model.BusDataConfiguration;
import net.wbz.moba.controlcenter.web.shared.track.model.Signal;
import net.wbz.moba.controlcenter.web.shared.track.model.Signal.FUNCTION;
import net.wbz.moba.controlcenter.web.shared.train.Train;
import net.wbz.moba.controlcenter.web.shared.train.Train.DRIVING_DIRECTION;
import net.wbz.moba.controlcenter.web.shared.train.TrainService;
import net.wbz.moba.controlcenter.web.shared.viewer.TrackViewerService;
import net.wbz.selectrix4java.block.BlockListener;
import net.wbz.selectrix4java.block.FeedbackBlockModule;
import net.wbz.selectrix4java.device.Device;
import net.wbz.selectrix4java.device.DeviceAccessException;
import net.wbz.selectrix4java.device.DeviceManager;

/**
 * The execution of a {@link Scenario} started by the {@link ScenarioExecutor}.
 * Update the run state of the {@link Scenario} and start the {@link Route}s after each other.
 * The execution can be stopped between routes.
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
    private final Map<FeedbackBlockModule, List<BlockListener>> blockListeners =
            new ConcurrentHashMap<>();
    private final TrainManager trainManager;
    private final TrackBuilder trackBuilder;
    private final ScenarioManager scenarioManager;
    /**
     * Flag for stop called to scenario.
     */
    private volatile boolean stopped = false;
    /**
     * Flag for a execution of a {@link Route} in the {@link RouteSequence}.
     */
    private volatile boolean blockRunning = false;

    public ScenarioExecution(Scenario scenario, TrackViewerService trackViewerService, TrainService trainService,
            SignalBlockRegistry signalBlockRegistry, DeviceManager deviceManager, TrainManager trainManager,
            TrackBuilder trackBuilder, ScenarioManager scenarioManager) {
        this.scenario = scenario;
        this.trackViewerService = trackViewerService;
        this.trainService = trainService;
        this.signalBlockRegistry = signalBlockRegistry;
        this.deviceManager = deviceManager;
        this.trainManager = trainManager;
        this.trackBuilder = trackBuilder;
        this.scenarioManager = scenarioManager;
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

        buildTracks();
        boolean isFirstRoute = true;
        for (RouteSequence routeSequence : routeSequences) {
            if (stopped) {
                LOG.info("stop scenario {}", scenario);
                finishScenarioExecution();
                return;
            }
            scenarioManager.routeStarted(routeSequence);
            LOG.info("start route sequence {}", routeSequence);

            blockRunning = true;
            try {
                prepare(routeSequence.getRoute(), isFirstRoute);
            } catch (InterruptedException e) {
                LOG.error("execution error of route sequence at pos: " + routeSequence.getPosition(), e);
            } catch (DeviceAccessException e) {
                LOG.error("no device", e);
            }

            // wait for next route block action and check the stop flag
            while (!stopped && blockRunning) {
                Thread.sleep(200L);

            }
            LOG.info("finished route sequence {}", routeSequence);
            scenarioManager.routeFinished(routeSequence);
            isFirstRoute = false;
        }
        finishScenarioExecution();
    }

    /**
     * Stop the execution by the next route block in the route sequence.
     */
    public void stop() {
        stopped = true;
        stopTrain();

        tearDown();
    }

    private void buildTracks() {
        LOG.info("build track for routes");
        for (RouteSequence routeSequence : scenario.getRouteSequences()) {
            try {
                Track track = trackBuilder.build(routeSequence.getRoute());
                routeSequence.getRoute().setTrack(track);
            } catch (TrackNotFoundException e) {
                LOG.error("can't build track for route {}.", routeSequence.getRoute(), e);
            }
        }
        LOG.info("finished track for routes", scenario);
    }

    private void finishScenarioExecution() {
        LOG.info("finished scenario {}", scenario);

        scenario.setMode(MODE.OFF);
        scenario.setRunState(RUN_STATE.STOPPED);

        fireScenarioStateChangeEvent(scenario);
    }

    /**
     * Callback for the run state change of the execution.
     *
     * @param scenario {@link Scenario}
     */
    protected abstract void fireScenarioStateChangeEvent(Scenario scenario);

    /**
     * Prepare the {@link Route} for drive.
     * 
     * @param route {@link Route}
     * @param isFirstRoute {@code true} if this is the check for the first route in the execution
     * @throws InterruptedException
     * @throws DeviceAccessException
     */
    private void prepare(final Route route, boolean isFirstRoute) throws InterruptedException, DeviceAccessException {
        /*
         * Check for available train in the first route block. Others are saved by the execution. An explicit check
         * can't be performed because the feedback could be too late.
         */
        Train train = getTrain();

        if (isFirstRoute && !train.isCurrentlyInBlock(route.getStart())) {
            LOG.error("train {} not in start block {}!", new Object[] { train,
                    route.getStart() });
            stop();
            return;
        }

        // find start position on signal
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
            if (availableSignal.getStopBlock() != null && route.getStart() != null) {
                // TODO check equals working?
                if (availableSignal.getStopBlock().equals(route.getStart())) {
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

        blockListeners.put(feedbackBlockModule, new ArrayList<BlockListener>());

        BlockListener feedbackBlockListener = new RouteEndBlockListener(route) {
            @Override
            protected void trainEnterRouteEnd() {
                routeFinished();
            }
        };
        blockListeners.get(feedbackBlockModule).add(feedbackBlockListener);

        feedbackBlockModule.addBlockListener(feedbackBlockListener);
    }

    private void prepareTrainToStart(Train train) {
        trainService.toggleDrivingDirection(train.getId(),
                scenario.getTrainDrivingDirection() == DRIVING_DIRECTION.FORWARD);
        trainService.toggleLight(train.getId(), true);
    }

    private void requestFreeTrack(final Route route, final Signal signal, final Train train)
            throws DeviceAccessException, InterruptedException {
        LOG.info("train ({}) request free track: {}", train, route);
        // TODO implement stop

        // TODO reference to running routes by new object !!!!!
        FreeTrackListener freeTrackListener = new FreeTrackListener(route, deviceManager.getConnectedDevice(),
                scenarioManager) {
            @Override
            void ready() {
                LOG.info("track ({}) free for train: ", route, train);
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
        for (FeedbackBlockModule feedbackBlockModule : blockListeners.keySet()) {
            for (BlockListener feedbackBlockListener : blockListeners.get(feedbackBlockModule)) {
                feedbackBlockModule.removeBlockListener(feedbackBlockListener);
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
        for (BusDataConfiguration routeBlockPart : route.getTrack().getTrackFunctions()) {
            trackPartStates.put(routeBlockPart, routeBlockPart.getBitState());
        }
        trackViewerService.toggleTrackParts(trackPartStates);
    }
}
