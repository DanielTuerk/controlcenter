package net.wbz.moba.controlcenter.web.client.viewer.controls.scenario;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Column;
import org.gwtbootstrap3.client.ui.Icon;
import org.gwtbootstrap3.client.ui.ListGroup;
import org.gwtbootstrap3.client.ui.ListGroupItem;
import org.gwtbootstrap3.client.ui.PanelCollapse;
import org.gwtbootstrap3.client.ui.Row;
import org.gwtbootstrap3.client.ui.constants.ColumnSize;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.constants.ListGroupItemType;

import com.google.common.base.Optional;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import net.wbz.moba.controlcenter.web.client.RequestUtils;
import net.wbz.moba.controlcenter.web.client.viewer.controls.AbstractItemPanel;
import net.wbz.moba.controlcenter.web.shared.scenario.RouteSequence;
import net.wbz.moba.controlcenter.web.shared.scenario.RouteStateEvent;
import net.wbz.moba.controlcenter.web.shared.scenario.Scenario;
import net.wbz.moba.controlcenter.web.shared.scenario.ScenarioStateEvent;

/**
 * @author Daniel Tuerk
 */
public class ScenarioItemPanel extends AbstractItemPanel<Scenario, ScenarioStateEvent> {

    private final Map<Long, ListGroupItem> routeSequenceListGroupItemMap = new HashMap<>();
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

    private Optional<RouteSequence> getRouteSequenceFromId(Long routeSequenceId) {
        for (RouteSequence routeSequence : getModel().getRouteSequences()) {
            if (Long.compare(routeSequence.getId(), routeSequenceId) == 0) {
                return Optional.of(routeSequence);
            }
        }
        return Optional.absent();
    }

    public void updateRouteState(RouteStateEvent event) {
        // reset for next state
        ListGroupItem listGroupItem = routeSequenceListGroupItemMap.get(event.getRouteSequenceId());
        Optional<RouteSequence> routeSequenceFromId = getRouteSequenceFromId(event.getRouteSequenceId());
        if (routeSequenceFromId.isPresent()) {
            listGroupItem.setText(routeSequenceFromId.get().getRoute().getName());
            // detail label text
            String text = event.getState().name()
                    + " (" + routeSequenceFromId.get().getPosition() + 1 + "/"
                    + getModel().getRouteSequences().size() + ")";
            getLblStateDetails().setText(text);
        }

        listGroupItem.setType(ListGroupItemType.DEFAULT);
        // remove icon
        for (int i = 0; i < listGroupItem.getWidgetCount(); i++) {
            if (listGroupItem.getWidget(i) instanceof Icon) {
                listGroupItem.remove(i);
                break;
            }
        }
        switch (event.getState()) {
            case WAITING:
                listGroupItem.setType(ListGroupItemType.WARNING);
                listGroupItem.add(new Icon(IconType.PAUSE_CIRCLE));
                break;
            case RUNNING:
                listGroupItem.setType(ListGroupItemType.INFO);
                listGroupItem.add(new Icon(IconType.PLAY_CIRCLE));
                break;
            case FINISHED:
                listGroupItem.setType(ListGroupItemType.SUCCESS);
                listGroupItem.add(new Icon(IconType.CHECK_CIRCLE));
                break;
            case FAILED:
                listGroupItem.setType(ListGroupItemType.DANGER);
                listGroupItem.add(new Icon(IconType.WARNING));
                listGroupItem.setText(listGroupItem.getText() + " " + event.getMessage());
                break;
        }

    }

    @Override
    public void updateItemData(ScenarioStateEvent event) {
        switch (event.getState()) {
            case RUNNING:
                btnSchedule.setEnabled(false);
                btnStart.setEnabled(false);
                btnStop.setEnabled(true);

                // reset route states for new execution
                // TODO maybe too late? because first route starts faster
                for (ListGroupItem widgets : routeSequenceListGroupItemMap.values()) {
                    widgets.setType(ListGroupItemType.DEFAULT);
                }

                break;
            case IDLE:
                btnSchedule.setEnabled(true);
                btnStart.setEnabled(true);
                btnStop.setEnabled(false);
                break;
            case PAUSED:
                btnSchedule.setEnabled(false);
                btnStart.setEnabled(false);
                btnStop.setEnabled(true);
                // TODO resume
                break;
            case STOPPED:
            case FINISHED:
                btnSchedule.setEnabled(true);
                btnStart.setEnabled(true);
                btnStop.setEnabled(false);
                break;
        }

        getLblState().setText(event.getState().name());
    }

    @Override
    public PanelCollapse createCollapseContentPanel() {
        PanelCollapse contentPanel = new PanelCollapse();
        contentPanel.add(createFunctions());
        contentPanel.add(createRouteProgress());
        return contentPanel;
    }

    private Row createFunctions() {
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
        return rowDrivingFunctions;
    }

    private ListGroup createRouteProgress() {
        ListGroup listGroup = new ListGroup();
        Scenario scenario = getModel();
        List<RouteSequence> routeSequences = scenario.getRouteSequences();
        Collections.sort(routeSequences, new Comparator<RouteSequence>() {
            @Override
            public int compare(RouteSequence o1, RouteSequence o2) {
                return Long.compare(o1.getPosition(), o2.getPosition());
            }
        });
        for (RouteSequence routeSequence : routeSequences) {
            ListGroupItem groupItem = new ListGroupItem();
            groupItem.setText(routeSequence.getRoute().getName());
            listGroup.add(groupItem);
            routeSequenceListGroupItemMap.put(routeSequence.getId(), groupItem);
        }
        return listGroup;
    }

}
