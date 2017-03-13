package net.wbz.moba.controlcenter.web.client.scenario;

import java.util.Collection;
import net.wbz.moba.controlcenter.web.client.Callbacks.OnlySuccessAsyncCallback;
import net.wbz.moba.controlcenter.web.client.RequestUtils;
import net.wbz.moba.controlcenter.web.shared.scenario.Route;
import net.wbz.moba.controlcenter.web.shared.train.Train;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.extras.select.client.ui.MultipleSelect;
import org.gwtbootstrap3.extras.select.client.ui.Option;
import org.gwtbootstrap3.extras.select.client.ui.Select;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

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
    Select selectTrain;
    @UiField
    MultipleSelect selectRoutes;

    public ScenarioEditModalBody(Scenario scenario) {
        this.scenario = scenario;
        initWidget(uiBinder.createAndBindUi(this));

        txtName.setText(scenario.getName());
        txtCron.setText(scenario.getCron());

        loadTrains();
        loadRoutes();
    }

    @Override
    protected void onLoad() {
        super.onLoad();

    }

    private void loadRoutes() {
        // RequestUtils.getInstance().getScenarioEditorService().getRouteSequences(new
        // OnlySuccessAsyncCallback<Collection<Train>>() {
        // @Override
        // public void onSuccess(Collection<Route> result) {
        // selectTrain.clear();
        // for (Route route : result) {
        // Option option = new Option();
        // option.setValue(String.valueOf(route.getId()));
        // option.setText(route.getPosition());
        // if(scenario.getTrain()!=null && scenario.getTrain()==train) {
        // option.setSelected(true);
        // }
        // selectTrain.add(option);
        // }
        // selectTrain.refresh();
        // }
        // });
    }

    private void loadTrains() {
        RequestUtils.getInstance().getTrainEditorService().getTrains(new OnlySuccessAsyncCallback<Collection<Train>>() {
            @Override
            public void onSuccess(Collection<Train> result) {
                selectTrain.clear();
                for (Train train : result) {
                    Option option = new Option();
                    option.setValue(String.valueOf(train.getId()));
                    option.setText(train.getName());
                    if (scenario.getTrain() != null && scenario.getTrain() == train) {
                        option.setSelected(true);
                    }
                    selectTrain.add(option);
                }
                selectTrain.refresh();
            }
        });
    }

    public Scenario getUpdatedModel() {
        scenario.setName(txtName.getText());
        scenario.setCron(txtCron.getText());

        return scenario;
    }

    interface Binder extends UiBinder<Widget, ScenarioEditModalBody> {
    }
}
