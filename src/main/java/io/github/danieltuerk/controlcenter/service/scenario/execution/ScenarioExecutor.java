package io.github.danieltuerk.controlcenter.service.scenario.execution;

import io.github.danieltuerk.controlcenter.service.scenario.ScenarioStateEventPublisher;
import io.github.danieltuerk.controlcenter.service.scenario.execution.route.RouteSequenceExecution;
import io.github.danieltuerk.controlcenter.shared.scenario.Scenario;
import io.github.danieltuerk.controlcenter.shared.scenario.Scenario.RUN_STATE;
import io.quarkus.virtual.threads.VirtualThreads;
import io.smallrye.mutiny.subscription.Cancellable;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;

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

    private final ExecutorService executorService;
    private final RouteSequenceExecution routeSequenceExecution;
    private final ScenarioStateEventPublisher eventPublisher;

    /**
     * Running executions of each scenario by id.
     */
    private final Map<Long, Cancellable> executionsByScenarioId = new ConcurrentHashMap<>();

    ScenarioExecutor(@VirtualThreads ExecutorService executorService,
                     RouteSequenceExecution routeSequenceExecution,
                     ScenarioStateEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
        this.executorService = executorService;
        this.routeSequenceExecution = routeSequenceExecution;
    }

    /**
     * Start the given {@link Scenario} if not already running.
     *
     * @param scenario {@link Scenario}
     */
    public synchronized void startScenario(Scenario scenario) {
        if (!executionsByScenarioId.containsKey(scenario.getId())) {
            final var scenarioExecution = new ScenarioExecution(routeSequenceExecution);

            eventPublisher.fireEvent(scenario.getId(), RUN_STATE.RUNNING);
            executionsByScenarioId.put(scenario.getId(), scenarioExecution.start(scenario)
                .runSubscriptionOn(executorService)
                .subscribe()
                .with(runState -> {
                    log.info("scenario execution finished with state: {}", runState);
                    finishExecution(scenario);
                    eventPublisher.fireEvent(scenario.getId(), runState);
                }, failure -> {
                    log.error("scenario execution failed: {}", failure.getMessage(), failure);
                    finishExecution(scenario);
                    eventPublisher.fireEvent(scenario.getId(), RUN_STATE.FAILED);
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
            eventPublisher.fireEvent(scenarioId, RUN_STATE.STOPPED);
        }
    }

    private void finishExecution(Scenario scenario) {
        executionsByScenarioId.remove(scenario.getId());
    }

}
