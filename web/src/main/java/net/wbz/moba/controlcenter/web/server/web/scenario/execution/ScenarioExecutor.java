package net.wbz.moba.controlcenter.web.server.web.scenario.execution;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import net.wbz.moba.controlcenter.web.server.EventBroadcaster;
import net.wbz.moba.controlcenter.web.server.persist.scenario.TrackBuilder;
import net.wbz.moba.controlcenter.web.server.web.editor.block.SignalBlockRegistry;
import net.wbz.moba.controlcenter.web.server.web.scenario.ScenarioManager;
import net.wbz.moba.controlcenter.web.server.web.scenario.ScenarioStateListener;
import net.wbz.moba.controlcenter.web.server.web.train.TrainManager;
import net.wbz.moba.controlcenter.web.server.web.train.TrainServiceImpl;
import net.wbz.moba.controlcenter.web.server.web.viewer.TrackViewerServiceImpl;
import net.wbz.moba.controlcenter.web.shared.scenario.Scenario;
import net.wbz.moba.controlcenter.web.shared.scenario.Scenario.RUN_STATE;
import net.wbz.moba.controlcenter.web.shared.scenario.ScenarioStateEvent;
import net.wbz.moba.controlcenter.web.shared.train.TrainService;
import net.wbz.selectrix4java.device.DeviceManager;

/**
 * Executor to start and stop {@link Scenario}s.
 * 
 * @author Daniel Tuerk
 */
@Singleton
public class ScenarioExecutor {

    private static final Logger LOG = LoggerFactory.getLogger(ScenarioExecutor.class);
    private final ExecutorService taskExecutor;

    private final TrackViewerServiceImpl trackViewerService;
    private final TrainService trainService;
    private final SignalBlockRegistry signalBlockRegistry;
    private final TrainManager trainManager;
    private final DeviceManager deviceManager;
    private final TrackBuilder trackBuilder;

    /**
     * Running executions of each scenario by id.
     */
    private final Map<Long, ScenarioExecution> executionsByScenarioId = new ConcurrentHashMap<>();
    /**
     * Broadcaster for client side event handling of state changes.
     */
    private final EventBroadcaster eventBroadcaster;
    /**
     * Server side listeners for state changes.
     */
    private final List<ScenarioStateListener> listeners = new ArrayList<>();
    private final ScenarioManager scenarioManager;

    @Inject
    public ScenarioExecutor(TrackViewerServiceImpl trackViewerService, TrainServiceImpl trainService,
            SignalBlockRegistry signalBlockRegistry, TrainManager trainManager, DeviceManager deviceManager,
            TrackBuilder trackBuilder, EventBroadcaster eventBroadcaster, ScenarioManager scenarioManager) {
        this.trackViewerService = trackViewerService;
        this.trainService = trainService;
        this.signalBlockRegistry = signalBlockRegistry;
        this.trainManager = trainManager;
        this.deviceManager = deviceManager;
        this.trackBuilder = trackBuilder;
        this.eventBroadcaster = eventBroadcaster;
        this.scenarioManager = scenarioManager;

        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat(getClass().getSimpleName() + "-%d")
                .build();
        // TODO shutdown
        taskExecutor = Executors.newCachedThreadPool(namedThreadFactory);
    }

    /**
     * Start the given {@link Scenario} if not already running.
     *
     * @param scenario {@link Scenario}
     */
    public synchronized void startScenario(Scenario scenario) {
        if (!executionsByScenarioId.containsKey(scenario.getId())) {
            final ScenarioExecution scenarioExecution = new ScenarioExecution(scenario, trackViewerService,
                    trainService, signalBlockRegistry, deviceManager, trainManager, trackBuilder, scenarioManager) {
                @Override
                protected void fireScenarioStateChangeEvent(Scenario scenario) {
                    if (scenario.getRunState() == RUN_STATE.STOPPED) {
                        finishExecution(scenario);
                    }
                }
            };
            executionsByScenarioId.put(scenario.getId(), scenarioExecution);
            taskExecutor.submit(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    scenarioExecution.start();
                    return null;
                }
            });
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
        ScenarioExecution scenarioExecution = executionsByScenarioId.get(scenario.getId());
        scenarioExecution.stop();
        finishExecution(scenario);
    }

    public void addScenarioStateListener(ScenarioStateListener listener) {
        listeners.add(listener);
    }

    public void removeScenarioStateListener(ScenarioStateListener listener) {
        listeners.remove(listener);
    }

    private void finishExecution(Scenario scenario) {
        executionsByScenarioId.remove(scenario.getId());
        ScenarioExecutor.this.fireEvent(scenario);
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

}
