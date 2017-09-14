package net.wbz.moba.controlcenter.web.server.web.scenario;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import net.wbz.moba.controlcenter.web.server.web.editor.block.TrackBlockRegistry;
import net.wbz.moba.controlcenter.web.server.web.train.TrainManager;
import net.wbz.moba.controlcenter.web.server.web.train.TrainServiceImpl;
import net.wbz.moba.controlcenter.web.server.web.viewer.TrackViewerServiceImpl;
import net.wbz.moba.controlcenter.web.shared.scenario.RouteBlockPart;
import net.wbz.moba.controlcenter.web.shared.scenario.Scenario;
import net.wbz.moba.controlcenter.web.shared.scenario.Scenario.MODE;
import net.wbz.moba.controlcenter.web.shared.scenario.Scenario.RUN_STATE;
import net.wbz.moba.controlcenter.web.shared.scenario.ScenarioService;
import net.wbz.moba.controlcenter.web.shared.scenario.ScenarioStateEvent;
import net.wbz.moba.controlcenter.web.shared.track.model.BusDataConfiguration;
import net.wbz.moba.controlcenter.web.shared.track.model.Signal;
import net.wbz.moba.controlcenter.web.shared.train.Train;
import net.wbz.moba.controlcenter.web.shared.train.TrainService;
import net.wbz.selectrix4java.block.FeedbackBlockListener;
import net.wbz.selectrix4java.device.DeviceAccessException;
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
     * Service to toggle track parts of the track in the scenarios.
     */
    private final TrackViewerServiceImpl trackViewerRequest;
    /**
     * Manager for the scenario data.
     */
    private final ScenarioManager scenarioManager;
    /**
     * Broadcaster for client side event handling of state changes.
     */
    private final EventBroadcaster eventBroadcaster;
    /**
     * Server side listeners for state changes.
     */
    private final List<ScenarioStateListener> listeners = new ArrayList<>();
    /**
     * Actual registered listeners for the scenario to determine the end of the scenario run.
     * The {@link ScenarioEndpointFeedbackListener} should remove itself by successful end of run. Otherwise it's
     * removed by calling {#stopScenario}.
     */
    private final Map<Scenario, ScenarioEndpointFeedbackListener> scenarioEndpointFeedbackListenerMap = new HashMap<>();
    private final TrackBlockRegistry trackBlockRegistry;
    private final DeviceManager deviceManager;
    private final TrainManager trainManager;
    /**
     * Service to control the {@link Train} in the running {@link Scenario}.
     */
    private final TrainService trainService;
    /**
     * Scheduler to start {@link Scenario} runs by cron trigger.
     */
    private final Scheduler scheduler;

    @Inject
    public ScenarioServiceImpl(TrackViewerServiceImpl trackViewerService, ScenarioManager scenarioManager,
            EventBroadcaster eventBroadcaster, TrackBlockRegistry trackBlockRegistry, DeviceManager deviceManager,
            TrainManager trainManager, TrainServiceImpl trainService, ScenarioHistoryService scenarioHistoryService) {
        this.trackViewerRequest = trackViewerService;
        this.scenarioManager = scenarioManager;

        this.eventBroadcaster = eventBroadcaster;
        this.trackBlockRegistry = trackBlockRegistry;
        this.deviceManager = deviceManager;
        this.trainManager = trainManager;
        this.trainService = trainService;

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
        listeners.add(listener);
    }

    public void removeScenarioStateListener(ScenarioStateListener listener) {
        listeners.remove(listener);
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

        if (scenarioById.getRunState() == RUN_STATE.IDLE) {

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

                fireEvent(scenarioById);

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

    @Override
    public void stop(long scenarioId) {
        stopScenario(scenarioId);
    }

    @Override
    public void pause(long scenarioId) {
        // TODO
        throw new NotImplementedException();
    }

    /**
     * Update the track for the given {@link Scenario}.
     * Fetch the next block of the route after the given {@link Signal}. The {@link Signal} is the start point on the
     * next {@link RouteBlock}. Toggle all {@link net.wbz.moba.controlcenter.web.shared.track.model.AbstractTrackPart}
     * for that {@link RouteBlock}.
     * 
     * @param scenario running {@link Scenario}
     * @param signal current {@link Signal}
     */
    public void updateTrack(Scenario scenario, Signal signal) {
        throw new NotImplementedException("");
        // TODO
        // LOG.debug("update the track for scenario {} with start signal: {}", scenario, signal);
        // Optional<RouteBlock> routeBlockOptional = scenario.getRouteBlockForStartSignal(signal);
        // if (routeBlockOptional.isPresent()) {
        // Map<BusDataConfiguration, Boolean> trackPartStates = new HashMap<>();
        //
        // RouteBlock routeBlock = routeBlockOptional.get();
        // for (RouteBlockPart routeBlockPart : routeBlock.getRouteBlockParts()) {
        // if (routeBlockPart.getSwitchTrackPart() != null && routeBlockPart.getSwitchTrackPart()
        // .getToggleFunction() != null) {
        // trackPartStates.put(routeBlockPart.getSwitchTrackPart().getToggleFunction(), routeBlockPart
        // .isState());
        // }
        // }
        // trackViewerRequest.toggleTrackParts(trackPartStates);
        // }
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

    private void fireEvent(Scenario scenario) {
        for (ScenarioStateListener listener : listeners) {
            if (scenario.getRunState() == RUN_STATE.RUNNING) {
                listener.scenarioStarted(scenario);
            } else {
                listener.scenarioStopped(scenario);
            }
        }
        eventBroadcaster.fireEvent(new ScenarioStateEvent(scenario.getId(), scenario.getRunState()));
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
        throw new NotImplementedException();
        // TODO
        // if (deviceManager.isConnected()) {
        // if (scenario.getRunState() != RUN_STATE.RUNNING) {
        // // reload train, because the DTO is not up to date
        // Train train = trainManager.getTrain(scenario.getTrain().getId());
        // // check train available at start position
        // if (train != null && train.isPresentOnTrack()) {
        // if (scenario.getFirstRouteBlock().isPresent()) {
        // Signal startPoint = scenario.getFirstRouteBlock().get().getStartPoint();
        // if (startPoint != null) {
        // if (train.isCurrentlyInBlock(startPoint.getStopBlock())) {
        // scenario.setRunState(RUN_STATE.RUNNING);
        // // set the driving direction for the train
        // if (scenario.getTrainDrivingDirection() != null) {
        // trainService.toggleDrivingDirection(train.getId(),
        // scenario.getTrainDrivingDirection() == Train.DRIVING_DIRECTION.FORWARD);
        // }
        // // fire events to start the train by block/signal
        // fireEvent(scenario);
        //
        // // add listener for last block to determine finished execution
        // try {
        // final ScenarioEndpointFeedbackListener listener =
        // new ScenarioEndpointFeedbackListener(scenario) {
        // @Override
        // protected void scenarioFinished() {
        // try {
        // // remove itself, next run will add a new listener
        // trackBlockRegistry.removeFeedbackListener(deviceManager
        // .getConnectedDevice(),
        // scenario.getEndPoint(), this);
        // scenarioEndpointFeedbackListenerMap.remove(scenario);
        // } catch (DeviceAccessException e) {
        // e.printStackTrace();
        // }
        // scenario.setRunState(RUN_STATE.IDLE);
        // fireEvent(scenario);
        // }
        // };
        // trackBlockRegistry.addFeedbackListener(deviceManager.getConnectedDevice(), scenario
        // .getEndPoint(),
        // listener);
        // scenarioEndpointFeedbackListenerMap.put(scenario, listener);
        // } catch (DeviceAccessException e) {
        // LOG.error("add listener", e);
        // }
        //
        // } else {
        // LOG.error("train on wrong block to start: {} expected: {}", train.getCurrentBlocks(),
        // startPoint.getStopBlock());
        // }
        // }
        // }
        // } else {
        // LOG.error("train or position unknown: " + scenario.getTrain());
        // }
        // } else {
        // LOG.error("scenario already running");
        // // TODO error
        // }
        // }
    }

    private void stopScenario(long scenarioId) {
        LOG.debug("stop scenario: {}", scenarioId);
        Scenario scenario = scenarioManager.getScenarioById(scenarioId);
        // stop train
        trainService.updateDrivingLevel(scenario.getTrain().getId(), 0);

        try {
            trackBlockRegistry.removeFeedbackListener(deviceManager.getConnectedDevice(), scenario.getEndPoint(),
                    scenarioEndpointFeedbackListenerMap.get(scenario));
        } catch (DeviceAccessException e) {
            LOG.error("can't remove listener", e);
        }

        scenario.setMode(MODE.OFF);
        scenario.setRunState(RUN_STATE.STOPPED);

        fireEvent(scenario);
    }

    /**
     * Abstract {@link FeedbackBlockListener} to use for determinate the end of the {@link Scenario} run in case of
     * defined train reaching the end block.
     */
    abstract private class ScenarioEndpointFeedbackListener implements FeedbackBlockListener {
        private final Scenario scenario;

        private ScenarioEndpointFeedbackListener(Scenario scenario) {
            this.scenario = scenario;
        }

        @Override
        public void blockOccupied(int blockNr) {
        }

        @Override
        public void blockFreed(int blockNr) {
        }

        @Override
        public void trainEnterBlock(int blockNumber, int trainAddress, boolean forward) {
            if (scenario.getTrain().getAddress() == trainAddress && blockNumber == scenario.getEndPoint()
                    .getBlockFunction().getBit()) {
                scenarioFinished();
            }
        }

        abstract protected void scenarioFinished();

        @Override
        public void trainLeaveBlock(int blockNumber, int trainAddress, boolean forward) {
        }

        public Scenario getScenario() {
            return scenario;
        }
    }

}
