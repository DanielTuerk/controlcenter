package net.wbz.moba.controlcenter.web.client.viewer.controls.scenario;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.wbz.moba.controlcenter.web.client.Callbacks.OnlySuccessAsyncCallback;
import net.wbz.moba.controlcenter.web.client.RequestUtils;
import net.wbz.moba.controlcenter.web.client.viewer.controls.AbstractItemViewerPanel;
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
}
