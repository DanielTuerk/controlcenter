package net.wbz.moba.controlcenter.service.scenario;

import io.quarkus.runtime.util.StringUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.wbz.moba.controlcenter.service.scenario.execution.ScenarioExecutor;
import net.wbz.moba.controlcenter.shared.scenario.Scenario;
import net.wbz.moba.controlcenter.shared.scenario.Scenario.MODE;
import net.wbz.moba.controlcenter.shared.scenario.Scenario.RUN_STATE;
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

import java.util.HashMap;
import java.util.Map;

/**
 * @author Daniel Tuerk
 */
@Slf4j
@ApplicationScoped
public class ScenarioService {

    private static final String JOB_SCENARIO_PREFIX = "job-scenario-";
    private static final String TRIGGER_SCENARIO_PREFIX = "trigger-scenario-";

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
     * Mapping of scenario id to {@link TriggerKey}s to unschedule running jobs.
     */
    private final Map<Long, TriggerKey> scenarioTriggerKeys = new HashMap<>();

    @Inject
    public ScenarioService(ScenarioManager scenarioManager, DeviceManager deviceManager,
                           ScenarioExecutor scenarioExecutor) {
        this.scenarioManager = scenarioManager;
        this.deviceManager = deviceManager;
        this.scenarioExecutor = scenarioExecutor;

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

    public void start(Scenario scenario) {
        scenario.setMode(MODE.MANUAL);

        startScenario(scenario);
    }

    public void schedule(Scenario scenario) {
        if (scenario.getRunState() != RUN_STATE.RUNNING) {
            var scenarioId = scenario.getId();
            String cron = scenario.getCron();
            if (!StringUtil.isNullOrEmpty(cron)) {

                JobDetail job = JobBuilder.newJob(ScheduleScenarioJob.class)
                        .withIdentity(JobKey.jobKey(JOB_SCENARIO_PREFIX + scenarioId))
                        .usingJobData("scenario", scenarioId).build();

                TriggerKey triggerKey = TriggerKey.triggerKey(TRIGGER_SCENARIO_PREFIX + scenarioId);
                scenarioTriggerKeys.put(scenarioId, triggerKey);

                Trigger trigger = TriggerBuilder.newTrigger().withIdentity(triggerKey)
                        .withSchedule(CronScheduleBuilder.cronSchedule(cron)).forJob(job).build();

                scenarioExecutor.scheduleScenario(scenario);

                // Tell quartz to schedule the job using our trigger
                try {
                    scheduler.scheduleJob(job, trigger);
                    log.info("scenario ({}) scheduled: {}", scenario, cron);
                } catch (SchedulerException e) {
                    log.error("error by schedule job", e);
                }

            } else {
                log.error("no cron expression");
            }
        } else {
            log.warn("Can't schedule scenario: {} - not in IDLE state (actual {})", scenario, scenario.getRunState());
        }
    }

    public void scheduleAll() {
        scenarioManager.getScenarios().forEach(this::schedule);
    }

    public void stop(Scenario scenario) {
        var scenarioId = scenario.getId();
        log.debug("stop scenario: {}", scenarioId);
        unscheduleScenario(scenarioId);
        scenarioExecutor.stopScenario(scenario);
        scenario.setMode(MODE.OFF);
    }

    public void stopAll() {
        scenarioManager.getScenarios().forEach(this::stop);
    }

    /**
     * TODO refactor to job
     *
     * @param scenarioId id of {@link Scenario}
     */
    public synchronized void foobarStartScheduledScenario(long scenarioId) {
        Scenario scenario = scenarioManager.getScenarioById(scenarioId).orElseThrow(
            () -> new RuntimeException("no scenario found for id: %d".formatted(scenarioId)));
        log.info("Start scheduled scenario: {}", scenario);
        if (scenario.getMode() != MODE.AUTOMATIC) {
            log.error("Scenario ({}) not in {} mode to schedule!", scenario, MODE.AUTOMATIC.name());
            // stop execution if scenario not anymore in automatic mode
            unscheduleScenario(scenarioId);
        }
        // TODO should be wrong
//        // reset state to schedule for next scheduled execution unless the job is unscheduled
//        scenarioExecutor.addScenarioStateListener(new DefaultScenarioStateListener() {
//            @Override
//            public void scenarioFinished(Scenario scenario) {
//                if (scenario.getMode() == MODE.AUTOMATIC) {
//                    scenarioExecutor.scheduleScenario(scenario);
//                }
//            }
//        });

        startScenario(scenario);
    }

    private void startScenario(final Scenario scenario) {
        if (deviceManager.isConnected()) {
            if (scenario.getRunState() != RUN_STATE.RUNNING) {
                scenarioExecutor.startScenario(scenario);
            } else {
                log.error("scenario already running");
            }
        }
    }

    private void unscheduleScenario(long scenarioId) {
        if (scenarioTriggerKeys.containsKey(scenarioId)) {
            try {
                scheduler.unscheduleJob(scenarioTriggerKeys.get(scenarioId));
            } catch (SchedulerException e) {
                log.error("can't unschedule job for trigger key of scenario id: " + scenarioId);
            }
        }
    }

}
