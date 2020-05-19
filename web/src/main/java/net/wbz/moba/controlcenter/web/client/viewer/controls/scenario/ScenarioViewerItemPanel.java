package net.wbz.moba.controlcenter.web.client.viewer.controls.scenario;

import net.wbz.moba.controlcenter.web.client.event.EventReceiver;
import net.wbz.moba.controlcenter.web.client.event.scenario.RouteStateRemoteListener;
import net.wbz.moba.controlcenter.web.client.event.scenario.ScenarioStateRemoteListener;
import net.wbz.moba.controlcenter.web.client.viewer.controls.BaseViewerItemPanel;
import net.wbz.moba.controlcenter.web.shared.scenario.RouteStateEvent;
import net.wbz.moba.controlcenter.web.shared.scenario.Scenario;
import net.wbz.moba.controlcenter.web.shared.scenario.Scenario.RUN_STATE;

/**
 * Item container for the {@link Scenario}.
 *
 * @author Daniel Tuerk
 */
public class ScenarioViewerItemPanel extends BaseViewerItemPanel<Scenario> {


    private final ScenarioStateRemoteListener scenarioStateRemoteListener;
    /**
     * Listener for the {@link RouteStateEvent}s to delegate to update the route state of the {@link
     * ScenarioViewerItemPanel}.
     */
    private RouteStateRemoteListener routeStateListener;

    ScenarioViewerItemPanel(ScenarioItemControlsPanel controlsPanel) {
        super(controlsPanel);
        final Scenario scenario = controlsPanel.getModel();

        getLblName().setText(scenario.getName());

        scenarioStateRemoteListener = event -> {
            if (scenario.getId().equals(event.getItemId())) {
                String name = event.getState().name();
                if (event.getState() == RUN_STATE.IDLE) {
                    name += " next run: " + event.getNextScheduleTimeText();
                }
                getLblState().setText(name);
            }
        };

        routeStateListener = anEvent -> {
            Long scenarioId = anEvent.getScenarioId();
            if (scenario.getId().equals(scenarioId)) {
                controlsPanel.getRouteSequenceFromId(anEvent.getRouteSequenceId()).ifPresent(routeSequence -> {
                    String text = anEvent.getState().name()
                        + " ("
                        + (routeSequence.getPosition() + 1)
                        + "/"
                        + getModel().getRouteSequences().size()
                        + ")";
                    getLblStateDetails().setText(text);
                });

                controlsPanel.updateRouteState(anEvent);
            }
        };

    }

    @Override
    protected void onLoad() {
        super.onLoad();
        EventReceiver.getInstance().addListener(scenarioStateRemoteListener, routeStateListener);
    }

    @Override
    protected void onUnload() {
        super.onUnload();
        EventReceiver.getInstance().removeListener(scenarioStateRemoteListener, routeStateListener);
    }

}
