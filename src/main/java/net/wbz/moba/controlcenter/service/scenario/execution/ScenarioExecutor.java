package net.wbz.moba.controlcenter.service.scenario.execution;

import io.quarkus.virtual.threads.VirtualThreads;
import io.smallrye.mutiny.subscription.Cancellable;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import lombok.extern.slf4j.Slf4j;
import net.wbz.moba.controlcenter.EventBroadcaster;
import net.wbz.moba.controlcenter.service.scenario.execution.route.ExecuteRouteSequence;
import net.wbz.moba.controlcenter.shared.scenario.Scenario;
import net.wbz.moba.controlcenter.shared.scenario.Scenario.RUN_STATE;
import net.wbz.moba.controlcenter.shared.scenario.ScenarioStateEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

/**
 * Executor to start and stop {@link Scenario}s.
 * 
 * @author Daniel Tuerk
 */
@Slf4j
@ApplicationScoped
public class ScenarioExecutor {

    private final EventBroadcaster eventBroadcaster;
    private final ExecutorService executorService;
    private final ExecuteRouteSequence executeRouteSequence;
    private final Event<ScenarioStateEvent> scenarioStateEvent;

    /**
     * Running executions of each scenario by id.
     */
    private final Map<Long, Cancellable> executionsByScenarioId = new ConcurrentHashMap<>();

    ScenarioExecutor(EventBroadcaster eventBroadcaster,
                     @VirtualThreads ExecutorService executorService,
                     ExecuteRouteSequence executeRouteSequence,
                     Event<ScenarioStateEvent> scenarioStateEvent) {
        this.eventBroadcaster = eventBroadcaster;
        this.executorService = executorService;
        this.executeRouteSequence = executeRouteSequence;
        this.scenarioStateEvent = scenarioStateEvent;
    }

    /**
     * TODO muss auch den job anlegen etc. raus ziehen aus scenario service
     */
    public void scheduleScenario(Scenario scenario) {
        throw new RuntimeException("Scheduling of scenarios is not implemented yet.");
//        fireEvent(scenario.getId(), RUN_STATE.SCHEDULED);
//        if (!scenarioStates.contains(scenario.getId())) {
//            scenario.setMode(MODE.AUTOMATIC);
//            scenario.setRunState(RUN_STATE.IDLE);
//            scenarioStates.add(new ScenarioState(scenario.getId(), scenario.getMode(), scenario.getRunState()));
//        }
//
//        fireEvent(scenario);
    }

    /**
     * Start the given {@link Scenario} if not already running.
     *
     * @param scenario {@link Scenario}
     */
    public synchronized void startScenario(Scenario scenario) {
        if (!executionsByScenarioId.containsKey(scenario.getId())) {
            final var scenarioExecution = new ScenarioExecution(executeRouteSequence);

            fireEvent(scenario.getId(), RUN_STATE.RUNNING);
            executionsByScenarioId.put(scenario.getId(), scenarioExecution.start(scenario)
                .runSubscriptionOn(executorService)
                .subscribe()
                .with(runState -> {
                    log.info("scenario execution finished with state: {}", runState);
                    finishExecution(scenario);
                    fireEvent(scenario.getId(), runState);
                }, failure -> {
                    log.error("scenario execution failed: {}", failure.getMessage(), failure);
                    finishExecution(scenario);
                    fireEvent(scenario.getId(), RUN_STATE.FAILED);
                })
            );
        } else {
            log.error("scenario '{}' already running!", scenario.getName());
        }
    }

    /**
     * Stop the running {@link Scenario}.
     *
     * @param scenario {@link Scenario}
     */
    public synchronized void stopScenario(Scenario scenario) {
        final var scenarioId = scenario.getId();
        if (executionsByScenarioId.containsKey(scenarioId)) {
            log.info("cancel scenario execution for scenario '{}'", scenario.getName());
            var scenarioExecution = executionsByScenarioId.get(scenarioId);
            scenarioExecution.cancel();
            finishExecution(scenario);
            fireEvent(scenarioId, RUN_STATE.STOPPED);
        }
    }

    private void finishExecution(Scenario scenario) {
        executionsByScenarioId.remove(scenario.getId());
    }

    private void fireEvent(Long scenarioId, RUN_STATE state) {
        final var event = new ScenarioStateEvent(scenarioId, state, null);
        scenarioStateEvent.fire(event);
        eventBroadcaster.fireEvent(event);
    }
}
