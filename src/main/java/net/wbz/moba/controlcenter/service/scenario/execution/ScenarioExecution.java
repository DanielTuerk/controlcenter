package net.wbz.moba.controlcenter.service.scenario.execution;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import lombok.extern.slf4j.Slf4j;
import net.wbz.moba.controlcenter.service.scenario.execution.route.ExecuteRouteSequence;
import net.wbz.moba.controlcenter.shared.scenario.Route;
import net.wbz.moba.controlcenter.shared.scenario.Route.ROUTE_RUN_STATE;
import net.wbz.moba.controlcenter.shared.scenario.RouteSequence;
import net.wbz.moba.controlcenter.shared.scenario.Scenario;
import net.wbz.moba.controlcenter.shared.scenario.Scenario.RUN_STATE;

import java.util.Comparator;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * The execution of a {@link Scenario} started by the {@link ScenarioExecutor}. Update the run state of the {@link
 * Scenario} and start the {@link Route}s after each other. The execution can be stopped between routes.
 *
 * @author Daniel Tuerk
 */
@Slf4j
class ScenarioExecution {

    private final ExecuteRouteSequence executeRouteSequence;

    ScenarioExecution(ExecuteRouteSequence executeRouteSequence) {
        this.executeRouteSequence = executeRouteSequence;
    }

    Uni<RUN_STATE> start(Scenario scenario) {
        final var lastRouteRunState = new AtomicReference<ROUTE_RUN_STATE>();
        log.info("start scenario {}", scenario.getName());
        return Uni.createFrom().item(scenario)
            .chain(scenarioToStart -> {
                final var routeSequences = scenarioToStart.getRouteSequences()
                    .stream()
                    .sorted(Comparator.comparingInt(RouteSequence::getPosition))
                    .toList();
                return Multi.createFrom().iterable(routeSequences)
                    .onItem().transformToUniAndConcatenate(routeSequence -> {
                        final var route = routeSequence.getRoute();
                        log.info("start to execute route: {} ({})", route.getName(), route.getId());
                        final var index = routeSequences.indexOf(routeSequence);
                        final var previousRouteSequence = Optional.ofNullable((index > 0) ? routeSequences.get(index - 1) : null);
                        final var nextRouteSequence = Optional.ofNullable((index + 1 < routeSequences.size()) ? routeSequences.get(index + 1) : null);
                        return executeRouteSequence.executeRouteSequence(new ExecuteRouteModel(
                                scenarioToStart.getId(),
                                routeSequence,
                                previousRouteSequence,
                                nextRouteSequence,
                                scenarioToStart.getTrain(),
                                scenarioToStart.getTrainDrivingDirection(),
                                scenarioToStart.getStartDrivingLevel(),
                                lastRouteRunState.get() == ROUTE_RUN_STATE.SKIPPED))
                            .onItem().call(runState -> {
                                lastRouteRunState.set(runState);
                                return Uni.createFrom().item(runState);
                            });
                    })
                    .onCancellation().invoke(() -> log.info("scenario ({}): execution cancelled", scenarioToStart.getId()))
                    // collect all route results
                    .collect().asList()
                    .onItem().transform(results -> {
                        boolean allRoutesSuccess = results.stream().allMatch(r -> r == ROUTE_RUN_STATE.FINISHED);
                        return allRoutesSuccess ? RUN_STATE.SUCCESS : RUN_STATE.FAILED;
                    });
            })

            .onFailure(ScenarioStoppedException.class)
            .recoverWithItem(RUN_STATE.STOPPED)

            .onFailure()
            .recoverWithItem(RUN_STATE.FAILED)

            .invoke(runState -> log.info("finished scenario: {} ({})", scenario.getName(), scenario.getId()));
    }

}
