package net.wbz.moba.controlcenter.web.client.scenario;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.gwtbootstrap3.client.ui.TextBox;

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

import net.wbz.moba.controlcenter.web.client.components.AbstractEditModal;
import net.wbz.moba.controlcenter.web.client.components.RouteSelect;
import net.wbz.moba.controlcenter.web.client.components.TrainDrivingDirectionSelect;
import net.wbz.moba.controlcenter.web.client.components.TrainSelect;
import net.wbz.moba.controlcenter.web.shared.scenario.RouteSequence;
import net.wbz.moba.controlcenter.web.shared.scenario.Scenario;

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

    public ScenarioEditModalBody(Scenario scenario) {
        this.scenario = scenario;
        initWidget(uiBinder.createAndBindUi(this));

        selectTrain.setSelectedItem(scenario.getTrain());
        selectTrainDrivingDirection.setSelectedItem(scenario.getTrainDrivingDirection());
    }

    @Override
    protected void onLoad() {
        super.onLoad();

        txtName.setText(scenario.getName());
        txtCron.setText(scenario.getCron());

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
        Collections.sort(routeSequences,
                new Comparator<RouteSequence>() {
                    @Override
                    public int compare(RouteSequence o1, RouteSequence o2) {
                        return Integer.compare(o1.getPosition(), o2.getPosition());
                    }
                });

        for (RouteSequence routeSequence : routeSequences) {
            routesPanel.add(new ScenarioRoutePanel(scenario, routeSequence) {
                @Override
                protected void positionChanged() {
                    initRouteSequences();
                }

                @Override
                protected void deleted() {
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
                routeSequence.setRoute(routeSelect.getSelected().orNull());
                scenario.getRouteSequences().add(routeSequence);

                hide();

                initRouteSequences();
            }
        }.show();
    }

    public Scenario getUpdatedModel() {
        scenario.setName(txtName.getText());
        scenario.setCron(txtCron.getText());
        scenario.setTrain(selectTrain.getSelected().orNull());
        scenario.setTrainDrivingDirection(selectTrainDrivingDirection.getSelected().orNull());
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
