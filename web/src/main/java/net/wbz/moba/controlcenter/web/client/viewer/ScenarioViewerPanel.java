package net.wbz.moba.controlcenter.web.client.viewer;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.InputAddOn;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.common.collect.Maps;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.gen2.logging.shared.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import de.novanic.eventservice.client.event.Event;
import de.novanic.eventservice.client.event.domain.Domain;
import de.novanic.eventservice.client.event.domain.DomainFactory;
import de.novanic.eventservice.client.event.listener.RemoteEventListener;
import net.wbz.moba.controlcenter.web.client.EventReceiver;
import net.wbz.moba.controlcenter.web.client.ServiceUtils;
import net.wbz.moba.controlcenter.web.shared.scenario.Scenario;
import net.wbz.moba.controlcenter.web.shared.scenario.ScenarioStateEvent;

import java.util.List;
import java.util.Map;

/**
 * @author Daniel Tuerk (daniel.tuerk@jambit.com)
 */
public class ScenarioViewerPanel extends VerticalPanel {

    private static final Domain DOMAIN = DomainFactory.getDomain("my_domain");
    private final Map<Long, ScenarioItemPanel> itemPanelByIdMap = Maps.newHashMap();
    private final VerticalPanel scenarioItemsContainerPanel  = new VerticalPanel();

    @Override
    protected void onLoad() {
        super.onLoad();

        /* Logic for GWTEventService starts here */
        //add a listener to the SERVER_MESSAGE_DOMAIN
        EventReceiver.getInstance().addListener(ScenarioStateEvent.class, new RemoteEventListener() {
            public void apply(Event anEvent) {
                if (anEvent instanceof ScenarioStateEvent) {
                    ScenarioStateEvent scenarioStateEvent = (ScenarioStateEvent) anEvent;
                    itemPanelByIdMap.get(scenarioStateEvent.getScenarioId()).updateScenarioRunState(scenarioStateEvent.getState());
                }
            }
        });

        InputAddOn inputCreate = new InputAddOn();
        final TextBox txtName = new TextBox();
        inputCreate.add(txtName);
        Button btnNew = new Button("new");
        btnNew.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                ServiceUtils.getScenarioEditorService().createScenario(txtName.getText(), new AsyncCallback<Void>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        Log.severe("","",caught);     //TODO
                    }

                    @Override
                    public void onSuccess(Void result) {
                        txtName.setText("");
                        loadScenarios();
                    }
                });
            }
        });
        inputCreate.add(btnNew);


        //setWidth("200px");

        add(inputCreate);

        add(new ScrollPanel(scenarioItemsContainerPanel));
        loadScenarios();
    }

    private void loadScenarios() {
        scenarioItemsContainerPanel.clear();
        ServiceUtils.getScenarioEditorService().getScenarios(new AsyncCallback<List<Scenario>>() {
            @Override
            public void onFailure(Throwable caught) {
            }

            @Override
            public void onSuccess(List<Scenario> result) {
                for (Scenario scenario : result) {
                    ScenarioItemPanel scenarioItemPanel = new ScenarioItemPanel(scenario);
                    itemPanelByIdMap.put(scenario.getId(), scenarioItemPanel);
                    scenarioItemsContainerPanel.add(scenarioItemPanel);
                }
            }
        });
    }
}
