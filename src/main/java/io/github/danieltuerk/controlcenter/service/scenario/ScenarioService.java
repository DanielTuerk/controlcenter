package io.github.danieltuerk.controlcenter.service.scenario;

import io.github.danieltuerk.controlcenter.EventBroadcaster;
import io.github.danieltuerk.controlcenter.service.scenario.execution.ScenarioExecutor;
import io.github.danieltuerk.controlcenter.shared.scenario.Scenario;
import io.github.danieltuerk.controlcenter.shared.scenario.ScenarioScheduleEvent;
import io.github.danieltuerk.selectrix4java.device.DeviceManager;
import io.quarkus.runtime.Startup;
import io.quarkus.scheduler.Scheduler;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.quartz.CronExpression;
import org.quartz.TriggerKey;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * @author Daniel Tuerk
 */
@Slf4j
@ApplicationScoped
@Startup
public class ScenarioService {

    private static final String JOB_SCENARIO_PREFIX = "job-scenario-";

    private final ScenarioManager scenarioManager;
    private final DeviceManager deviceManager;
    /**
     * Scheduler to start {@link Scenario} runs by cron trigger.
     */
    private final Scheduler scheduler;
    private final ScenarioExecutor scenarioExecutor;
    private final ScenarioStateEventPublisher eventPublisher;
    private final EventBroadcaster eventBroadcaster;

    /**
     * Mapping of scenario id to {@link TriggerKey}s to unschedule running jobs.
     */
    private final Map<Long, io.quarkus.scheduler.Trigger> scenarioTriggerKeys = new HashMap<>();

    @Inject
    public ScenarioService(ScenarioManager scenarioManager, DeviceManager deviceManager, Scheduler scheduler,
                           ScenarioExecutor scenarioExecutor, ScenarioStateEventPublisher eventPublisher,
                           EventBroadcaster eventBroadcaster) {
        this.scenarioManager = scenarioManager;
        this.deviceManager = deviceManager;
        this.scheduler = scheduler;
        this.scenarioExecutor = scenarioExecutor;
        this.eventPublisher = eventPublisher;
        this.eventBroadcaster = eventBroadcaster;
    }

    public void start(Scenario scenario) {
        startScenario(scenario);
    }

    public synchronized void schedule(Scenario scenario) {
        final var scenarioId = scenario.getId();
        if (scenarioTriggerKeys.containsKey(scenarioId)) {
            log.warn("scenario already scheduled: {}", scenarioId);
            return;
        }
        final var cron = scenario.getCron();
        if (cron == null || cron.isBlank()) {
            log.error("Scenario {} has no cron expression", scenarioId);
            return;
        }
        if (!CronExpression.isValidExpression(cron)) {
            log.error("Invalid cron expression for scenario {}: {}", scenarioId, cron);
            return;
        }

        log.info("schedule scenario {} [{}] for: {}", scenario.getName(), scenario.getId(), scenario.getCron());
        scenarioTriggerKeys.put(scenarioId, scheduler.newJob(jobIdentity(scenarioId))
            .setCron(cron)
            .setTask(executionContext -> scenarioExecutor.startScenario(scenario))
            .schedule()
        );

        eventBroadcaster.fireEvent(ScenarioScheduleEvent.scheduled(scenarioId, cron));
        eventPublisher.fireEvent(scenarioId, Scenario.RUN_STATE.SCHEDULED);
    }

    public void scheduleAll() {
        scenarioManager.getScenarios().forEach(this::schedule);
    }

    public void cancelAll() {
        scenarioManager.getScenarios().forEach(this::cancel);
    }

    public void cancel(Scenario scenario) {
        var scenarioId = scenario.getId();
        log.debug("stop scenario: {}", scenarioId);
        unschedule(scenarioId);
        scenarioExecutor.stopScenario(scenario);
    }

    public void unscheduleAll() {
        new HashSet<>(scenarioTriggerKeys.keySet()).forEach(this::unschedule);
    }

    public synchronized void unschedule(long scenarioId) {
        if (scenarioTriggerKeys.containsKey(scenarioId)) {
            scheduler.unscheduleJob(jobIdentity(scenarioId));
            scenarioTriggerKeys.remove(scenarioId);
            eventBroadcaster.fireEvent(ScenarioScheduleEvent.unscheduled(scenarioId));
        }
    }

    private static String jobIdentity(Long scenarioId) {
        return JOB_SCENARIO_PREFIX + scenarioId;
    }

    private void startScenario(final Scenario scenario) {
        if (deviceManager.isConnected()) {
            scenarioExecutor.startScenario(scenario);
        } else {
            log.error("can't start scenario - no device connected");
        }
    }

}
