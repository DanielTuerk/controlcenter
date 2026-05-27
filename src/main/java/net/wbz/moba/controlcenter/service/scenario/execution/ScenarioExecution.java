package net.wbz.moba.controlcenter.service.scenario.execution;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import lombok.extern.slf4j.Slf4j;
import net.wbz.moba.controlcenter.service.track.TrackProvider;
import net.wbz.moba.controlcenter.service.track.block.TrackBlockRegistry;
import net.wbz.moba.controlcenter.service.train.TrainService;
import net.wbz.moba.controlcenter.shared.scenario.Route;
import net.wbz.moba.controlcenter.shared.scenario.Route.ROUTE_RUN_STATE;
import net.wbz.moba.controlcenter.shared.scenario.RouteSequence;
import net.wbz.moba.controlcenter.shared.scenario.Scenario;
import net.wbz.moba.controlcenter.shared.scenario.Scenario.RUN_STATE;
import net.wbz.moba.controlcenter.shared.track.model.Signal;
import net.wbz.moba.controlcenter.shared.train.TrainInstance;
import net.wbz.selectrix4java.device.DeviceManager;

import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * The execution of a {@link Scenario} started by the {@link ScenarioExecutor}. Update the run state of the {@link
 * Scenario} and start the {@link Route}s after each other. The execution can be stopped between routes.
 *
 * @author Daniel Tuerk
 */
@Slf4j
class ScenarioExecution {


    //    private final Scenario scenario;
    private final TrainService trainService;
    private final DeviceManager deviceManager;

    private final TrainInstance trainInstance;
    private final TrackProvider trackProvider;

    private final ExecuteRouteSequence executeRouteSequence;
    /**
     * Flag for stop called to scenario.
     */
    private volatile boolean stopped = false;
//    /**
//     * Flag for a execution of a {@link Route} in the {@link RouteSequence}.
//     */
//    private volatile boolean blockRunning = false;

    ScenarioExecution(Scenario scenario, TrainService trainService,
                      DeviceManager deviceManager,
                      TrackProvider trackProvider, TrackBlockRegistry trackBlockRegistry, ExecuteRouteSequence executeRouteSequence) {
        this.trainService = trainService;
        this.deviceManager = deviceManager;
        this.executeRouteSequence = executeRouteSequence;
        this.trainInstance = trainService.trainInstanceByTrain(scenario.getTrain());
        this.trackProvider = trackProvider;
    }


    Uni<RUN_STATE> start2(Scenario scenario2) {
        log.info("start scenario {}", scenario2.getName());
        return Uni.createFrom().item(scenario2)
            .onItem().invoke(scenarioToStart -> {
                scenarioToStart.setRunState(RUN_STATE.RUNNING);
                stopped = false;
            }).chain(scenario -> {
                final var routeSequences = scenario.getRouteSequences()
                    .stream()
                    .sorted(Comparator.comparingInt(RouteSequence::getPosition))
                    .toList();
                return Multi.createFrom().iterable(routeSequences)
                    .onItem().transformToUniAndConcatenate(routeSequence -> {
                        if (stopped) {
                            return Uni.createFrom().failure(new ScenarioStopped("stopped"));
                        }

                        log.info("start route {}", routeSequence.getRoute().getName());
                        final var i = routeSequences.indexOf(routeSequence);
                        final var previousRouteSequence = Optional.ofNullable((i > 0) ? routeSequences.get(i - 1) : null);
                        return executeRouteSequence.executeRouteSequence(new ExecuteRouteModel(
                            scenario.getId(),
                            routeSequence,
                            previousRouteSequence,
                            Optional.ofNullable((i + 1 < routeSequences.size()) ? routeSequences.get(i + 1) : null),
                            scenario.getTrain(), scenario.getTrainDrivingDirection(),
                            scenario.getStartDrivingLevel(),
                            trackProvider.findStartSignal(routeSequence.getRoute()),
                            false
                        )).onItem().invoke(routeRunState -> {
                            routeSequence.getRoute().setRunState(routeRunState);
                        });

                    })

                    .onCancellation().invoke(() -> {
                        // Cleanup / Compensation
                        stopped = true;
                        stopTrain();
                    })
                    // sammelt Ergebnisse
                    .collect().asList()

                    .onItem().transform(results -> {
                        boolean allRoutesSuccess = results.stream().allMatch(r -> r == ROUTE_RUN_STATE.FINISHED);
                        return allRoutesSuccess ? RUN_STATE.SUCCESS : RUN_STATE.ERROR;
                    });

            })

            .onFailure(ScenarioStopped.class)
            .recoverWithItem(RUN_STATE.STOPPED)

            .onFailure()
            .recoverWithItem(RUN_STATE.ERROR)
            .onTermination().invoke(this::tearDownRouteExecution)
            .invoke(runState -> {
                scenario2.setRunState(runState);
                log.info("finished scenario {}", scenario2.getName());
            });
    }

    static final class ScenarioStopped extends RuntimeException {
        ScenarioStopped(String msg) {
            super(msg);
        }
    }


//    private void routeFinished(RouteSequence nextRouteSequence) {
//        if (nextRouteSequence == null || nextRouteSequence.getRoute().getRunState() != ROUTE_RUN_STATE.RESERVED) {
//            stopTrain();
//            try {
//                // delay to end the route to let the train stop for a bit at the route end (or signal for next route)
//                Thread.sleep(FINISH_ROUTE_DELAY_MILLIS);
//            } catch (InterruptedException e) {
//                log.error("error during delayed HP0 switch", e);
//            }
//        }
//        blockRunning = false;
//    }

    private void stopTrain() {
        log.warn("stop train: {}", trainInstance.train().getName());
        trainService.updateDrivingLevel(trainInstance.train(), 0);
    }

    private void tearDownRouteExecution() {
        executeRouteSequence.tearDown();

    }


}
