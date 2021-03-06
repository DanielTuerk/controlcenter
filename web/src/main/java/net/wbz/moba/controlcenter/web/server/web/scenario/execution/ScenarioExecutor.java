package net.wbz.moba.controlcenter.web.server.web.scenario.execution;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import net.wbz.moba.controlcenter.web.server.event.EventBroadcaster;
import net.wbz.moba.controlcenter.web.server.persist.scenario.TrackBuilder;
import net.wbz.moba.controlcenter.web.server.web.editor.block.SignalBlockRegistry;
import net.wbz.moba.controlcenter.web.server.web.scenario.RouteListener;
import net.wbz.moba.controlcenter.web.server.web.scenario.ScenarioStateListener;
import net.wbz.moba.controlcenter.web.server.web.scenario.ScenarioUtil;
import net.wbz.moba.controlcenter.web.server.web.train.TrainManager;
import net.wbz.moba.controlcenter.web.server.web.train.TrainServiceImpl;
import net.wbz.moba.controlcenter.web.server.web.viewer.TrackViewerServiceImpl;
import net.wbz.moba.controlcenter.web.shared.scenario.Scenario;
import net.wbz.moba.controlcenter.web.shared.scenario.Scenario.MODE;
import net.wbz.moba.controlcenter.web.shared.scenario.Scenario.RUN_STATE;
import net.wbz.moba.controlcenter.web.shared.scenario.ScenarioStateEvent;
import net.wbz.moba.controlcenter.web.shared.train.TrainService;
import net.wbz.selectrix4java.device.DeviceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Executor to start and stop {@link Scenario}s.
 * 
 * @author Daniel Tuerk
 */
@Singleton
public class ScenarioExecutor {

    private static final Logger LOG = LoggerFactory.getLogger(ScenarioExecutor.class);
//    private final ExecutorService taskExecutor;

    private final TrackViewerServiceImpl trackViewerService;
    private final TrainService trainService;
    private final SignalBlockRegistry signalBlockRegistry;
    private final TrainManager trainManager;
    private final DeviceManager deviceManager;
    private final TrackBuilder trackBuilder;
    private final RouteExecutionObserver routeExecutionObserver;
    /**
     * Broadcaster for client side event handling of state changes.
     */
    private final EventBroadcaster eventBroadcaster;
    /**
     * Running executions of each scenario by id.
     */
    private final Map<Long, ScenarioExecution> executionsByScenarioId = new ConcurrentHashMap<>();
    /**
     * Server side listeners for state changes.
     */
    private final List<ScenarioStateListener> listeners = new CopyOnWriteArrayList<>();
    /**
     * Server side listeners for route run state changes.
     */
    private final List<RouteListener> routeListeners = new CopyOnWriteArrayList<>();

    @Inject
    ScenarioExecutor(TrackViewerServiceImpl trackViewerService, TrainServiceImpl trainService,
            SignalBlockRegistry signalBlockRegistry, TrainManager trainManager, DeviceManager deviceManager,
            TrackBuilder trackBuilder, EventBroadcaster eventBroadcaster,
            ScenarioRouteEventBroadcaster scenarioRouteEventBroadcaster,
            RouteExecutionObserver routeExecutionObserver) {
        this.trackViewerService = trackViewerService;
        this.trainService = trainService;
        this.signalBlockRegistry = signalBlockRegistry;
        this.trainManager = trainManager;
        this.deviceManager = deviceManager;
        this.trackBuilder = trackBuilder;
        this.eventBroadcaster = eventBroadcaster;
        this.routeExecutionObserver = routeExecutionObserver;

//        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat(getClass().getSimpleName() + "-%d")
//                .build();
//        taskExecutor = Executors.newCachedThreadPool(namedThreadFactory);

        routeListeners.add(scenarioRouteEventBroadcaster);
    }

    /**
     * TODO muss auch den job anlegen etc. raus ziehen aus scenario service
     * @param scenario
     */
    public void scheduleScenario(Scenario scenario) {
        scenario.setMode(MODE.AUTOMATIC);
        scenario.setRunState(RUN_STATE.IDLE);

        fireEvent(scenario);
    }

    /**
     * Start the given {@link Scenario} if not already running.
     *
     * @param scenario {@link Scenario}
     */
    public synchronized void startScenario(Scenario scenario) {
        if (!executionsByScenarioId.containsKey(scenario.getId())) {
            final ScenarioExecution scenarioExecution = new ScenarioExecution(scenario, trackViewerService,
                    trainService, signalBlockRegistry, deviceManager, trainManager, trackBuilder,
                    routeListeners, routeExecutionObserver) {
                @Override
                protected void scenarioExecutionFinished(Scenario scenario) {
                    finishExecution(scenario);
                    fireEvent(scenario);
                }
            };
            executionsByScenarioId.put(scenario.getId(), scenarioExecution);
            // TODO
//            taskExecutor.submit(new ScenarioCallable(scenarioExecution));
            Thread thread = new Thread(new ScenarioCallable(scenarioExecution));
            thread.start();
        } else {
            LOG.error("scenario {} already running!", scenario);
        }
    }

    /**
     * Stop the running {@link Scenario}.
     *
     * @param scenario {@link Scenario}
     */
    public synchronized void stopScenario(Scenario scenario) {
        Long scenarioId = scenario.getId();
        if (executionsByScenarioId.containsKey(scenarioId)) {
            ScenarioExecution scenarioExecution = executionsByScenarioId.get(scenarioId);
            scenarioExecution.stop();
        } else {
            scenario.setRunState(RUN_STATE.STOPPED);
        }
        fireEvent(scenario);
    }

    public void addScenarioStateListener(ScenarioStateListener listener) {
        listeners.add(listener);
    }

    public void removeScenarioStateListener(ScenarioStateListener listener) {
        listeners.remove(listener);
    }

    public void addRouteListener(RouteListener listener) {
        routeListeners.add(listener);
    }

    public void removeRouteListener(RouteListener listener) {
        routeListeners.remove(listener);
    }

    private void finishExecution(Scenario scenario) {
        executionsByScenarioId.remove(scenario.getId());
    }

    private void fireEvent(Scenario scenario) {
        for (ScenarioStateListener listener : listeners) {
            switch (scenario.getRunState()) {
                case RUNNING:
                    listener.scenarioStarted(scenario);
                    break;
                case IDLE:
                    listener.scenarioQueued(scenario);
                    break;
                case PAUSED:
                    listener.scenarioPaused(scenario);
                    break;
                case SUCCESS:
                    listener.scenarioSuccessfullyExecuted(scenario);
                case ERROR:
                    listener.scenarioExecuteWithError(scenario);
                case STOPPED:
                    listener.scenarioStopped(scenario);
                default:
                    listener.scenarioFinished(scenario);
                    break;
            }
        }
        eventBroadcaster.fireEvent(new ScenarioStateEvent(scenario.getId(), scenario.getRunState(),
            ScenarioUtil.nextExecutionTime(scenario)));
    }

}
