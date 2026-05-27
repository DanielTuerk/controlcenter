package net.wbz.moba.controlcenter.service.scenario.execution;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.TimeoutException;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.wbz.moba.controlcenter.BusAddressIdentifier;
import net.wbz.moba.controlcenter.EventBroadcaster;
import net.wbz.moba.controlcenter.SelectrixHelper;
import net.wbz.moba.controlcenter.service.track.TrackProvider;
import net.wbz.moba.controlcenter.service.track.TrackViewerService;
import net.wbz.moba.controlcenter.service.track.block.TrackBlockRegistry;
import net.wbz.moba.controlcenter.service.train.TrainService;
import net.wbz.moba.controlcenter.shared.scenario.Route;
import net.wbz.moba.controlcenter.shared.scenario.RouteSequence;
import net.wbz.moba.controlcenter.shared.scenario.RouteStateEvent;
import net.wbz.moba.controlcenter.shared.scenario.Scenario;
import net.wbz.moba.controlcenter.shared.track.model.BlockStraight;
import net.wbz.moba.controlcenter.shared.track.model.BusDataConfiguration;
import net.wbz.moba.controlcenter.shared.track.model.Signal;
import net.wbz.moba.controlcenter.shared.track.model.TrackBlock;
import net.wbz.moba.controlcenter.shared.train.Train;
import net.wbz.selectrix4java.block.BlockListener;
import net.wbz.selectrix4java.block.FeedbackBlockModule;
import net.wbz.selectrix4java.device.Device;
import net.wbz.selectrix4java.device.DeviceAccessException;
import net.wbz.selectrix4java.device.DeviceManager;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

@Slf4j
@ApplicationScoped
public class ExecuteRouteSequence {
    /**
     * Delay to start the {@link Train} for a started {@link Route}.
     * TODO config value
     */
    private static final int START_TRAIN_DELAY_MILLIS = 3000;

    /**
     * Delay to wait to finish the {@link Route} for a stopped {@link Train} at {@link Route} end.
     */
    private static final int FINISH_ROUTE_DELAY_MILLIS = 3000;
    private static final int DEFAULT_START_DRIVING_LEVEL = 10;
    private static final long MILLIS_TO_WAIT_IN_BLOCK_RUN = 200L;
    private static final long HP0_AFTER_TRAIN_PASS_DELAY_IN_MILLIS = 15000L;

    private final Map<FeedbackBlockModule, List<BlockListener>> blockListeners = new ConcurrentHashMap<>();

    private final TrackProvider trackProvider;
    private final TrainService trainService;
    private final RouteExecutionObserver routeExecutionObserver;
    private final TrackViewerService trackViewerService;
    private final Event<RouteStateEvent> routeStateHandler;
    private final EventBroadcaster eventBroadcaster;
    private final DeviceManager deviceManager;
    private final TrackBlockRegistry trackBlockRegistry;

    @Inject
    public ExecuteRouteSequence(TrackProvider trackProvider, TrainService trainService, RouteExecutionObserver routeExecutionObserver, TrackViewerService trackViewerService, Event<RouteStateEvent> routeStateHandler, EventBroadcaster eventBroadcaster, DeviceManager deviceManager, TrackBlockRegistry trackBlockRegistry) {
        this.trackProvider = trackProvider;
        this.trainService = trainService;
        this.routeExecutionObserver = routeExecutionObserver;
        this.trackViewerService = trackViewerService;
        this.routeStateHandler = routeStateHandler;
        this.eventBroadcaster = eventBroadcaster;
        this.deviceManager = deviceManager;
        this.trackBlockRegistry = trackBlockRegistry;
    }

    Uni<Route.ROUTE_RUN_STATE> executeRouteSequence(ExecuteRouteModel executeRouteModel) {

//        executeRouteModel.previousRouteSequence().ifPresent(routeSequence -> routeSequence.getRoute().getRunState())

        return Uni.createFrom().item(executeRouteModel)

            .onItem().call(this::prepare)

            .onItem().call(this::waitForFreeTrack)

            // check all track blocks are free for the route, also by manual drive or lost wagons
            .onItem().call(model -> checkForFreeBlocks(model.routeSequence().getRoute().getAllTrackBlocksToDrive()))


// TODO start und waituntifinish sollte ein call sein
            .onItem().call(this::startRoute)

            .onItem().call(model -> {
                routeExecutionObserver.removeRunningRouteSequence(model.routeSequence());
                return Uni.createFrom().voidItem();
            })

//            .onItem().transformToUni(routeExecution -> waitUntilFinished(routeExecution))
// TODO sollte auch in startRoute sein, deterministisch
//            .onItem().invoke(this::finishRoute)

            .replaceWith(Route.ROUTE_RUN_STATE.FINISHED)

            .onFailure(NoTrainInStartBlockException.class)
            .recoverWithItem(Route.ROUTE_RUN_STATE.SKIPPED)

            .onFailure()
            .recoverWithItem(e -> {
                log.error("route failed", e);
                return Route.ROUTE_RUN_STATE.FAILED;
            })
            .onItem().invoke(state -> {
                log.info("route finished with state: {}", state);
                // TODO FINISH_ROUTE_DELAY_MILLIS warten, damit die Rückmeldung von der Rückmeldung der Blöcke nicht zu spät kommt und die nächste Route nicht direkt reserviert wird?
                // TODO eigentlich ist finished sobald der train den ziel block belegt hat
                fireEvent(executeRouteModel.scenarioId(), executeRouteModel.routeSequence(), state);
            });

    }

    /**
     * Check that all given {@link TrackBlock}s are free. If not an exception is thrown.
     *
     * @param trackBlocks {@link TrackBlock}s to check
     */
    private Uni<Void> checkForFreeBlocks(Set<TrackBlock> trackBlocks) {
        try {
            Device device = deviceManager.getConnectedDevice()
                .orElseThrow(() -> new DeviceAccessException("no connected device"));
            for (TrackBlock trackBlock : trackBlocks) {
                BusDataConfiguration blockFunction = trackBlock.getBlockFunction();
                BusAddressIdentifier entry = new BusAddressIdentifier(blockFunction);
                if (SelectrixHelper.getFeedbackBlockModule(device, entry)
                    .getLastReceivedBlockState(blockFunction.getBit())) {
                    return Uni.createFrom().failure(new RouteExecutionInterruptException(
                        String.format("track not free at block: %d (%d) ", entry.getAddress(),
                            blockFunction.getBit())));
                }
            }
        } catch (DeviceAccessException e) {
            final var msg = "no connected device";
            return Uni.createFrom().failure(new ScenarioExecutionInterruptException(msg, e));
        }
        return Uni.createFrom().voidItem();
    }

    private Uni<ExecuteRouteModel> prepare(final ExecuteRouteModel executeRouteModel) {

        return Uni.createFrom().item(executeRouteModel)

            .onItem().call(model -> {

                final var route = model.routeSequence().getRoute();

                final var train = model.train();

                boolean shouldCheckTrainInStartBlock = model.previousRouteSequence().isEmpty() || model.previousRouteSequence()
                    .map(prev -> prev.getRoute().getRunState() == Route.ROUTE_RUN_STATE.SKIPPED)
                    .orElse(false);

                // detect train
                if (shouldCheckTrainInStartBlock && trainIsNotInStartBlock(route, train)) {
                    /*
                     * Check for the available train in the first route block to be executed.
                     * Others are saved by the execution.
                     * An explicit check of each route block can't be performed because the feedback could be too late.
                     */
                    log.error("no train in start block: {}, train: {}", route.getStart(), train);
                    return Uni.createFrom().failure(new NoTrainInStartBlockException(train, route.getStart()));
                }

                // find start position on signal

                prepareTrainToStart(train, model.scenarioDrivingDirection());
                // TODO state change here or by result? or fire event?
                if (route.getRunState() != Route.ROUTE_RUN_STATE.RESERVED) {
                    route.setRunState(Route.ROUTE_RUN_STATE.PREPARED);
                }
//                Optional<Signal> signal = findStartSignal(route);
////        return new RouteExecution(routeSequence, previousRouteSequence, nextRouteSequence, signal.orElse(null));
                return Uni.createFrom().item(model);
            });

    }

    private boolean trainIsNotInStartBlock(Route route, Train train) throws ScenarioExecutionInterruptException {
        List<TrackBlock> trackBlocks = Stream.of(route.getStart().getLeftTrackBlock(),
                route.getStart().getMiddleTrackBlock(),
                route.getStart().getRightTrackBlock())
            .filter(Objects::nonNull)
            .toList();
        for (TrackBlock trackBlock : trackBlocks) {
            if (!trackBlock.getFeedback()) {
                try {
                    if (SelectrixHelper.getBlockModule(deviceManager.getConnectedDevice()
                            .orElseThrow(() -> new DeviceAccessException("no connected device")), trackBlock)
                        .getLastReceivedBlockState(trackBlock.getBlockFunction().getBit())) {
                        return false;
                    }
                } catch (DeviceAccessException e) {
                    throw new ScenarioExecutionInterruptException("no connected device", e);
                }
            }
        }

        return !trackBlockRegistry.isCurrentlyInBlock(train.getId(), trackBlocks.toArray(new TrackBlock[0]));
    }

    private void prepareTrainToStart(Train train, Train.DRIVING_DIRECTION direction) {
        trainService.toggleDrivingDirection(train.getId(),
            direction == Train.DRIVING_DIRECTION.FORWARD);
        trainService.toggleLight(train.getId(), true);
    }

    private Uni<Void> waitForFreeTrack(ExecuteRouteModel routeExecution2) throws ScenarioExecutionInterruptException {

        return Uni.createFrom().voidItem().call(() -> {

                fireEvent(routeExecution2.scenarioId(), routeExecution2.routeSequence(), Route.ROUTE_RUN_STATE.RESERVED);

                routeExecutionObserver.addRunningRouteSequence(routeExecution2.routeSequence());

                final Route route = routeExecution2.routeSequence().getRoute();
                log.info("train request free track to start: {}", route.getName());
                return Uni.createFrom().voidItem();
            })
            .chain(() -> {
//            try {
                return Multi.createFrom().ticks().every(Duration.ofMillis(500))
                    .onItem().transform(tick ->
                        routeExecutionObserver.checkAndReserveNextRunningRoute(
                            routeExecution2.routeSequence(),
                            routeExecution2.previousRouteSequence()
                        )
                    )
                    .select().where(Boolean.TRUE::equals)
                    .select().first()
                    .toUni()
                    .replaceWithVoid()
                    .ifNoItem().after(Duration.ofMinutes(5)).fail();
            })

            // TODO gefällt mir nicht, ich weiß ggf nicht welche runtime exceptions kommen, was ist der bessere weg?
            .onFailure(ScenarioExecution.ScenarioStopped.class)
            .transform(e -> new ScenarioExecutionInterruptException("aborted (stopped)", e))
            .onFailure(TimeoutException.class)
            .transform(e -> new ScenarioExecutionInterruptException("timed out", e))
            .onFailure()
            .transform(e -> (e instanceof ScenarioExecutionInterruptException)
                ? e
                : new ScenarioExecutionInterruptException("failed", e)
            );

    }

    private Uni<Void> startRoute(final ExecuteRouteModel routeExecution) {

        return Uni.createFrom().item(routeExecution)
            .onItem().call(item -> {
                try {
                    initRouteEndListener(routeExecution.routeSequence());
                    return Uni.createFrom().voidItem();
                } catch (DeviceAccessException e) {
                    log.error("init route end listener", e);
                    return Uni.createFrom().failure(new ScenarioExecutionInterruptException("no device connection", e));
                }
            })
            .onItem().call(this::fooUpdateTrack)
            .onItem().call(this::fooStartTrain)
            .onItem().call(this::reserveNextRouteForCurrentRoute)
            .onItem().call(model -> {
                fireEvent(routeExecution.scenarioId(), model.routeSequence(), Route.ROUTE_RUN_STATE.RUNNING);

                // register block listener for each block on the track to try to reserve the next route immediately
                for (TrackBlock trackBlock : routeExecution.routeSequence().getRoute().getTrack().trackBlocks()) {
                    try {
                        addBlockListener(new BlockListener() {
                            @Override
                            public void blockOccupied(int blockNr) {
                                reserveNextRouteForCurrentRoute(routeExecution).subscribe().with(
                                    unused -> log.debug("try to reserve next route on block occupied: {}", blockNr),
                                    failure -> log.error("failed to reserve next route on block occupied: {}", blockNr, failure)
                                );
                            }

                            @Override
                            public void blockFreed(int blockNr) {
                            }
                        }, trackBlock.getBlockFunction());
                    } catch (DeviceAccessException e) {
                        final var msg = "reserve next route: " + model.routeSequence().getRoute().getName();
                        log.error(msg, e);
                        return Uni.createFrom().failure(new ScenarioExecutionInterruptException(msg, e));
                    }
                }
                return Uni.createFrom().voidItem();
            })
            .replaceWithVoid();
    }

    private Uni<Void> fooUpdateTrack(ExecuteRouteModel model) {
        log.info("update the track for scenario {} with start route: {}",
            model.scenarioId(), model.routeSequence().getRoute().getName());

        updateTrack(model.routeSequence().getRoute());

        if (model.startSignal().isPresent()) {
            final var blockStraight$ = model.nextRouteSequence()
                .map(RouteSequence::getRoute)
                .map(Route::getStart);
            if (blockStraight$.isPresent()) {
                return switchSignalToDrive(model.startSignal().get(), blockStraight$.get());
            }
        }
        return Uni.createFrom().voidItem();
    }

    private Uni<Void> fooStartTrain(ExecuteRouteModel routeExecution) {
        final var trainInstance = trainService.trainInstanceByTrain(routeExecution.train());
        if (trainInstance.trainStatus().getDrivingLevel() <= 0) {
            return startTrain(trainInstance.train(), routeExecution.trainStartDrivingLevel())
                .invoke(() -> log.debug("train ({}) start scheduled", routeExecution.train().getName()))
                .onFailure().invoke(f -> log.error("failed to start train with delay", f));
        } else {
            return Uni.createFrom().voidItem();
        }
    }

    /**
     * Update the track of the {@link Route} for the given {@link Scenario}.
     *
     * @param route current {@link Route}
     */
    private void updateTrack(Route route) {
        Map<BusDataConfiguration, Boolean> trackPartStates = new HashMap<>();
        for (BusDataConfiguration routeBlockPart : route.getTrack().trackFunctions()) {
            trackPartStates.put(routeBlockPart, routeBlockPart.getBitState());
        }
        trackViewerService.toggleTrackParts(trackPartStates);
    }

    /**
     * Add the {@link RouteEndBlockListener} to indicate the end of the route and throw the state for the finished
     * execution.
     *
     * @param routeSequence {@link RouteSequence} to detect the end of execution
     * @throws DeviceAccessException no access
     */
    private void initRouteEndListener(final RouteSequence routeSequence) throws DeviceAccessException {
        Route route = routeSequence.getRoute();
        BlockListener blockListener = new RouteEndBlockListener(route) {
            @Override
            protected void trainEnterRouteEnd() {
                log.info("route finished {}", route);
                // TODO kann nicht sein
//                routeFinished(routeExecution.getNextRouteSequence());
            }
        };
        addBlockListener(blockListener, route.getEnd().getBlockFunction());
    }

    private void addBlockListener(BlockListener listener, BusDataConfiguration busDataConfiguration) throws
        DeviceAccessException {
        final var device = deviceManager.getConnectedDevice()
            .orElseThrow(() -> new DeviceAccessException("no connected device"));
        FeedbackBlockModule feedbackBlockModule = SelectrixHelper
            .getFeedbackBlockModule(device, new BusAddressIdentifier(busDataConfiguration));

        if (!blockListeners.containsKey(feedbackBlockModule)) {
            blockListeners.put(feedbackBlockModule, new ArrayList<>());
        }

        blockListeners.get(feedbackBlockModule).add(listener);

        feedbackBlockModule.addBlockListener(listener);
    }

    /**
     * Try to reserve next route. Switch start signal of next route also to HP1 to drive through from the end of the
     * current route.
     *
     * @param routeExecution {@link RouteExecution}
     */
    private Uni<Void> reserveNextRouteForCurrentRoute(ExecuteRouteModel routeExecution) {
        if (routeExecution.nextRouteSequence().isEmpty()) {
            return Uni.createFrom().voidItem();
        }
        var nextRouteSequence = routeExecution.nextRouteSequence().get();

        if (routeExecutionObserver.checkAndReserveNextRunningRoute(nextRouteSequence,
            Optional.of(routeExecution.routeSequence()))) {

            synchronized (routeExecutionObserver) {
                routeExecutionObserver.addRunningRouteSequence(nextRouteSequence);
            }

            final var startSignal$ = trackProvider.findStartSignal(nextRouteSequence.getRoute());
            if (startSignal$.isPresent()) {
                return switchSignalToDrive(startSignal$.get(),
                    nextRouteSequence.getRoute().getStart());
            }
        }
        return Uni.createFrom().voidItem();
    }

    private Uni<Void> switchSignalToDrive(final Signal signal, BlockStraight startOfNextRoute) {
        var signalFunction = nextSignalFunction(startOfNextRoute);
        log.info("switch signal to {} {}", signalFunction, signal);
        trackViewerService.switchSignal(signal, signalFunction);


        return Uni.createFrom().voidItem().onItem()
            // signal have no monitoring block and will never get the HP0 after drive
            .delayIt().by(Duration.ofMillis(HP0_AFTER_TRAIN_PASS_DELAY_IN_MILLIS))
            .invoke(() -> {
                /*
                 * Set to HP0 after delay to simulate a occupied monitoring block to switch the signal after the
                 * train passed.
                 */
                trackViewerService.switchSignal(signal, Signal.FUNCTION.HP0);
            });
    }

    private Uni<Void> startTrain(Train train, Integer startDrivingLevel) {
        return Uni.createFrom().voidItem().onItem()
            // delay the start of the train
            .delayIt().by(Duration.ofMillis(START_TRAIN_DELAY_MILLIS))
            .invoke(() -> {
                // start train
                log.info("start train to drive {}", train);
                trainService.updateDrivingLevel(train.getId(),
                    startDrivingLevel != null ? startDrivingLevel : DEFAULT_START_DRIVING_LEVEL);
            });
    }

    private Signal.FUNCTION nextSignalFunction(BlockStraight startOfNextRoute) {
        if (startOfNextRoute != null && deviceManager.getConnectedDevice().isPresent()) {
            var device = deviceManager.getConnectedDevice().get();
            return startOfNextRoute.getAllTrackBlocks().stream().anyMatch(trackBlock -> {
                try {
                    return device.getBusAddress(trackBlock.getBlockFunction().getBus(),
                            trackBlock.getBlockFunction().getAddress())
                        .getBitState(trackBlock.getBlockFunction().getBit());
                } catch (DeviceAccessException e) {
                    log.error("cannot get device address for block of start", e);
                    return false;
                }
            }) ? Signal.FUNCTION.HP2 : Signal.FUNCTION.HP1;
        }
        return Signal.FUNCTION.HP1;
    }

    private void fireEvent(long scenarioId, RouteSequence routeSequence, Route.ROUTE_RUN_STATE state) {
        fireEvent(scenarioId, routeSequence, state, null);
    }

    private void fireEvent(long scenarioId, RouteSequence routeSequence, Route.ROUTE_RUN_STATE state, String message) {
        final var routeStateEvent = new RouteStateEvent(scenarioId, routeSequence.getId(), state, message);
        routeStateHandler.fire(routeStateEvent);
        eventBroadcaster.fireEvent(routeStateEvent);
    }

    public void tearDown() {
        for (FeedbackBlockModule feedbackBlockModule : blockListeners.keySet()) {
            for (BlockListener feedbackBlockListener : blockListeners.get(feedbackBlockModule)) {
                feedbackBlockModule.removeBlockListener(feedbackBlockListener);
            }
        }
    }
}
