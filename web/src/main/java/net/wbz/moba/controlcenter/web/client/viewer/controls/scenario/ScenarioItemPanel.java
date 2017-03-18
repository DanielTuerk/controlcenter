package net.wbz.moba.controlcenter.web.client.viewer.controls.scenario;

import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Column;
import org.gwtbootstrap3.client.ui.PanelCollapse;
import org.gwtbootstrap3.client.ui.Row;
import org.gwtbootstrap3.client.ui.constants.ColumnSize;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import net.wbz.moba.controlcenter.web.client.RequestUtils;
import net.wbz.moba.controlcenter.web.client.viewer.controls.AbstractItemPanel;
import net.wbz.moba.controlcenter.web.shared.scenario.Scenario;
import net.wbz.moba.controlcenter.web.shared.scenario.ScenarioStateEvent;

/**
 * @author Daniel Tuerk
 */
public class ScenarioItemPanel extends AbstractItemPanel<Scenario, ScenarioStateEvent> {

    private Button btnSchedule;
    private Button btnStart;
    private Button btnStop;

    public ScenarioItemPanel(Scenario scenario) {
        super(scenario, scenario.getName());
        assert getModel() != null;
    }

    @Override
    protected void deviceConnectionChanged(boolean connected) {
        btnSchedule.setEnabled(connected);
        btnStart.setEnabled(connected);
        btnStop.setEnabled(connected);

        getLblState().setText(getModel().getRunState().name());
    }

    @Override
    public void updateItemData(ScenarioStateEvent event) {
        switch (event.getState()) {
            case RUNNING:
                btnSchedule.setEnabled(false);
                btnStart.setEnabled(false);
                btnStop.setEnabled(true);
                break;
            case IDLE:
                btnSchedule.setEnabled(true);
                btnStart.setEnabled(true);
                btnStop.setEnabled(false);
                break;
            case PAUSED:
                btnSchedule.setEnabled(true);
                btnStart.setEnabled(true);
                btnStop.setEnabled(true);
                break;
        }

        getLblState().setText(event.getState().name());
    }

    @Override
    public PanelCollapse createCollapseContentPanel() {
        PanelCollapse contentPanel = new PanelCollapse();
        Row rowDrivingFunctions = new Row();
        btnSchedule = new Button("Schedule", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                RequestUtils.getInstance().getScenarioService().schedule(
                        getModel().getId(), RequestUtils.VOID_ASYNC_CALLBACK);
            }
        });
        btnStart = new Button("Start", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                RequestUtils.getInstance().getScenarioService().start(
                        getModel().getId(), RequestUtils.VOID_ASYNC_CALLBACK);
            }
        });
        btnStop = new Button("Stop", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                RequestUtils.getInstance().getScenarioService().stop(
                        getModel().getId(), RequestUtils.VOID_ASYNC_CALLBACK);
            }
        });
        rowDrivingFunctions.add(new Column(ColumnSize.MD_12, btnSchedule, btnStart, btnStop));
        contentPanel.add(rowDrivingFunctions);
        return contentPanel;
    }

}
