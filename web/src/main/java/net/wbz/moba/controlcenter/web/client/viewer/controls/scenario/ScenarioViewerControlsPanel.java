package net.wbz.moba.controlcenter.web.client.viewer.controls.scenario;

import java.util.Collection;
import net.wbz.moba.controlcenter.web.client.event.EventReceiver;
import net.wbz.moba.controlcenter.web.client.event.scenario.ScenarioRemoteListener;
import net.wbz.moba.controlcenter.web.client.request.Callbacks.OnlySuccessAsyncCallback;
import net.wbz.moba.controlcenter.web.client.request.RequestUtils;
import net.wbz.moba.controlcenter.web.client.viewer.controls.AbstractViewerContainer;
import net.wbz.moba.controlcenter.web.shared.scenario.Scenario;
import org.gwtbootstrap3.client.ui.Button;

/**
 * Viewer for the {@link Scenario}s.
 *
 * @author Daniel Tuerk
 */
public class ScenarioViewerControlsPanel extends AbstractViewerContainer {

    private final ScenarioRemoteListener scenarioRemoteListener;

    public ScenarioViewerControlsPanel() {
        super();
        scenarioRemoteListener = event -> reloadItems();

        getItemsControlsPanel().add(new Button("schedule all",
            event -> RequestUtils.getInstance().getScenarioService().scheduleAll(RequestUtils.VOID_ASYNC_CALLBACK)));
        getItemsControlsPanel().add(new Button("stop all",
            event -> RequestUtils.getInstance().getScenarioService().stopAll(RequestUtils.VOID_ASYNC_CALLBACK)));
    }

    @Override
    protected void loadItems() {
        RequestUtils.getInstance().getScenarioEditorService().getScenarios(
            new OnlySuccessAsyncCallback<Collection<Scenario>>() {
                @Override
                public void onSuccess(Collection<Scenario> result) {
                    for (Scenario scenario : result) {
                        addItemPanel(new ScenarioViewerItemPanel(
                            new ScenarioItemControlsPanel(scenario)));
                    }
                }
            });
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        EventReceiver.getInstance().addListener(scenarioRemoteListener);
    }

    @Override
    protected void onUnload() {
        super.onUnload();
        EventReceiver.getInstance().removeListener(scenarioRemoteListener);
    }
}
