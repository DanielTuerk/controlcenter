package net.wbz.moba.controlcenter.web.client.scenario;

import com.google.common.base.Strings;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import java.util.Comparator;
import java.util.List;
import net.wbz.moba.controlcenter.web.client.components.AbstractEditModal;
import net.wbz.moba.controlcenter.web.client.components.RouteSelect;
import net.wbz.moba.controlcenter.web.client.components.TrainDrivingDirectionSelect;
import net.wbz.moba.controlcenter.web.client.components.TrainSelect;
import net.wbz.moba.controlcenter.web.shared.scenario.RouteSequence;
import net.wbz.moba.controlcenter.web.shared.scenario.Scenario;
import org.gwtbootstrap3.client.ui.TextBox;

/**
 * @author Daniel Tuerk
 */
public class ScenarioEditModalBody extends Composite {

    private static Binder uiBinder = GWT.create(Binder.class);

    private final Scenario scenario;

    @UiField
    TextBox txtName;
    @UiField
    TextBox txtCron;
    @UiField
    TextBox txtStartDrivingLevel;
    @UiField
    TrainSelect selectTrain;
    @UiField
    TrainDrivingDirectionSelect selectTrainDrivingDirection;
    @UiField
    Panel routesPanel;

    ScenarioEditModalBody(Scenario scenario) {
        this.scenario = scenario;
        initWidget(uiBinder.createAndBindUi(this));

        selectTrain.setNullableOption(true);
    }

    @Override
    protected void onLoad() {
        super.onLoad();

        txtName.setText(scenario.getName());
        txtCron.setText(scenario.getCron());

        selectTrain.setSelectedItem(scenario.getTrain());
        selectTrainDrivingDirection.setSelectedItem(scenario.getTrainDrivingDirection());

        Integer startDrivingLevel = scenario.getStartDrivingLevel();
        if (startDrivingLevel != null) {
            txtStartDrivingLevel.setText(String.valueOf(startDrivingLevel));
        } else {
            txtStartDrivingLevel.setText(null);
        }

        initRouteSequences();
    }

    private void initRouteSequences() {
        routesPanel.clear();

        List<RouteSequence> routeSequences = scenario.getRouteSequences();
        routeSequences.sort(Comparator.comparingInt(RouteSequence::getPosition));

        for (RouteSequence routeSequence : routeSequences) {
            routesPanel.add(new ScenarioRoutePanel(scenario, routeSequence) {
                @Override
                protected void positionChanged() {
                    initRouteSequences();
                }

                @Override
                protected void deleted() {
                    scenario.getRouteSequences().remove(routeSequence);
                    initRouteSequences();
                }
            });
        }
    }

    @UiHandler("btnAddRouteSequence")
    void onClickAddRouteSequence(ClickEvent ignored) {
        new AbstractEditModal<RouteSequence>("Select Route", "Ok", "Cancel", null) {

            private RouteSelect routeSelect;

            @Override
            protected IsWidget createContent(RouteSequence model) {
                routeSelect = new RouteSelect();
                return routeSelect;
            }

            @Override
            protected void onCancel() {
            }

            @Override
            protected void onConfirm(RouteSequence model) {
                RouteSequence routeSequence = new RouteSequence();
                routeSequence.setPosition(scenario.getRouteSequences().size());
                routeSequence.setRoute(routeSelect.getSelected().orElse(null));
                scenario.getRouteSequences().add(routeSequence);

                hide();

                initRouteSequences();
            }
        }.show();
    }

    Scenario getUpdatedModel() {
        scenario.setName(txtName.getText());
        scenario.setCron(txtCron.getText());
        scenario.setTrain(selectTrain.getSelected().orElse(null));
        scenario.setTrainDrivingDirection(selectTrainDrivingDirection.getSelected().orElse(null));
        String txtStartDrivingLevelText = txtStartDrivingLevel.getText();
        if (Strings.isNullOrEmpty(txtStartDrivingLevelText)) {
            scenario.setStartDrivingLevel(null);
        } else {
            scenario.setStartDrivingLevel(Integer.parseInt(txtStartDrivingLevelText));
        }
        return scenario;
    }

    interface Binder extends UiBinder<Widget, ScenarioEditModalBody> {

    }
}
