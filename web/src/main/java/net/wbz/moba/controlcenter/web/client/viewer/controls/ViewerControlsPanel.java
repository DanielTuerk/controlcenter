package net.wbz.moba.controlcenter.web.client.viewer.controls;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import net.wbz.moba.controlcenter.web.client.viewer.controls.scenario.ScenarioViewerPanel;
import net.wbz.moba.controlcenter.web.client.viewer.controls.train.TrainViewerPanel;
import org.gwtbootstrap3.client.ui.NavTabs;
import org.gwtbootstrap3.client.ui.PanelBody;
import org.gwtbootstrap3.client.ui.RadioButton;
import org.gwtbootstrap3.client.ui.TabContent;
import org.gwtbootstrap3.client.ui.TabListItem;
import org.gwtbootstrap3.client.ui.TabPane;
import org.gwtbootstrap3.extras.animate.client.ui.Animate;
import org.gwtbootstrap3.extras.animate.client.ui.constants.Animation;

/**
 * @author Daniel Tuerk
 */
public class ViewerControlsPanel extends Composite {
    private static Binder uiBinder = GWT.create(Binder.class);

//    @UiField
//    TabPane trainTab;
//    @UiField
//    TabPane scenarioTab;
//    @UiField
//    TabContent tabContent;
    //    @UiField
//    TabPanel tabPanel;
    @UiField
    RadioButton btnScenario;
    @UiField
    RadioButton btnTrain;
//    @UiField
//    TabListItem tabListItemScenario;
//    @UiField
//    TabListItem tabListItemTrain;
//    @UiField
//    NavTabs navTabs;
    @UiField
FlowPanel panelBody;
    public ViewerControlsPanel() {
        initWidget(uiBinder.createAndBindUi(this));

        TrainViewerPanel trainViewerPanel = new TrainViewerPanel();
//        trainViewerPanel.setVisible(false);

        ScenarioViewerPanel scenarioViewerPanel = new ScenarioViewerPanel();
//        scenarioViewerPanel.setVisible(false);
//        scenarioTab.add();
        btnTrain.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(final ValueChangeEvent<Boolean> event) {
//                tabListItemScenario.showTab(event.getValue());
                panelBody.clear();
                panelBody.add(trainViewerPanel);
//                Animate.animate(trainViewerPanel, Animation.FADE_IN, 1, 1000);
//                Animate.animate(scenarioViewerPanel, Animation.FADE_OUT, 1, 1000);
            }
        });
        btnScenario.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(final ValueChangeEvent<Boolean> event) {
//                tabListItemTrain.showTab(event.getValue());
//                Animate.animate(trainViewerPanel, Animation.FADE_OUT, 1, 1000);
                panelBody.clear();
                panelBody.add(scenarioViewerPanel);
//                Animate.animate(scenarioViewerPanel, Animation.FADE_IN, 1, 1000);
            }
        });
    }

    @Override
    protected void onLoad() {
        super.onLoad();
//        trainTab.setActive(true);
//        tabContent.(TabPos);
    }

    interface Binder extends UiBinder<com.google.gwt.user.client.ui.Widget, ViewerControlsPanel> {
    }

}
