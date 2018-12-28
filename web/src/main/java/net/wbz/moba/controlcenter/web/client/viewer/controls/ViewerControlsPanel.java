package net.wbz.moba.controlcenter.web.client.viewer.controls;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import net.wbz.moba.controlcenter.web.client.viewer.controls.scenario.ScenarioViewerControlsPanel;
import net.wbz.moba.controlcenter.web.client.viewer.controls.train.TrainViewerControlsPanel;
import org.gwtbootstrap3.client.ui.RadioButton;

/**
 * Controls panel at the track viewer to switch between trains and scenarios.
 *
 * @author Daniel Tuerk
 */
public class ViewerControlsPanel extends Composite {

    private static Binder uiBinder = GWT.create(Binder.class);
    private final TrainViewerControlsPanel trainViewerControlsPanel;
    private final Widget scenarioViewerPanel;
    @UiField
    RadioButton btnScenario;
    @UiField
    RadioButton btnTrain;
    @UiField
    FlowPanel panelBody;

    public ViewerControlsPanel() {
        initWidget(uiBinder.createAndBindUi(this));

        trainViewerControlsPanel = new TrainViewerControlsPanel();
        scenarioViewerPanel = new ScenarioViewerControlsPanel();

        btnTrain.addValueChangeHandler(event -> showWidget(trainViewerControlsPanel));
        btnScenario.addValueChangeHandler(event -> showWidget(scenarioViewerPanel));
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        showWidget(trainViewerControlsPanel);
    }

    private void showWidget(Widget widget) {
        panelBody.clear();
        panelBody.add(widget);
    }

    interface Binder extends UiBinder<com.google.gwt.user.client.ui.Widget, ViewerControlsPanel> {

    }

}
