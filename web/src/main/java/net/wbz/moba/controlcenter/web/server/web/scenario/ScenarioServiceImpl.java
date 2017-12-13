package net.wbz.moba.controlcenter.web.server.web.scenario;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.NotImplementedException;
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

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import net.wbz.moba.controlcenter.web.server.EventBroadcaster;
import net.wbz.moba.controlcenter.web.server.web.scenario.execution.ScenarioExecutor;
import net.wbz.moba.controlcenter.web.shared.scenario.Scenario;
import net.wbz.moba.controlcenter.web.shared.scenario.Scenario.MODE;
import net.wbz.moba.controlcenter.web.shared.scenario.Scenario.RUN_STATE;
import net.wbz.moba.controlcenter.web.shared.scenario.ScenarioService;
import net.wbz.moba.controlcenter.web.shared.scenario.ScenarioStateEvent;
import net.wbz.moba.controlcenter.web.shared.train.Train;
import net.wbz.selectrix4java.device.DeviceManager;

/**
 * Implementation of {@link ScenarioService}.
 * 
 * @author Daniel Tuerk
 */
@Singleton
public class ScenarioServiceImpl extends RemoteServiceServlet implements ScenarioService {

    private static final String JOB_SCENARIO_PREFIX = "job-scenario-";
    private static final String TRIGGER_SCENARIO_PREFIX = "trigger-scenario-";
    private static final Logger LOG = LoggerFactory.getLogger(ScenarioServiceImpl.class);

    /**
     * TODO REMOVE this ugly hack.
     */
    private static ScenarioServiceImpl INSTANCE;
    /**
     * Manager for the scenario data.
     */
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

    @Inject
    public ScenarioServiceImpl(ScenarioManager scenarioManager, DeviceManager deviceManager,
            ScenarioHistoryService scenarioHistoryService, ScenarioExecutor scenarioExecutor,
            EventBroadcaster eventBroadcaster) {
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

        addScenarioStateListener(scenarioHistoryService);

        INSTANCE = this;
    }

    /**
     * TODO refactor
     * 
     * @return
     */
    public static ScenarioServiceImpl getInstance() {
        return INSTANCE;
    }

    public void addScenarioStateListener(ScenarioStateListener listener) {
        scenarioExecutor.addScenarioStateListener(listener);
    }

    public void removeScenarioStateListener(ScenarioStateListener listener) {
        scenarioExecutor.removeScenarioStateListener(listener);
    }

    @Override
    public void start(long scenarioId) {
        Scenario scenario = scenarioManager.getScenarioById(scenarioId);
        scenario.setMode(MODE.MANUAL);

        startScenario(scenario);
    }

    @Override
    public void schedule(long scenarioId) {
        final Scenario scenarioById = scenarioManager.getScenarioById(scenarioId);

        if (scenarioById.getRunState() != RUN_STATE.RUNNING) {

            String cron = scenarioById.getCron();
            if (!Strings.isNullOrEmpty(cron)) {

                JobDetail job = JobBuilder.newJob(ScheduleScenarioJob.class)
                        .withIdentity(JobKey.jobKey(JOB_SCENARIO_PREFIX + scenarioId))
                        .usingJobData("scenario", scenarioId)
                        .build();

                Trigger trigger = TriggerBuilder.newTrigger()
                        .withIdentity(TriggerKey.triggerKey(TRIGGER_SCENARIO_PREFIX + scenarioId))
                        .withSchedule(CronScheduleBuilder.cronSchedule(cron))
                        .forJob(job)
                        .build();

                scenarioById.setRunState(RUN_STATE.IDLE);
                scenarioById.setMode(MODE.AUTOMATIC);

                // TODO
                fireScenarioStateChangeEvent(scenarioById);

                // Tell quartz to schedule the job using our trigger
                try {
                    scheduler.scheduleJob(job, trigger);
                } catch (SchedulerException e) {
                    LOG.error("error by schedule job", e);
                }

            } else {
                LOG.error("no cron expression");
            }
        } else {
            LOG.warn("Can't schedule scenario: {} - not in IDLE state (actual {})", scenarioById, scenarioById
                    .getRunState());
        }
    }

    private void fireScenarioStateChangeEvent(Scenario scenario) {
        eventBroadcaster.fireEvent(new ScenarioStateEvent(scenario.getId(), scenario.getRunState()));
    }

    @Override
    public void stop(long scenarioId) {
        stopScenario(scenarioId);
    }

    @Override
    public void pause(long scenarioId) {
        // TODO
        throw new NotImplementedException("");
    }

    /**
     * Get scenarios for the given train.
     *
     * @param train {@link Train}
     * @return {@link Scenario}s
     */
    public Iterable<Scenario> getScenariosOfTrain(final Train train) {
        return Iterables.filter(scenarioManager.getScenarios(), new Predicate<Scenario>() {
            @Override
            public boolean apply(Scenario input) {
                return input.getTrain().equals(train);
            }
        });
    }

    /**
     * Get the running scenario for the given train.
     *
     * @param train {@link Train}
     * @return {@link Optional} for {@link Scenario}
     */
    public Optional<Scenario> getRunningScenarioOfTrain(Train train) {
        for (Scenario scenario : getScenariosOfTrain(train)) {
            if (scenario.getRunState() == RUN_STATE.RUNNING) {
                return Optional.of(scenario);
            }
        }
        return Optional.absent();
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

            LOG.error("Scenario ({" + scenario + "}) not in {"
                    + MODE.AUTOMATIC.name() + "} mode to schedule!");

            // stop execution if scenario not anymore in automatic mode
            String triggerKey = TRIGGER_SCENARIO_PREFIX + scenarioId;
            try {
                scheduler.unscheduleJob(TriggerKey.triggerKey(triggerKey));
            } catch (SchedulerException e) {
                LOG.error("unschedule job by trigger: " + triggerKey, e);
            }
        }
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
        Scenario scenario = scenarioManager.getScenarioById(scenarioId);
        scenarioExecutor.stopScenario(scenario);
    }

}
