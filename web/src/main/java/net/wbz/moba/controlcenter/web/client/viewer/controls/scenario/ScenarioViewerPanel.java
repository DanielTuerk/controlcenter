package net.wbz.moba.controlcenter.web.client.viewer.controls.scenario;

import com.google.common.collect.Lists;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.gen2.logging.shared.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import net.wbz.moba.controlcenter.web.client.ServiceUtils;
import net.wbz.moba.controlcenter.web.client.viewer.controls.AbstractItemViewerPanel;
import net.wbz.moba.controlcenter.web.shared.scenario.Scenario;
import net.wbz.moba.controlcenter.web.shared.scenario.ScenarioStateEvent;
import org.gwtbootstrap3.client.ui.TextBox;

import java.util.List;

/**
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
public class ScenarioViewerPanel extends AbstractItemViewerPanel<ScenarioItemPanel, ScenarioStateEvent> {

    public ScenarioViewerPanel() {
        super();
    }

    @Override
    protected List<Class<ScenarioStateEvent>> getStateEventClasses() {
        return Lists.newArrayList(ScenarioStateEvent.class);
    }

    @Override
    protected ClickHandler getBtnNewClickHandler(final TextBox textBox) {
        return new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                ServiceUtils.getScenarioEditorService().createScenario(textBox.getText(), new AsyncCallback<Void>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        Log.severe("", "", caught);     //TODO
                    }

                    @Override
                    public void onSuccess(Void result) {
                        textBox.setText("");
                        loadData();
                    }
                });
            }
        };
    }

    @Override
    protected void loadItems() {
        ServiceUtils.getScenarioEditorService().getScenarios(new AsyncCallback<List<Scenario>>() {
            @Override
            public void onFailure(Throwable caught) {
                Log.severe("", "", caught);     //TODO
            }

            @Override
            public void onSuccess(List<Scenario> result) {
                for (Scenario scenario : result) {
                    ScenarioItemPanel scenarioItemPanel = new ScenarioItemPanel(scenario);
                    addItemPanel(scenarioItemPanel);
                }
            }
        });
    }
}
