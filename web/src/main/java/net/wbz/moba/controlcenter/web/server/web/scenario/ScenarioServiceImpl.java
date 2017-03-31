package net.wbz.moba.controlcenter.web.server.web.scenario;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.NotImplementedException;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import net.wbz.moba.controlcenter.web.server.EventBroadcaster;
import net.wbz.moba.controlcenter.web.server.web.editor.block.TrackBlockRegistry;
import net.wbz.moba.controlcenter.web.server.web.train.TrainManager;
import net.wbz.moba.controlcenter.web.server.web.train.TrainServiceImpl;
import net.wbz.moba.controlcenter.web.server.web.viewer.TrackViewerServiceImpl;
import net.wbz.moba.controlcenter.web.shared.scenario.RouteBlock;
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

    private static final Logger LOG = LoggerFactory.getLogger(ScenarioServiceImpl.class);

    /**
     * TODO limit the scenarios to run asynchronous? How much for raspberry?
     */
    private static final int SCHEDULER_POOL_SIZE = 10;

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
    /**
     * Parser for the cron value in the {@link Scenario}.
     * Supports {@link CronType#UNIX} cron syntax only.
     */
    private final CronParser cronParser = new CronParser(CronDefinitionBuilder.instanceDefinitionFor(CronType.UNIX));
    /**
     * Executor to schedule {@link Scenario} runs.
     */
    private final ScheduledExecutorService scheduledExecutorService;
    private final TrainManager trainManager;
    /**
     * Service to control the {@link Train} in the running {@link Scenario}.
     */
    private final TrainService trainService;

    @Inject
    public ScenarioServiceImpl(TrackViewerServiceImpl trackViewerService, ScenarioManager scenarioManager,
            EventBroadcaster eventBroadcaster, TrackBlockRegistry trackBlockRegistry, DeviceManager deviceManager,
            TrainManager trainManager, TrainServiceImpl trainService) {
        this.trackViewerRequest = trackViewerService;
        this.scenarioManager = scenarioManager;

        this.eventBroadcaster = eventBroadcaster;
        this.trackBlockRegistry = trackBlockRegistry;
        this.deviceManager = deviceManager;
        this.trainManager = trainManager;
        this.trainService = trainService;

        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("scenario-executor-%d").build();
        scheduledExecutorService = Executors.newScheduledThreadPool(SCHEDULER_POOL_SIZE, namedThreadFactory);
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

        startScenario(scenarioId);
    }

    @Override
    public void schedule(final long scenarioId) {
        Scenario scenarioById = scenarioManager.getScenarioById(scenarioId);

        String cron = scenarioById.getCron();
        if (!Strings.isNullOrEmpty(cron)) {

            ExecutionTime executionTime = ExecutionTime.forCron(cronParser.parse(cron));
            DateTime now = DateTime.now();
            DateTime nextExecution = executionTime.nextExecution(now);

            long millisecondsToNextRun = nextExecution.minus(now.getMillis()).getMillis();

            scenarioById.setRunState(RUN_STATE.IDLE);
            scenarioById.setMode(MODE.AUTOMATIC);

            fireEvent(scenarioById);

            scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    startScenario(scenarioId);
                }
            }, millisecondsToNextRun, millisecondsToNextRun, TimeUnit.MILLISECONDS);

        } else {
            LOG.error("no cron expression");
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
        LOG.debug("update the track for scenario {} with start signal: {}", scenario, signal);
        Optional<RouteBlock> routeBlockOptional = scenario.getRouteBlockForStartSignal(signal);
        if (routeBlockOptional.isPresent()) {
            Map<BusDataConfiguration, Boolean> trackPartStates = new HashMap<>();

            RouteBlock routeBlock = routeBlockOptional.get();
            for (RouteBlockPart routeBlockPart : routeBlock.getRouteBlockParts()) {
                if (routeBlockPart.getSwitchTrackPart() != null && routeBlockPart.getSwitchTrackPart()
                        .getToggleFunction() != null) {
                    trackPartStates.put(routeBlockPart.getSwitchTrackPart().getToggleFunction(), routeBlockPart
                            .isState());
                }
            }
            trackViewerRequest.toggleTrackParts(trackPartStates);
        }
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
            }
        }
        eventBroadcaster.fireEvent(new ScenarioStateEvent(scenario.getId(), scenario.getRunState()));
    }

    private void startScenario(final long scenarioId) {
        if (deviceManager.isConnected()) {
            final Scenario scenario = scenarioManager.getScenarioById(scenarioId);
            if (scenario.getRunState() != RUN_STATE.RUNNING) {
                // reload train, because the DTO is not up to date
                Train train = trainManager.getTrain(scenario.getTrain().getId());
                // check train available at start position
                if (train != null && train.getCurrentBlock() != null) {
                    if (scenario.getFirstRouteBlock().isPresent()) {
                        Signal startPoint = scenario.getFirstRouteBlock().get().getStartPoint();
                        if (startPoint != null) {
                            if (startPoint.getStopBlock().equals(train.getCurrentBlock())) {
                                scenario.setRunState(RUN_STATE.RUNNING);
                                // set the driving direction for the train
                                if (scenario.getTrainDrivingDirection() != null) {
                                    trainService.toggleDrivingDirection(train.getId(),
                                            scenario.getTrainDrivingDirection() == Train.DRIVING_DIRECTION.FORWARD);
                                }
                                // fire events to start the train by block/signal
                                fireEvent(scenario);
                            } else {
                                LOG.error("train on wrong block to start: " + train.getCurrentBlock());
                            }
                        }
                    }
                } else {
                    LOG.error("train or position unknown: " + scenario.getTrain());
                }
                // add listener for last block to determine finished execution
                try {
                    final ScenarioEndpointFeedbackListener listener = new ScenarioEndpointFeedbackListener(scenario) {
                        @Override
                        protected void scenarioFinished() {
                            try {
                                // remove itself, next run will add a new listener
                                trackBlockRegistry.removeFeedbackListener(deviceManager.getConnectedDevice(),
                                        scenario.getEndPoint(), this);
                                scenarioEndpointFeedbackListenerMap.remove(scenario);
                            } catch (DeviceAccessException e) {
                                e.printStackTrace();
                            }
                            scenario.setRunState(RUN_STATE.IDLE);
                            fireEvent(scenario);
                        }
                    };
                    trackBlockRegistry.addFeedbackListener(deviceManager.getConnectedDevice(), scenario.getEndPoint(),
                            listener);
                    scenarioEndpointFeedbackListenerMap.put(scenario, listener);
                } catch (DeviceAccessException e) {
                    LOG.error("add listener", e);
                }
            } else {
                LOG.error("scenario already running");
                // TODO error
            }
        }
    }

    private void stopScenario(long scenarioId) {
        LOG.debug("stop scenario: {}", scenarioId);
        Scenario scenario = scenarioManager.getScenarioById(scenarioId);
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
