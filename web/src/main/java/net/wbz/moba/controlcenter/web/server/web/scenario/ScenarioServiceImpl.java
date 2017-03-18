package net.wbz.moba.controlcenter.web.server.web.scenario;

import java.util.ArrayList;
import java.util.List;
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
import net.wbz.moba.controlcenter.web.server.web.viewer.TrackViewerServiceImpl;
import net.wbz.moba.controlcenter.web.shared.scenario.RouteBlock;
import net.wbz.moba.controlcenter.web.shared.scenario.RouteBlockPart;
import net.wbz.moba.controlcenter.web.shared.scenario.Scenario;
import net.wbz.moba.controlcenter.web.shared.scenario.Scenario.MODE;
import net.wbz.moba.controlcenter.web.shared.scenario.Scenario.RUN_STATE;
import net.wbz.moba.controlcenter.web.shared.scenario.ScenarioService;
import net.wbz.moba.controlcenter.web.shared.scenario.ScenarioStateEvent;
import net.wbz.moba.controlcenter.web.shared.track.model.Signal;
import net.wbz.moba.controlcenter.web.shared.train.Train;
import net.wbz.selectrix4java.block.FeedbackBlockListener;
import net.wbz.selectrix4java.device.DeviceAccessException;
import net.wbz.selectrix4java.device.DeviceManager;

/**
 * @author Daniel Tuerk
 */
@Singleton
public class ScenarioServiceImpl extends RemoteServiceServlet implements ScenarioService {
    private static final Logger LOG = LoggerFactory.getLogger(ScenarioServiceImpl.class);

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
    private final ScheduledExecutorService scheduledExecutorService;
    private final TrackBlockRegistry trackBlockRegistry;
    private final DeviceManager deviceManager;

    @Inject
    public ScenarioServiceImpl(TrackViewerServiceImpl trackViewerService, ScenarioManager scenarioManager,
            EventBroadcaster eventBroadcaster, TrackBlockRegistry trackBlockRegistry, DeviceManager deviceManager) {
        this.trackViewerRequest = trackViewerService;
        this.scenarioManager = scenarioManager;

        this.eventBroadcaster = eventBroadcaster;
        this.trackBlockRegistry = trackBlockRegistry;
        this.deviceManager = deviceManager;

        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("scenario-executor-%d").build();
        scheduledExecutorService = Executors.newScheduledThreadPool(10, namedThreadFactory);
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

            CronParser parser = new CronParser(CronDefinitionBuilder.instanceDefinitionFor(CronType.UNIX));
            ExecutionTime executionTime = ExecutionTime.forCron(parser.parse("* * * * * ? *"));
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

    private void fireEvent(Scenario scenario) {
        for (ScenarioStateListener listener : listeners) {
            if (scenario.getRunState() == RUN_STATE.RUNNING) {
                listener.scenarioStarted(scenario);
            }
        }
        eventBroadcaster.fireEvent(new ScenarioStateEvent(scenario.getId(), scenario.getRunState()));
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
        LOG.debug("update the track for scenario {} in signal: ", scenario, signal);
        Optional<RouteBlock> routeBlockOptional = scenario.getRouteBlockForStartSignal(signal);
        if (routeBlockOptional.isPresent()) {
            RouteBlock routeBlock = routeBlockOptional.get();
            for (RouteBlockPart routeBlockPart : routeBlock.getRouteBlockParts()) {
                trackViewerRequest.toggleTrackPart(routeBlockPart.getSwitchTrackPart().getToggleFunction(),
                        routeBlockPart.isState());
            }
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

    private void startScenario(final long scenarioId) {
        if (deviceManager.isConnected()) {
            final Scenario scenario = scenarioManager.getScenarioById(scenarioId);
            if (scenario.getRunState() != RUN_STATE.RUNNING) {

                scenario.setRunState(RUN_STATE.RUNNING);

                fireEvent(scenario);
            } else {
                LOG.error("scenario already running");
                // TODO error
            }
            // TODO add listener for last block to determine finished execution
            try {
                final ScenarioEndpointFeedbackListener listener = new ScenarioEndpointFeedbackListener(scenario) {
                    @Override
                    protected void scenarioFinished() {
                        try {
                            trackBlockRegistry.removeFeedbackListener(deviceManager.getConnectedDevice(),
                                    scenario.getEndPoint(), this);
                        } catch (DeviceAccessException e) {
                            e.printStackTrace();
                        }
                        scenario.setRunState(RUN_STATE.IDLE);
                        fireEvent(scenario);
                    }
                };
                trackBlockRegistry.addFeedbackListener(deviceManager.getConnectedDevice(), scenario.getEndPoint(),
                        listener);
            } catch (DeviceAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private void stopScenario(long scenarioId) {
        Scenario scenario = scenarioManager.getScenarioById(scenarioId);
        scenario.getTrain().setDrivingLevel(0);

        scenario.setMode(MODE.OFF);
        scenario.setRunState(RUN_STATE.STOPPED);

        fireEvent(scenario);
    }

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
                    .getBlockFunction().getBit() + 1) {
                scenarioFinished();
            }
        }

        abstract protected void scenarioFinished();

        @Override
        public void trainLeaveBlock(int blockNumber, int trainAddress, boolean forward) {
        }
    }
}
