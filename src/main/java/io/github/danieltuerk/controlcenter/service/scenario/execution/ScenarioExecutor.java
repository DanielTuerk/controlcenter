package io.github.danieltuerk.controlcenter.service.scenario.execution;

import io.github.danieltuerk.controlcenter.MetricConstants;
import io.github.danieltuerk.controlcenter.service.scenario.ScenarioStateEventPublisher;
import io.github.danieltuerk.controlcenter.service.scenario.execution.route.RouteSequenceExecution;
import io.github.danieltuerk.controlcenter.shared.scenario.Scenario;
import io.github.danieltuerk.controlcenter.shared.scenario.Scenario.RUN_STATE;
import io.micrometer.core.instrument.MeterRegistry;
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
    private final MeterRegistry meterRegistry;

    /**
     * Running executions of each scenario by id.
     */
    private final Map<Long, Cancellable> executionsByScenarioId = new ConcurrentHashMap<>();

    ScenarioExecutor(@VirtualThreads ExecutorService executorService,
                     RouteSequenceExecution routeSequenceExecution,
                     ScenarioStateEventPublisher eventPublisher,
                     MeterRegistry meterRegistry) {
        this.eventPublisher = eventPublisher;
        this.executorService = executorService;
        this.routeSequenceExecution = routeSequenceExecution;
        this.meterRegistry = meterRegistry;
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
            meterRegistry.counter(MetricConstants.SCENARIO_STARTED_TOTAL).increment();
            executionsByScenarioId.put(scenario.getId(), scenarioExecution.start(scenario)
                .runSubscriptionOn(executorService)
                .subscribe()
                .with(runState -> {
                    log.info("scenario execution finished with state: {}", runState);
                    finishExecution(scenario);
                    eventPublisher.fireEvent(scenario.getId(), runState);
                    meterRegistry.counter(MetricConstants.SCENARIO_FINISHED_TOTAL, "result", runState.name())
                            .increment();
                }, failure -> {
                    log.error("scenario execution failed: {}", failure.getMessage(), failure);
                    finishExecution(scenario);
                    eventPublisher.fireEvent(scenario.getId(), RUN_STATE.FAILED);
                    meterRegistry.counter(MetricConstants.SCENARIO_FINISHED_TOTAL, "result", RUN_STATE.FAILED.name())
                            .increment();
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
            meterRegistry.counter(MetricConstants.SCENARIO_FINISHED_TOTAL, "result", RUN_STATE.STOPPED.name())
                    .increment();
        }
    }

    private void finishExecution(Scenario scenario) {
        executionsByScenarioId.remove(scenario.getId());
    }

}
