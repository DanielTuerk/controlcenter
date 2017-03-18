package net.wbz.moba.controlcenter.web.client.viewer.controls.scenario;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.gwtbootstrap3.client.ui.TextBox;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import net.wbz.moba.controlcenter.web.client.Callbacks.OnlySuccessAsyncCallback;
import net.wbz.moba.controlcenter.web.client.RequestUtils;
import net.wbz.moba.controlcenter.web.client.viewer.controls.AbstractItemViewerPanel;
import net.wbz.moba.controlcenter.web.shared.scenario.Scenario;
import net.wbz.moba.controlcenter.web.shared.scenario.ScenarioStateEvent;

/**
 * TODO: reload trains for configuration changes
 * Created by Daniel on 08.03.14.
 */
public class ScenarioViewerPanel extends AbstractItemViewerPanel<ScenarioItemPanel, ScenarioStateEvent> {

    @Override
    protected List<Class<ScenarioStateEvent>> getStateEventClasses() {
        List<Class<ScenarioStateEvent>> classes = new ArrayList<>();
        classes.add(ScenarioStateEvent.class);
        return classes;
    }

    @Override
    protected ClickHandler getBtnNewClickHandler(final TextBox name) {
        // TODO only for train
        return new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
            }
        };
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
