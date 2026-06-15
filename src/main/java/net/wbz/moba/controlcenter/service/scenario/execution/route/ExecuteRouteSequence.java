package net.wbz.moba.controlcenter.service.scenario.execution.route;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.TimeoutException;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.wbz.moba.controlcenter.BusAddressIdentifier;
import net.wbz.moba.controlcenter.SelectrixHelper;
import net.wbz.moba.controlcenter.service.config.ConfigService;
import net.wbz.moba.controlcenter.service.scenario.execution.ExecuteRouteModel;
import net.wbz.moba.controlcenter.service.scenario.execution.NoConnectedDeviceException;
import net.wbz.moba.controlcenter.service.scenario.execution.ScenarioExecutionInterruptException;
import net.wbz.moba.controlcenter.service.scenario.execution.ScenarioStoppedException;
import net.wbz.moba.controlcenter.service.track.TrackViewerService;
import net.wbz.moba.controlcenter.service.track.block.FeedbackTrackBlockRegistry;
import net.wbz.moba.controlcenter.service.train.TrainService;
import net.wbz.moba.controlcenter.shared.scenario.Route;
import net.wbz.moba.controlcenter.shared.track.model.BusDataConfiguration;
import net.wbz.moba.controlcenter.shared.track.model.TrackBlock;
import net.wbz.moba.controlcenter.shared.train.Train;
import net.wbz.selectrix4java.device.Device;
import net.wbz.selectrix4java.device.DeviceAccessException;
import net.wbz.selectrix4java.device.DeviceManager;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
@ApplicationScoped
public class ExecuteRouteSequence {

    private final TrainService trainService;
    private final RouteExecutionObserver routeExecutionObserver;
    private final TrackViewerService trackViewerService;
    private final DeviceManager deviceManager;
    private final FeedbackTrackBlockRegistry feedbackTrackBlockRegistry;
    private final SwitchStartSignalOfRouteToDrive switchStartSignalOfRouteToDrive;
    private final RouteReservationCoordinator routeReservationCoordinator;
    private final RouteStateEventPublisher routeStateEventPublisher;
    private final ConfigService configService;

    @Inject
    public ExecuteRouteSequence(TrainService trainService,
                                RouteExecutionObserver routeExecutionObserver,
                                TrackViewerService trackViewerService,
                                DeviceManager deviceManager,
                                FeedbackTrackBlockRegistry feedbackTrackBlockRegistry,
                                SwitchStartSignalOfRouteToDrive switchStartSignalOfRouteToDrive,
                                RouteReservationCoordinator routeReservationCoordinator,
                                RouteStateEventPublisher routeStateEventPublisher, ConfigService configService) {
        this.trainService = trainService;
        this.routeExecutionObserver = routeExecutionObserver;
        this.trackViewerService = trackViewerService;
        this.deviceManager = deviceManager;
        this.feedbackTrackBlockRegistry = feedbackTrackBlockRegistry;
        this.switchStartSignalOfRouteToDrive = switchStartSignalOfRouteToDrive;
        this.routeReservationCoordinator = routeReservationCoordinator;
        this.routeStateEventPublisher = routeStateEventPublisher;
        this.configService = configService;
    }

    record Result(Route.ROUTE_RUN_STATE state, Optional<String> message) {
        static Result of() {
            return new Result(Route.ROUTE_RUN_STATE.FINISHED, Optional.empty());
        }

        static Result of(Route.ROUTE_RUN_STATE state, String message) {
            return new Result(state, Optional.of(message));
        }
    }

    public Uni<Route.ROUTE_RUN_STATE> executeRouteSequence(ExecuteRouteModel executeRouteModel) {
        return Uni.createFrom().item(executeRouteModel)
            .onItem().call(this::prepare)

            .onItem().call(this::waitForFreeTrack)

            // check all track blocks are free for the route, also by manual drive or lost wagons
            .onItem().call(model -> checkForFreeBlocksOnRoute(model.routeSequence().getRoute()))

            .onItem().call(model -> {
                routeReservationCoordinator.reserve(model.scenarioId(), model.routeSequence());
                return Uni.createFrom().item(model);
            })

            .onItem().call(model -> startRoute(model, routeReservationCoordinator))

            .onItem().call(routeExecution -> {
                routeExecution.nextRouteSequence().ifPresent(next ->
                    routeExecutionObserver.registerBlocksToReserveNextRoute(routeExecution.scenarioId(), routeExecution.routeSequence(), next));
                return Uni.createFrom().item(routeExecution);
            })

            .replaceWith(Result::of)

            .onFailure(NoConnectedDeviceException.class)
            .recoverWithItem(() -> Result.of(Route.ROUTE_RUN_STATE.FAILED, "no connected device"))

            .onFailure(NoTrackForRouteException.class)
            .recoverWithItem(() -> Result.of(Route.ROUTE_RUN_STATE.FAILED, "no track for route"))

            .onFailure(NoTrainInStartBlockException.class)
            .recoverWithItem(() -> Result.of(Route.ROUTE_RUN_STATE.SKIPPED, "no train in start block"))

            .onFailure()
            .recoverWithItem(e -> {
                log.error("route '{}' failed", executeRouteModel.routeSequence().getRoute().getName(), e);
                stopTrain(executeRouteModel.train()).subscribe().with(
                    unused -> log.debug("train stopped after route failure: {}", executeRouteModel.train()),
                    failure -> log.error("failed to stop train after route failure: {}", executeRouteModel.train(), failure)
                );
                return Result.of(Route.ROUTE_RUN_STATE.FAILED, "route failed");
            })

            .onItem().invoke(result -> {
                log.info("route finished with state: {}", result);
                routeStateEventPublisher.fireEvent(executeRouteModel.scenarioId(), executeRouteModel.routeSequence(), result.state(), result.message().orElse(null));
            })
            .map(Result::state)
            .onCancellation().call(() -> {
                log.info("route execution cancelled for route: {} ({})", executeRouteModel.routeSequence().getRoute().getName(),
                    executeRouteModel.routeSequence().getRoute().getId());
                routeStateEventPublisher.fireEvent(executeRouteModel.scenarioId(), executeRouteModel.routeSequence(), Route.ROUTE_RUN_STATE.CANCELED);

                var train = executeRouteModel.train();
                return stopTrain(train);
            })
            .eventually(() -> {
                final var routeSequence = executeRouteModel.routeSequence();
                try {
                    routeExecutionObserver.cleanup(routeSequence);
                } catch (DeviceAccessException e) {
                    log.error("device access failed to cleanup block listeners for route sequence: {} (route: {})",
                        routeSequence.getId(), routeSequence.getRoute().getName(), e);
                }
                return Uni.createFrom().voidItem();
            });

    }

    private Uni<Void> stopTrain(Train train) {
        log.info("stop train: {}", train.getName());
        return Uni.createFrom()
            .item(() -> {
                trainService.updateDrivingLevel(train.getId(), 0);
                return (Void) null;
            })
            .runSubscriptionOn(Infrastructure.getDefaultWorkerPool())
            .onItem()
            .delayIt().by(Duration.ofSeconds(configService.getFinishRouteDelaySeconds()))
            // wait a bit before end the action to be sure that the train is physically stopped
            .call(() -> {
                log.info("train stop called: {}", train.getName());
                return Uni.createFrom().voidItem();
            });
    }

    /**
     * Check that all given {@link TrackBlock}s are free. If not, an exception is thrown.
     * In the meanwhile of the execution, where could be an external interaction,
     * which has occupied the track blocks for the route. (e.g., by manual drive)
     *
     * @param route {@link Route} for which to check block availability
     */
    private Uni<Void> checkForFreeBlocksOnRoute(Route route) {
        return Uni.createFrom().deferred(() -> {
            try {
                Device device = connectedDevice();
                for (TrackBlock trackBlock : route.getAllTrackBlocksToDrive()) {
                    var blockFunction = trackBlock.getBlockFunction();
                    var entry = new BusAddressIdentifier(blockFunction);
                    if (SelectrixHelper.getFeedbackBlockModule(device, entry)
                        .getLastReceivedBlockState(blockFunction.getBit())) {
                        return Uni.createFrom().failure(new RouteExecutionInterruptException(
                            String.format("track not free at block: %d (%d) ", entry.getAddress(),
                                blockFunction.getBit())));
                    }
                }
                return Uni.createFrom().voidItem();
            } catch (DeviceAccessException e) {
                return Uni.createFrom().failure(new NoConnectedDeviceException(e));
            }
        });
    }

    private Uni<Route.ROUTE_RUN_STATE> prepare(final ExecuteRouteModel executeRouteModel) {
        return Uni.createFrom().deferred(() -> {
                final var route = executeRouteModel.routeSequence().getRoute();
                final var train = executeRouteModel.train();

                if (!route.hasTrack()) {
                    return Uni.createFrom().failure(new NoTrackForRouteException(route));
                }

                boolean shouldCheckTrainInStartBlock = executeRouteModel.previousRouteSequence().isEmpty()
                    || executeRouteModel.wasSkipped();

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

                routeStateEventPublisher.fireEvent(executeRouteModel.scenarioId(), executeRouteModel.routeSequence(), Route.ROUTE_RUN_STATE.PREPARED);

            return prepareTrainToStart(train, executeRouteModel.scenarioDrivingDirection()).replaceWith(() -> Route.ROUTE_RUN_STATE.PREPARED);
//            return Uni.createFrom().item(Route.ROUTE_RUN_STATE.PREPARED);
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
                    if (SelectrixHelper.getBlockModule(connectedDevice(), trackBlock)
                        .getLastReceivedBlockState(trackBlock.getBlockFunction().getBit())) {
                        return false;
                    }
                } catch (DeviceAccessException e) {
                    throw new ScenarioExecutionInterruptException("no connected device", e);
                }
            }
        }
        return !feedbackTrackBlockRegistry.isCurrentlyInBlock(train.getId(), trackBlocks.toArray(new TrackBlock[0]));
    }

    private Uni<Void> prepareTrainToStart(Train train, Train.DRIVING_DIRECTION direction) {
        return Uni.createFrom().item(() -> {
            trainService.toggleDrivingDirection(train.getId(),
                direction == Train.DRIVING_DIRECTION.FORWARD);
            trainService.toggleLight(train.getId(), true);
            return (Void) null;
        }).runSubscriptionOn(Infrastructure.getDefaultWorkerPool());
    }

    private Uni<Void> waitForFreeTrack(ExecuteRouteModel executeRouteModel) throws ScenarioExecutionInterruptException {
        return Uni.createFrom().item(() -> {
                final var routeSequence = executeRouteModel.routeSequence();

                routeExecutionObserver.addRunningRouteSequence(routeSequence);

                final Route route = routeSequence.getRoute();
                log.info("train request free track to start: {} ({})", route.getName(), route.getId());
                return (Void) null;
            })
            .chain(() -> Multi.createFrom().ticks().every(Duration.ofMillis(500))
                // observer calls for each item are blocking
                .emitOn(Infrastructure.getDefaultWorkerPool())
                .onItem().transform(tick ->
                    routeExecutionObserver.checkAndReserveNextRunningRoute(executeRouteModel.scenarioId(),
                        executeRouteModel.routeSequence(),
                        executeRouteModel.previousRouteSequence()
                    )
                )
                .select().where(Boolean.TRUE::equals)
                .select().first()
                .toUni()
                .replaceWithVoid()
                .ifNoItem()
                .after(Duration.ofMinutes(configService.getWaitForFreeTackTimeoutInMinutes()))
                .fail()
            )

            .onFailure(ScenarioStoppedException.class)
            .transform(e -> new ScenarioExecutionInterruptException("aborted (stopped)", e))
            .onFailure(TimeoutException.class)
            .transform(e -> new ScenarioExecutionInterruptException("timed out", e))
            .onFailure()
            .transform(e -> new ScenarioExecutionInterruptException("failed", e));
    }

    private Uni<ExecuteRouteModel> startRoute(final ExecuteRouteModel routeExecution, RouteReservationCoordinator routeReservationCoordinator) {
        log.info("start route: {} ({})", routeExecution.routeSequence().getRoute().getName(),
            routeExecution.routeSequence().getRoute().getId());

        return Uni.createFrom().item(routeExecution)
            .onItem().invoke((executeRouteModel) ->
                routeStateEventPublisher.fireEvent(routeExecution.scenarioId(), executeRouteModel.routeSequence(), Route.ROUTE_RUN_STATE.RUNNING))
            .onItem().call(this::updateTrack)
            .onItem().call(model -> {
                model.nextRouteSequence().ifPresent(next ->
                    routeExecutionObserver.registerBlocksToReserveNextRoute(model.scenarioId(), model.routeSequence(), next));
                return Uni.createFrom().item(model);
            })
            .onItem().call(x -> {
                final var route = routeExecution.routeSequence().getRoute();
                final var endBlock = route.getEnd();

                log.info("wait for end of route '{}' ({}) by end block: {} ({})",
                    route.getName(), route.getId(), endBlock.getName(), endBlock.getBlockFunction());

                return Uni.createFrom().emitter(emitter -> {
                    try {
                        final var blockModule = SelectrixHelper.getBlockModule(connectedDevice(),
                            new BusAddressIdentifier(endBlock.getBlockFunction())
                        );

                        final RouteEndBlockListener listener = new RouteEndBlockListener(route) {
                            @Override
                            protected void trainEnterRouteEnd() {
                                log.debug("train entered route end ({}): {}", endBlock.getBlockFunction(), routeExecution.train());

                                var shouldStopTrain = routeExecution.nextRouteSequence()
                                    .map(next -> !routeReservationCoordinator.isAlreadyReserved(next))
                                    .orElse(true);

                                if (shouldStopTrain) {
                                    stopTrain(routeExecution.train())
                                        .subscribe().with(
                                            unused -> {
                                                emitter.complete(null);
                                                log.debug("train stopped after route end: {}", routeExecution.train());
                                            },
                                            failure -> {
                                                log.error("failed to stop train after route end: {}", routeExecution.train(), failure);
                                                emitter.fail(failure); // falls sinnvoll in deinem Kontext
                                            }
                                        );
                                } else {
                                    emitter.complete(null);
                                }
                            }
                        };

                        blockModule.addBlockListener(listener);

                        startTrain(x).subscribe().with(unused -> {
                        }, emitter::fail);

                        emitter.onTermination(() -> {
                            log.debug("route end block listener removed for route: {} ({})", route.getName(), route.getId());
                            blockModule.removeBlockListener(listener);
                        });

                    } catch (DeviceAccessException e) {
                        log.error("device access failed", e);
                        emitter.fail(e);
                    }
                });

            });
    }

    private Uni<Void> updateTrack(ExecuteRouteModel model) {
        return Uni.createFrom()
            .item(() -> {
                final var route = model.routeSequence().getRoute();
                log.info("update the track for scenario {} with start route: {} ({})",
                    model.scenarioId(),
                    route.getName(),
                    route.getId());

                Map<BusDataConfiguration, Boolean> trackPartStates = new HashMap<>();
                for (BusDataConfiguration routeBlockPart : route.getTrack().trackFunctions()) {
                    trackPartStates.put(routeBlockPart, routeBlockPart.getBitState());
                }
                trackViewerService.toggleTrackParts(trackPartStates);
                return (Void) null;
            }).onItem().call(() -> switchStartSignalOfRouteToDrive.call(model.routeSequence().getRoute()));
    }

    private Uni<Void> startTrain(ExecuteRouteModel routeExecution) {
        return Uni.createFrom().item(routeExecution.train())
            .onItem().transform(trainService::trainInstanceByTrain)
            .onItem().call(trainInstance -> {
                if (trainInstance.trainStatus().getDrivingLevel() <= 0) {
                    return startTrain(trainInstance.train(), routeExecution.trainStartDrivingLevel())
                        .invoke(() -> log.debug("train ({}) start scheduled", routeExecution.train().getName()))
                        .onFailure().invoke(f -> log.error("failed to start train with delay", f));
                } else {
                    return Uni.createFrom().voidItem();
                }
            })
            .replaceWithVoid();
    }

    private Device connectedDevice() throws DeviceAccessException {
        return deviceManager.getConnectedDevice()
            .orElseThrow(() -> new DeviceAccessException("no connected device"));
    }

    private Uni<Void> startTrain(Train train, Integer startDrivingLevel) {
        return Uni.createFrom().voidItem().onItem()
            // delay the start of the train
            .delayIt().by(Duration.ofSeconds(configService.getStartTrainDelaySeconds()))
            .emitOn(Infrastructure.getDefaultWorkerPool())
            .chain(() -> Uni.createFrom().item(() -> {
                log.info("start train to drive {}", train);
                trainService.updateDrivingLevel(train.getId(),
                    startDrivingLevel != null ? startDrivingLevel : configService.getDefaultStartDrivingLevel());
                return null;
            }));
    }

}
