package net.wbz.moba.controlcenter.web.server.scenario;

import net.wbz.moba.controlcenter.web.server.EventBroadcaster;
import net.wbz.moba.controlcenter.web.shared.scenario.*;
import net.wbz.moba.controlcenter.web.shared.track.model.Configuration;
import net.wbz.moba.controlcenter.web.shared.viewer.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;

/**
 * @author Daniel Tuerk (daniel.tuerk@jambit.com)
 */
public class ScenarioRunCallable implements Callable<Boolean> {
    private static final Logger LOG = LoggerFactory.getLogger(ScenarioRunCallable.class);

    private final TrackViewerService trackViewerService;
    private final EventBroadcaster eventBroadcaster;

    private final Scenario scenario;

    public ScenarioRunCallable(Scenario scenario, TrackViewerService trackViewerService, EventBroadcaster eventBroadcaster) {
        this.scenario = scenario;
        this.trackViewerService=trackViewerService;
        this.eventBroadcaster=eventBroadcaster;
    }

    @Override
    public Boolean call() throws Exception {
        fireEvents(scenario, Scenario.RUN_STATE.RUNNING);
        LOG.info(String.format("scenario %s started", scenario.getName()));
        try {
            for (ScenarioCommand command : scenario.getCommands()) {
                // paused
                while (scenario.getRunState() == Scenario.RUN_STATE.PAUSED) {
                    Thread.sleep(500L);
                }
                // stopped
                if(scenario.getRunState()== Scenario.RUN_STATE.IDLE) {
                    LOG.info(String.format("scenario %s stopped", scenario.getName()));
                    break;
                }

                if (command instanceof ScenarioWaitCommand) {
                    Thread.sleep((long) ((ScenarioWaitCommand) command).getSeconds() * 1000L);
                } else if (command instanceof ScenarioToggleCommand) {
                    ScenarioToggleCommand toggleCommand = (ScenarioToggleCommand) command;
                    Configuration trackPartConfiguration = toggleCommand.getConfiguration();
                    if (trackViewerService.getTrackPartState(trackPartConfiguration) != toggleCommand.isState()) {
                        trackViewerService.toogleTrackPart(trackPartConfiguration, toggleCommand.isState());
                    }
                } else {
                    LOG.error(String.format("invalid scenario command %s", scenario.getClass().getName()));
                    return false;
                }
            }

        } catch (Exception e) {
            LOG.error(String.format("error in  scenario: %s", scenario.getName()), e);
        }
        LOG.info(String.format("scenario %s finished", scenario.getName()));
        fireEvents(scenario, Scenario.RUN_STATE.IDLE);

        return true;
    }

    public void stop() {
        fireEvents(scenario, Scenario.RUN_STATE.IDLE);
    }

    public void pause() {
        fireEvents(scenario, Scenario.RUN_STATE.PAUSED);
    }
    public void resume() {
        fireEvents(scenario, Scenario.RUN_STATE.RUNNING);
    }

    private void fireEvents(Scenario scenario, Scenario.RUN_STATE state) {
        scenario.setRunState(state);
        eventBroadcaster.fireEvent(new ScenarioStateEvent(scenario.getId(), state));
    }
}
