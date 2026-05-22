package net.wbz.moba.controlcenter.service.scenario.execution;

import io.quarkus.virtual.threads.VirtualThreads;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import net.wbz.moba.controlcenter.EventBroadcaster;
import net.wbz.moba.controlcenter.service.scenario.ScenarioStateListener;
import net.wbz.moba.controlcenter.service.scenario.ScenarioUtil;
import net.wbz.moba.controlcenter.service.scenario.route.RouteListener;
import net.wbz.moba.controlcenter.service.track.TrackProvider;
import net.wbz.moba.controlcenter.service.track.TrackViewerService;
import net.wbz.moba.controlcenter.service.track.block.TrackBlockRegistry;
import net.wbz.moba.controlcenter.service.train.TrainService;
import net.wbz.moba.controlcenter.shared.scenario.Scenario;
import net.wbz.moba.controlcenter.shared.scenario.Scenario.MODE;
import net.wbz.moba.controlcenter.shared.scenario.Scenario.RUN_STATE;
import net.wbz.moba.controlcenter.shared.scenario.ScenarioStateEvent;
import net.wbz.selectrix4java.device.DeviceManager;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;

/**
 * Executor to start and stop {@link Scenario}s.
 * 
 * @author Daniel Tuerk
 */
@Slf4j
@ApplicationScoped
public class ScenarioExecutor {

    private final TrackViewerService trackViewerService;
    private final TrainService trainService;
    private final DeviceManager deviceManager;
    private final TrackProvider trackProvider;
    private final RouteExecutionObserver routeExecutionObserver;
    private final TrackBlockRegistry trackBlockRegistry;
    private final EventBroadcaster eventBroadcaster;
    private final ExecutorService executorService;

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

    ScenarioExecutor(ScenarioRouteEventBroadcaster scenarioRouteEventBroadcaster,
                     TrackViewerService trackViewerService, TrainService trainService,
                     DeviceManager deviceManager, TrackProvider trackProvider,
                     RouteExecutionObserver routeExecutionObserver, TrackBlockRegistry trackBlockRegistry,
                     EventBroadcaster eventBroadcaster, @VirtualThreads ExecutorService executorService) {
        this.trackViewerService = trackViewerService;
        this.trainService = trainService;
        this.deviceManager = deviceManager;
        this.trackProvider = trackProvider;
        this.routeExecutionObserver = routeExecutionObserver;
        this.trackBlockRegistry = trackBlockRegistry;
        this.eventBroadcaster = eventBroadcaster;
        this.executorService = executorService;

        routeListeners.add(scenarioRouteEventBroadcaster);
    }

    /**
     * TODO muss auch den job anlegen etc. raus ziehen aus scenario service
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
                trainService, deviceManager, routeListeners, routeExecutionObserver, trackProvider,
                trackBlockRegistry, executorService) {
                @Override
                protected void scenarioExecutionFinished(Scenario scenario) {
                    finishExecution(scenario);
                    fireEvent(scenario);
                }
            };
            executionsByScenarioId.put(scenario.getId(), scenarioExecution);
            executorService.submit(scenarioExecution);
        } else {
            log.error("scenario {} already running!", scenario);
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
            scenario.getRunState() != RUN_STATE.STOPPED ? ScenarioUtil.nextExecutionTime(scenario) : null));
    }

}
