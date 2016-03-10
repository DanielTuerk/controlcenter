package net.wbz.moba.controlcenter.web.server.scenario;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.wbz.moba.controlcenter.web.server.EventBroadcaster;
import net.wbz.moba.controlcenter.web.server.viewer.TrackViewerServiceImpl;
import net.wbz.moba.controlcenter.web.shared.scenario.Scenario;
import net.wbz.moba.controlcenter.web.shared.scenario.ScenarioService;
import net.wbz.moba.controlcenter.web.shared.viewer.TrackViewerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

/**
 * @author Daniel Tuerk
 */
@Singleton
public class ScenarioServiceImpl extends RemoteServiceServlet implements ScenarioService {
    private static final Logger LOG = LoggerFactory.getLogger(ScenarioServiceImpl.class);

    private final TrackViewerService trackViewerService;
    private final Executor executor = Executors.newSingleThreadExecutor();

    private final ScenarioManager scenarioManager;
    private final EventBroadcaster eventBroadcaster;

    @Inject
    public ScenarioServiceImpl(TrackViewerServiceImpl trackViewerService, ScenarioManager scenarioManager, EventBroadcaster eventBroadcaster) {
        this.trackViewerService = trackViewerService;
        this.scenarioManager = scenarioManager;

        this.eventBroadcaster = eventBroadcaster;
    }

    @Override
    public void start(long scenarioId) {
        final Scenario scenario = scenarioManager.getScenarioById(scenarioId);
        if (scenario.getRunState() != Scenario.RUN_STATE.RUNNING) { //TODO multiple ok -> check by modification for conflicts
            FutureTask<Boolean> scenarioRunTask = new FutureTask<Boolean>(new ScenarioRunCallable(scenario, trackViewerService, eventBroadcaster));
            executor.execute(scenarioRunTask);
        } else if (scenario.getRunState() == Scenario.RUN_STATE.PAUSED) {

        } else {
            LOG.error(String.format("can't start %d (%s)", scenarioId, scenario.getRunState()));
        }
    }

    @Override
    public void stop(long scenarioId) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void pause(long scenarioId) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

}
