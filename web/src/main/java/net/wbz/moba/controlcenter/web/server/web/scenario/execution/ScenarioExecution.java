package net.wbz.moba.controlcenter.web.server.web.scenario.execution;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.validation.constraints.NotNull;
import net.wbz.moba.controlcenter.web.server.SelectrixHelper;
import net.wbz.moba.controlcenter.web.server.persist.scenario.TrackBuilder;
import net.wbz.moba.controlcenter.web.server.web.editor.block.BusAddressIdentifier;
import net.wbz.moba.controlcenter.web.server.web.editor.block.SignalBlockRegistry;
import net.wbz.moba.controlcenter.web.server.web.scenario.RouteListener;
import net.wbz.moba.controlcenter.web.server.web.train.TrainManager;
import net.wbz.moba.controlcenter.web.shared.scenario.Route;
import net.wbz.moba.controlcenter.web.shared.scenario.Route.ROUTE_RUN_STATE;
import net.wbz.moba.controlcenter.web.shared.scenario.RouteSequence;
import net.wbz.moba.controlcenter.web.shared.scenario.Scenario;
import net.wbz.moba.controlcenter.web.shared.scenario.Scenario.RUN_STATE;
import net.wbz.moba.controlcenter.web.shared.track.model.BusDataConfiguration;
import net.wbz.moba.controlcenter.web.shared.track.model.Signal;
import net.wbz.moba.controlcenter.web.shared.track.model.Signal.FUNCTION;
import net.wbz.moba.controlcenter.web.shared.track.model.TrackBlock;
import net.wbz.moba.controlcenter.web.shared.train.Train;
import net.wbz.moba.controlcenter.web.shared.train.Train.DRIVING_DIRECTION;
import net.wbz.moba.controlcenter.web.shared.train.TrainService;
import net.wbz.moba.controlcenter.web.shared.viewer.TrackViewerService;
import net.wbz.selectrix4java.block.BlockListener;
import net.wbz.selectrix4java.block.FeedbackBlockModule;
import net.wbz.selectrix4java.device.Device;
import net.wbz.selectrix4java.device.DeviceAccessException;
import net.wbz.selectrix4java.device.DeviceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The execution of a {@link Scenario} started by the {@link ScenarioExecutor}. Update the run state of the {@link
 * Scenario} and start the {@link Route}s after each other. The execution can be stopped between routes.
 *
 * @author Daniel Tuerk
 */
abstract class ScenarioExecution implements Callable<Void> {

    private static final Logger LOG = LoggerFactory.getLogger(ScenarioExecution.class);
    private static final int START_DELAY_MILLIS = 3000;
    private static final int DEFAULT_START_DRIVING_LEVEL = 8;
    private static final long MILLIS_TO_WAIT_IN_BLOCK_RUN = 200L;
    private static final long HP0_AFTER_TRAIN_PASS_DELAY_IN_MILLIS = 10000L;

    private final Scenario scenario;
    private final TrackViewerService trackViewerService;
    private final TrainService trainService;
    private final SignalBlockRegistry signalBlockRegistry;
    private final DeviceManager deviceManager;
    private final Map<FeedbackBlockModule, List<BlockListener>> blockListeners = new ConcurrentHashMap<>();
    private final TrainManager trainManager;
    private final TrackBuilder trackBuilder;
    private final List<RouteListener> routeListeners;
    private final RouteExecutionObserver routeExecutionObserver;
//    private final ExecutorService executor;
    /**
     * Flag for stop called to scenario.
     */
    private volatile boolean stopped = false;
    /**
     * Flag for a execution of a {@link Route} in the {@link RouteSequence}.
     */
    private volatile boolean blockRunning = false;

    ScenarioExecution(Scenario scenario, TrackViewerService trackViewerService, TrainService trainService,
        SignalBlockRegistry signalBlockRegistry, DeviceManager deviceManager, TrainManager trainManager,
        TrackBuilder trackBuilder, List<RouteListener> routeListeners,
        RouteExecutionObserver routeExecutionObserver) {
        this.scenario = scenario;
        this.trackViewerService = trackViewerService;
        this.trainService = trainService;
        this.signalBlockRegistry = signalBlockRegistry;
        this.deviceManager = deviceManager;
        this.trainManager = trainManager;
        this.trackBuilder = trackBuilder;
        this.routeListeners = routeListeners;
        this.routeExecutionObserver = routeExecutionObserver;

//        executor = Executors.poolnewFixedThreadPool();
//        return new ThreadPoolExecutor(nThreads, nThreads,
//            0L, TimeUnit.MILLISECONDS,
//            new LinkedBlockingQueue<Runnable>());
    }

    @Override
    public Void call() throws Exception {
        start();
        return null;
    }

    /**
     * Start the scenario and run all route sequence one after another by the order of the position.
     */
    void start() {
        try {
            scenario.setRunState(RUN_STATE.RUNNING);
            fireScenarioStateChangeEvent(scenario);

            stopped = false;

            // sort routes by position
            List<RouteSequence> routeSequences = scenario.getRouteSequences();
            routeSequences.sort(Comparator.comparingInt(RouteSequence::getPosition));

            LOG.info("start scenario {}", scenario);
            // flag for the first route to try to start
            boolean isFirstRoute = true;
            // indicate that just a single route started for execution
            boolean isAnyRouteRunning = false;
            // previous executed route
            RouteSequence previousRouteSequence = null;
            for (RouteSequence routeSequence : routeSequences) {
                if (stopped) {
                    LOG.info("stop scenario {}", scenario);
                    finishScenarioExecution(RUN_STATE.STOPPED);
                    cleanupRunningRouteInSequence(previousRouteSequence);
                    return;
                }

                LOG.info("start route sequence {}", routeSequence);

                blockRunning = true;
                // get next route
                int nextIndex = routeSequences.indexOf(routeSequence) + 1;
                RouteSequence nextRouteSequence =
                    nextIndex < routeSequences.size() ? routeSequences.get(nextIndex) : null;
                try {
                    RouteExecution routeExecution = prepare(routeSequence, previousRouteSequence, nextRouteSequence,
                        isFirstRoute, isAnyRouteRunning);

                    previousRouteSequence = routeSequence;

                    // TODO die nächsrten beiden werden niocht für start und ende 2er routen aufgerufen

                    // check track for running scenarios and wait for the free track
                    waitForFreeTrack(routeExecution);

                    // check all track blocks are free for the route, also by manual drive or lost wagons
                    checkForFreeBlocks(routeSequence.getRoute().getAllTrackBlocksToDrive());

                    startRoute(routeExecution);

                    // execution started with any route
                    isAnyRouteRunning = true;

                    // wait until the route is running and check the scenario stop flag
                    try {
                        while (!stopped && blockRunning) {
                            Thread.sleep(MILLIS_TO_WAIT_IN_BLOCK_RUN);
                        }
                    } catch (InterruptedException e) {
                        throw new RouteExecutionInterruptException("block run interrupted", e);
                    }

                    finishRoute(routeSequence);
                } catch (ScenarioExecutionInterruptException | RouteExecutionInterruptException e) {
                    String msg = "execution error of route sequence at pos: " + routeSequence.getPosition();
                    LOG.error(msg, e);
                    fireRouteErrorState(routeSequence, e.getMessage());
                    // scenario can't be executed and will be stopped
                    stop();
                } catch (NoTrainInStartBlockException e) {
                    LOG.info(e.getMessage());
                    fireRouteErrorState(routeSequence, e.getMessage());
                    cleanupRunningRouteInSequence(routeSequence);
                    // route interrupted but try to execute next route
                } finally {
                    tearDownRouteExecution();
                }
                LOG.info("finished route sequence {}", routeSequence);

                isFirstRoute = false;
            }
            /*
              Remove the last running for the end of the scenario sequence.
              Otherwise it will be only removed for the start of next route in the sequence which is'nt available for
              the
              last route.
             */
            cleanupRunningRouteInSequence(previousRouteSequence);

        } catch (Exception e) {
            // catch unexpected errors and stop the scenario to also stop the trains
            LOG.error("error in by scenario execution of: " + scenario, e);
            stop();
        } finally {
            // do it for success or error to be able to restart the scenario
            finishScenarioExecution(RUN_STATE.FINISHED);
        }
    }

    private void cleanupRunningRouteInSequence(RouteSequence previousRouteSequence) {
        routeExecutionObserver.removeRunningRouteSequence(previousRouteSequence);
    }

    /**
     * Stop the execution by the next route block in the route sequence.
     */
    void stop() {
        LOG.info("stop");
        stopped = true;
        stopTrain();
        scenario.setRunState(RUN_STATE.STOPPED);
    }

    private void startRoute(final RouteExecution routeExecution) throws ScenarioExecutionInterruptException,
        RouteExecutionInterruptException {
        RouteSequence routeSequence = routeExecution.getRouteSequence();

        try {
            initRouteEndListener(routeExecution);
        } catch (DeviceAccessException e) {
            throw new ScenarioExecutionInterruptException("no device connection", e);
        }

        updateTrack(scenario, routeSequence.getRoute());
        Optional.ofNullable(routeExecution.getSignal()).ifPresent(this::switchSignalToDrive);

        final Train train = routeExecution.getTrain();
        if (train.getDrivingLevel() <= 0) {
            try {
                startTrain(train, scenario.getStartDrivingLevel());
            } catch (InterruptedException e) {
                throw new RouteExecutionInterruptException("start route interrupted", e);
            }
        }

        reserveNextRouteForCurrentRoute(routeExecution);

        for (RouteListener routeListener : routeListeners) {
            routeListener.routeStarted(scenario, routeSequence);
        }

        // register block listener for each block on the track to try to reserve the next route immediately
        for (TrackBlock trackBlock : routeExecution.getRouteSequence().getRoute().getTrack().getTrackBlocks()) {
            try {
                addBlockListener(new BlockListener() {
                    @Override
                    public void blockOccupied(int blockNr) {
                        reserveNextRouteForCurrentRoute(routeExecution);
                    }

                    @Override
                    public void blockFreed(int blockNr) {
                    }
                }, trackBlock.getBlockFunction());
            } catch (DeviceAccessException e) {
                LOG.error("reserve next route", e);
            }
        }

    }

    /**
     * Try to reserve next route. Turnout start signal of next route also to HP1 to drive through from the end of the
     * current route.
     *
     * @param routeExecution {@link RouteExecution}
     */
    private void reserveNextRouteForCurrentRoute(RouteExecution routeExecution) {
        if (routeExecution.getNextRouteSequence() != null && routeExecutionObserver
            .checkAndReserveNextRunningRoute(routeExecution.getNextRouteSequence(),
                routeExecution.getRouteSequence())) {

            Optional<Signal> startSignal = findStartSignal(routeExecution.getNextRouteSequence().getRoute());
            startSignal.ifPresent(signal -> trackViewerService.switchSignal(signal, FUNCTION.HP1));
        }
    }

    private void finishRoute(RouteSequence routeSequence) {
        routeSequence.getRoute().setRunState(ROUTE_RUN_STATE.FINISHED);
        for (RouteListener routeListener : routeListeners) {
            routeListener.routeFinished(scenario, routeSequence);
        }
    }

    private void fireRouteErrorState(RouteSequence routeSequence, String msg) {
        for (RouteListener routeListener : routeListeners) {
            routeListener.routeFailed(scenario, routeSequence, msg);
        }
    }

    private void finishScenarioExecution(RUN_STATE runState) {
        LOG.info("finished scenario {}", scenario);

        scenario.setRunState(runState);

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
     * @param routeSequence {@link RouteSequence}
     * @param previousRouteSequence previous executed {@link RouteSequence}
     * @param nextRouteSequence next {@link RouteSequence} in scenario
     * @param isFirstRoute {@code true} if this is the check for the first route in the execution
     * @param isAnyRouteRunning {@code false} until any route was successful prepared @throws
     * ScenarioExecutionInterruptException interrupt of scenario
     */
    private RouteExecution prepare(final RouteSequence routeSequence, final RouteSequence previousRouteSequence,
        RouteSequence nextRouteSequence, boolean isFirstRoute, boolean isAnyRouteRunning) throws
        NoTrainInStartBlockException {
        Route route = routeSequence.getRoute();

        Train train = getTrain();

        // detect train
        if ((isFirstRoute || !isAnyRouteRunning) && trainIsNotInStartBlock(route, train)) {
            /*
             * Check for available train in the first route block to be executed. Others are saved by the execution.
             * An explicit check of each route block can't be performed because the feedback could be too late.
             */
            throw new NoTrainInStartBlockException(train, route.getStart());
        }

        // find start position on signal

        prepareTrainToStart(train);
        if (routeSequence.getRoute().getRunState() != ROUTE_RUN_STATE.RESERVED) {
            routeSequence.getRoute().setRunState(ROUTE_RUN_STATE.PREPARED);
        }
        Optional<Signal> signal = findStartSignal(route);
        return new RouteExecution(routeSequence, previousRouteSequence, nextRouteSequence, train, signal.orElse(null));
    }

    private boolean trainIsNotInStartBlock(Route route, Train train) {
        return !train.isCurrentlyInBlock(route.getStart().getLeftTrackBlock(), route.getStart().getMiddleTrackBlock(),
            route.getStart().getRightTrackBlock());
    }

    /**
     * Check that all given {@link TrackBlock}s are free. If not a exception is thrown.
     *
     * @param trackBlocks {@link TrackBlock}s to check
     * @throws ScenarioExecutionInterruptException no connection
     * @throws RouteExecutionInterruptException blocks not free
     */
    private void checkForFreeBlocks(Set<TrackBlock> trackBlocks) throws RouteExecutionInterruptException,
        ScenarioExecutionInterruptException {
        try {
            Device device = deviceManager.getConnectedDevice();
            for (TrackBlock trackBlock : trackBlocks) {
                BusDataConfiguration blockFunction = trackBlock.getBlockFunction();
                BusAddressIdentifier entry = new BusAddressIdentifier(blockFunction);
                if (SelectrixHelper.getFeedbackBlockModule(device, entry)
                    .getLastReceivedBlockState(blockFunction.getBit())) {
                    throw new RouteExecutionInterruptException(
                        String.format("track not free at block: %d (%d) ", entry.getAddress(),
                            blockFunction.getBit()));
                }
            }
        } catch (DeviceAccessException e) {
            throw new ScenarioExecutionInterruptException("no connected device", e);
        }
    }

    private Train getTrain() {
        return trainManager.getTrain(scenario.getTrain().getId());
    }

    /**
     * Find start signal of {@link Route} start block.
     *
     * TODO
     *
     * @param route {@link Route}
     * @return {@link Signal}
     * @deprecated aktuell findet der 2 an beiden enden und gibt nur 1 zurück.
     */
    @Deprecated
    private Optional<Signal> findStartSignal(Route route) {
        for (Signal availableSignal : signalBlockRegistry.getSignals()) {
            if (availableSignal.getStopBlock() != null && route.getStart() != null) {
                if (route.getStart().getAllTrackBlocks().contains(availableSignal.getStopBlock())) {
                    return Optional.of(availableSignal);
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Add the {@link RouteEndBlockListener} to indicate the end of the route and throw the state for the finished
     * execution.
     *
     * @param routeExecution {@link Route} to detect the end of execution
     * @throws DeviceAccessException no access
     */
    private void initRouteEndListener(final RouteExecution routeExecution) throws DeviceAccessException {
        Route route = routeExecution.getRouteSequence().getRoute();
        BlockListener blockListener = new RouteEndBlockListener(route) {
            @Override
            protected void trainEnterRouteEnd() {
                LOG.info("route finished {}", routeExecution);
                routeFinished(routeExecution.getNextRouteSequence());
            }
        };
        addBlockListener(blockListener, route.getEnd().getBlockFunction());
    }

    private void addBlockListener(BlockListener listener, BusDataConfiguration busDataConfiguration) throws
        DeviceAccessException {
        Device device = deviceManager.getConnectedDevice();
        FeedbackBlockModule feedbackBlockModule = SelectrixHelper
            .getFeedbackBlockModule(device, new BusAddressIdentifier(busDataConfiguration));

        if (!blockListeners.containsKey(feedbackBlockModule)) {
            blockListeners.put(feedbackBlockModule, new ArrayList<>());
        }

        blockListeners.get(feedbackBlockModule).add(listener);

        feedbackBlockModule.addBlockListener(listener);
    }

    private void prepareTrainToStart(Train train) {
        trainService.toggleDrivingDirection(train.getId(),
            scenario.getTrainDrivingDirection() == DRIVING_DIRECTION.FORWARD);
        trainService.toggleLight(train.getId(), true);
    }

    private void waitForFreeTrack(RouteExecution routeExecution) throws ScenarioExecutionInterruptException {
        for (RouteListener routeListener : routeListeners) {
            routeListener.routeWaitingToStart(scenario, routeExecution.getRouteSequence());
        }

//        Future<Void> future = executor
//            .submit(new WaitForFreeTrackCallable(routeExecutionObserver, scenario, routeExecution));
        Thread thread = new Thread(new WaitForFreeTrackCallable(routeExecutionObserver, scenario, routeExecution));
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
//        } catch (InterruptedException | ExecutionException e) {
            throw new ScenarioExecutionInterruptException("wait for free track interrupted", e);
        }
    }

    private void switchSignalToDrive(@NotNull final Signal signal) {
        LOG.info("switch signal to HP1 {}", signal);
        trackViewerService.switchSignal(signal, FUNCTION.HP1);

        if (signal.getMonitoringBlock() == null || signal.getMonitoringBlock().getBlockFunction() == null) {
            // signal have no monitoring block and will never get the HP0 after drive
            new Thread(() -> {
                try {
                    /*
                     * Set to HP0 after delay to simulate a occupied monitoring block to switch the signal after the
                     * train passed.
                     */
                    Thread.sleep(HP0_AFTER_TRAIN_PASS_DELAY_IN_MILLIS);
                    trackViewerService.switchSignal(signal, FUNCTION.HP0);
                } catch (InterruptedException e) {
                    LOG.error("error during delayed HP0 switch", e);
                }
            }).start();
        }
    }

    private void startTrain(Train train, Integer startDrivingLevel) throws InterruptedException {
        // delay the start of the train
        Thread.sleep(START_DELAY_MILLIS);
        // start train
        LOG.info("start train to drive {}", train);
        trainService.updateDrivingLevel(train.getId(),
            startDrivingLevel != null ? startDrivingLevel : DEFAULT_START_DRIVING_LEVEL);
    }

    private void routeFinished(RouteSequence nextRouteSequence) {
        if (nextRouteSequence == null || nextRouteSequence.getRoute().getRunState() != ROUTE_RUN_STATE.RESERVED) {
            stopTrain();
        }
        blockRunning = false;
    }

    private void stopTrain() {
        trainService.updateDrivingLevel(getTrain(), 0);
    }

    private void tearDownRouteExecution() {
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
        LOG.info("update the track for scenario {} with start route: {}", scenario.getName(), route.getName());
        Map<BusDataConfiguration, Boolean> trackPartStates = new HashMap<>();
        for (BusDataConfiguration routeBlockPart : route.getTrack().getTrackFunctions()) {
            trackPartStates.put(routeBlockPart, routeBlockPart.getBitState());
        }
        trackViewerService.toggleTrackParts(trackPartStates);
    }

}
