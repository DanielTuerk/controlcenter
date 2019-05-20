package net.wbz.moba.controlcenter.web.client.viewer.controls.scenario;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Widget;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.wbz.moba.controlcenter.web.client.event.EventReceiver;
import net.wbz.moba.controlcenter.web.client.event.scenario.ScenarioStateRemoteListener;
import net.wbz.moba.controlcenter.web.client.request.RequestUtils;
import net.wbz.moba.controlcenter.web.client.viewer.controls.AbstractViewerItemControlsComposite;
import net.wbz.moba.controlcenter.web.shared.scenario.RouteSequence;
import net.wbz.moba.controlcenter.web.shared.scenario.RouteStateEvent;
import net.wbz.moba.controlcenter.web.shared.scenario.Scenario;
import net.wbz.moba.controlcenter.web.shared.scenario.ScenarioStateEvent;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Icon;
import org.gwtbootstrap3.client.ui.ListGroup;
import org.gwtbootstrap3.client.ui.ListGroupItem;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.constants.ListGroupItemType;

/**
 * @author Daniel Tuerk
 */
public class ScenarioItemControlsPanel extends AbstractViewerItemControlsComposite<Scenario> {

    private static Binder uiBinder = GWT.create(Binder.class);
    private final Map<Long, ListGroupItem> routeSequenceListGroupItemMap = new HashMap<>();
    private final ScenarioStateRemoteListener scenarioStateRemoteListener;
    @UiField
    Button btnSchedule;
    @UiField
    Button btnStart;
    @UiField
    Button btnStop;
    @UiField
    ListGroup groupRoutes;

    ScenarioItemControlsPanel(Scenario scenario) {
        super(scenario);
        initWidget(uiBinder.createAndBindUi(this));

        scenarioStateRemoteListener = this::updateScenarioState;

        createRouteProgress();
    }

    @UiHandler("btnSchedule")
    void btnScheduleClicked(ClickEvent event) {
        RequestUtils.getInstance().getScenarioService()
            .schedule(getModel().getId(), RequestUtils.VOID_ASYNC_CALLBACK);
    }

    @UiHandler("btnStart")
    void btnStartClicked(ClickEvent event) {
        routeSequenceListGroupItemMap.forEach(
            (key, value) -> getRouteSequenceFromId(key).ifPresent(x -> resetListItem(value, x))
        );

        RequestUtils.getInstance().getScenarioService()
            .start(getModel().getId(), RequestUtils.VOID_ASYNC_CALLBACK);
    }

    @UiHandler("btnStop")
    void btnStopClicked(ClickEvent event) {
        RequestUtils.getInstance().getScenarioService()
            .stop(getModel().getId(), RequestUtils.VOID_ASYNC_CALLBACK);
    }

    private void createRouteProgress() {
        Scenario scenario = getModel();
        List<RouteSequence> routeSequences = scenario.getRouteSequences();
        routeSequences.sort(Comparator.comparingLong(RouteSequence::getPosition));

        for (RouteSequence routeSequence : routeSequences) {
            ListGroupItem groupItem = new ListGroupItem();
            groupItem.setText(routeSequence.getRoute().getName());
            groupRoutes.add(groupItem);
            routeSequenceListGroupItemMap.put(routeSequence.getId(), groupItem);
        }
    }

    @Override
    protected void deviceConnectionChanged(boolean connected) {
        btnSchedule.setEnabled(connected);
        btnStart.setEnabled(connected);
        btnStop.setEnabled(connected);
        /*
         * Only react to run state changed for a connected device to avoid override button state for last send state
         * event.
         */
        if (connected) {
            EventReceiver.getInstance().addListener(scenarioStateRemoteListener);
        } else {
            EventReceiver.getInstance().removeListener(scenarioStateRemoteListener);
        }
    }

    Optional<RouteSequence> getRouteSequenceFromId(Long routeSequenceId) {
        for (RouteSequence routeSequence : getModel().getRouteSequences()) {
            if (routeSequence.getId().equals(routeSequenceId)) {
                return Optional.of(routeSequence);
            }
        }
        return Optional.empty();
    }

    void updateRouteState(RouteStateEvent event) {
        // reset for next state
        ListGroupItem listGroupItem = routeSequenceListGroupItemMap.get(event.getRouteSequenceId());
        Optional<RouteSequence> routeSequenceFromId = getRouteSequenceFromId(event.getRouteSequenceId());
        if (routeSequenceFromId.isPresent()) {
            resetListItem(listGroupItem, routeSequenceFromId.get());
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
    }

    private void resetListItem(ListGroupItem listGroupItem, RouteSequence routeSequenceFromId) {
        listGroupItem.setText(routeSequenceFromId.getRoute().getName());
        // detail label text
        listGroupItem.setType(ListGroupItemType.DEFAULT);
        // remove icon
        for (int i = 0; i < listGroupItem.getWidgetCount(); i++) {
            if (listGroupItem.getWidget(i) instanceof Icon) {
                listGroupItem.remove(i);
                break;
            }
        }
    }

    private void updateScenarioState(ScenarioStateEvent event) {
        if (event.getItemId() == getModel().getId()) {
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
        }
    }

    interface Binder extends UiBinder<Widget, ScenarioItemControlsPanel> {

    }

}
