package net.wbz.moba.controlcenter.web.client.viewer.controls.scenario;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.novanic.eventservice.client.event.Event;
import de.novanic.eventservice.client.event.listener.RemoteEventListener;
import net.wbz.moba.controlcenter.web.client.Callbacks.OnlySuccessAsyncCallback;
import net.wbz.moba.controlcenter.web.client.EventReceiver;
import net.wbz.moba.controlcenter.web.client.RequestUtils;
import net.wbz.moba.controlcenter.web.client.viewer.controls.AbstractItemViewerPanel;
import net.wbz.moba.controlcenter.web.shared.scenario.RouteStateEvent;
import net.wbz.moba.controlcenter.web.shared.scenario.Scenario;
import net.wbz.moba.controlcenter.web.shared.scenario.ScenarioStateEvent;
import net.wbz.moba.controlcenter.web.shared.scenario.ScenariosChangedEvent;

/**
 * Viewer for the {@link Scenario}s.
 * 
 * @author Daniel Tuerk
 */
public class ScenarioViewerPanel extends
        AbstractItemViewerPanel<ScenarioItemPanel, ScenarioStateEvent, ScenariosChangedEvent> {

    /**
     * Listener for the {@link RouteStateEvent}s to delegate to update the route state of the {@link ScenarioItemPanel}.
     */
    private RemoteEventListener routeStateListener;
    /**
     * Mapping of scenario ids to the {@link ScenarioItemPanel}s.
     */
    private Map<Long, ScenarioItemPanel> scenarioIdItemPanels = new HashMap<>();

    @Override
    protected List<Class<? extends ScenarioStateEvent>> getStateEventClasses() {
        List<Class<? extends ScenarioStateEvent>> classes = new ArrayList<>();
        classes.add(ScenarioStateEvent.class);
        return classes;
    }

    @Override
    protected Class<ScenariosChangedEvent> getDataEventClass() {
        return ScenariosChangedEvent.class;
    }

    @Override
    protected void loadItems() {
        scenarioIdItemPanels.clear();
        RequestUtils.getInstance().getScenarioEditorService().getScenarios(
                new OnlySuccessAsyncCallback<Collection<Scenario>>() {
                    @Override
                    public void onSuccess(Collection<Scenario> result) {
                        for (Scenario scenario : result) {
                            addItemPanel(new ScenarioItemPanel(scenario));
                        }
                    }
                });
    }

    @Override
    protected void addListeners() {
        super.addListeners();
        routeStateListener = new RemoteEventListener() {

            @Override
            public void apply(Event anEvent) {
                if (anEvent instanceof RouteStateEvent) {
                    Long scenarioId = ((RouteStateEvent) anEvent).getScenarioId();
                    if (scenarioIdItemPanels.containsKey(scenarioId)) {
                        scenarioIdItemPanels.get(scenarioId).updateRouteState((RouteStateEvent) anEvent);
                    }
                }
            }
        };
        EventReceiver.getInstance().addListener(RouteStateEvent.class, routeStateListener);
    }

    @Override
    protected void removeListeners() {
        super.removeListeners();
        EventReceiver.getInstance().removeListener(RouteStateEvent.class, routeStateListener);
    }
}
