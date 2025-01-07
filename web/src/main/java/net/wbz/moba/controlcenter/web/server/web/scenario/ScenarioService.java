package net.wbz.moba.controlcenter.web.server.web.scenario;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import net.wbz.moba.controlcenter.web.server.event.EventBroadcaster;
import net.wbz.moba.controlcenter.web.server.web.scenario.execution.ScenarioExecutor;
import net.wbz.moba.controlcenter.web.shared.scenario.Scenario;
import net.wbz.moba.controlcenter.web.shared.scenario.Scenario.MODE;
import net.wbz.moba.controlcenter.web.shared.scenario.Scenario.RUN_STATE;
import net.wbz.moba.controlcenter.web.shared.train.Train;
import net.wbz.selectrix4java.device.DeviceManager;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Daniel Tuerk
 */
@Singleton
public class ScenarioService {

    private static final String JOB_SCENARIO_PREFIX = "job-scenario-";
    private static final String TRIGGER_SCENARIO_PREFIX = "trigger-scenario-";
    private static final Logger LOG = LoggerFactory.getLogger(ScenarioService.class);

    /**
     * TODO REMOVE this ugly hack.
     */
    private static ScenarioService INSTANCE;

    private final ScenarioManager scenarioManager;

    private final DeviceManager deviceManager;
    /**
     * Scheduler to start {@link Scenario} runs by cron trigger.
     */
    private final Scheduler scheduler;

    private final ScenarioExecutor scenarioExecutor;

    /**
     * Broadcaster for client side event handling of state changes.
     */
    private final EventBroadcaster eventBroadcaster;
    /**
     * Mapping of scenario id to {@link TriggerKey}s to unschedule running jobs.
     */
    private Map<Long, TriggerKey> scenarioTriggerKeys = new HashMap<>();

    @Inject
    public ScenarioService(ScenarioManager scenarioManager, DeviceManager deviceManager,
        ScenarioExecutor scenarioExecutor, EventBroadcaster eventBroadcaster) {
        this.scenarioManager = scenarioManager;
        this.deviceManager = deviceManager;
        this.scenarioExecutor = scenarioExecutor;
        this.eventBroadcaster = eventBroadcaster;

        // start the scheduler to trigger scenarios by cron
        try {
            scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
        } catch (SchedulerException e) {
            throw new RuntimeException("Can't create scheduler", e);
        }
        INSTANCE = this;
    }

    public static ScenarioService getInstance() {
        //TODO refactor
        return INSTANCE;
    }

    public void start(long scenarioId) {
        Scenario scenario = scenarioManager.getScenarioById(scenarioId);
        scenario.setMode(MODE.MANUAL);

        startScenario(scenario);
    }

    public void schedule(long scenarioId) {
        final Scenario scenarioById = scenarioManager.getScenarioById(scenarioId);
        if (scenarioById.getRunState() != RUN_STATE.RUNNING) {
            String cron = scenarioById.getCron();
            if (!Strings.isNullOrEmpty(cron)) {

                JobDetail job = JobBuilder.newJob(ScheduleScenarioJob.class)
                    .withIdentity(JobKey.jobKey(JOB_SCENARIO_PREFIX + scenarioId))
                    .usingJobData("scenario", scenarioId).build();

                TriggerKey triggerKey = TriggerKey.triggerKey(TRIGGER_SCENARIO_PREFIX + scenarioId);
                scenarioTriggerKeys.put(scenarioId, triggerKey);

                Trigger trigger = TriggerBuilder.newTrigger().withIdentity(triggerKey)
                    .withSchedule(CronScheduleBuilder.cronSchedule(cron)).forJob(job).build();

                scenarioExecutor.scheduleScenario(scenarioById);

                // Tell quartz to schedule the job using our trigger
                try {
                    scheduler.scheduleJob(job, trigger);
                    LOG.info("scenario (" + scenarioById + ") scheduled: " + cron);
                } catch (SchedulerException e) {
                    LOG.error("error by schedule job", e);
                }

            } else {
                LOG.error("no cron expression");
            }
        } else {
            LOG.warn("Can't schedule scenario: {} - not in IDLE state (actual {})", scenarioById,
                scenarioById.getRunState());
        }
    }

    public void scheduleAll() {
        scenarioManager.getScenarios().forEach(scenario -> schedule(scenario.getId()));
    }

    public void stop(long scenarioId) {
        stopScenario(scenarioId);
    }

    public void stopAll() {
        scenarioManager.getScenarios().forEach(scenario -> stop(scenario.getId()));
    }

    /**
     * Get scenarios for the given train.
     *
     * @param train {@link Train}
     * @return {@link Scenario}s
     */
    public Iterable<Scenario> getScenariosOfTrain(final Train train) {
        return scenarioManager.getScenarios().stream().filter(input -> input.getTrain().equals(train))
            .collect(Collectors.toList());
    }

    /**
     * Get the running scenario for the given train.
     *
     * @param train {@link Train}
     * @return {@link Optional} for {@link Scenario}
     */
    public java.util.Optional<Scenario> getRunningScenarioOfTrain(Train train) {
        for (Scenario scenario : getScenariosOfTrain(train)) {
            if (scenario.getRunState() == RUN_STATE.RUNNING) {
                return java.util.Optional.of(scenario);
            }
        }
        return java.util.Optional.empty();
    }

    /**
     * TODO refactor to job
     *
     * @param scenarioId id of {@link Scenario}
     */
    public synchronized void foobarStartScheduledScenario(long scenarioId) {
        Scenario scenario = scenarioManager.getScenarioById(scenarioId);
        LOG.info("Start scheduled scenario: {}", scenario);
        if (scenario.getMode() != MODE.AUTOMATIC) {
            LOG.error("Scenario ({" + scenario + "}) not in {" + MODE.AUTOMATIC.name() + "} mode to schedule!");
            // stop execution if scenario not anymore in automatic mode
            unscheduleScenario(scenarioId);
        }
        // reset state to schedule for next scheduled execution unless the job is unscheduled
        scenarioExecutor.addScenarioStateListener(new DefaultScenarioStateListener() {
            @Override
            public void scenarioFinished(Scenario scenario) {
                if (scenario.getMode() == MODE.AUTOMATIC) {
                    scenarioExecutor.scheduleScenario(scenario);
                }
            }
        });

        startScenario(scenario);
    }

    private void startScenario(final Scenario scenario) {
        if (deviceManager.isConnected()) {
            if (scenario.getRunState() != RUN_STATE.RUNNING) {
                scenarioExecutor.startScenario(scenario);
            } else {
                LOG.error("scenario already running");
            }
        }
    }

    private void stopScenario(long scenarioId) {
        LOG.debug("stop scenario: {}", scenarioId);
        unscheduleScenario(scenarioId);
        Scenario scenario = scenarioManager.getScenarioById(scenarioId);
        scenarioExecutor.stopScenario(scenario);
        scenario.setMode(MODE.OFF);
        // TODO also happen in scenario execution
//        scenario.setRunState(RUN_STATE.STOPPED);
//        fireScenarioStateChangeEvent(scenario);
    }

    private void unscheduleScenario(long scenarioId) {
        if (scenarioTriggerKeys.containsKey(scenarioId)) {
            try {
                scheduler.unscheduleJob(scenarioTriggerKeys.get(scenarioId));
            } catch (SchedulerException e) {
                LOG.error("can't unschedule job for trigger key of scenario id: " + scenarioId);
            }
        }
    }

}
