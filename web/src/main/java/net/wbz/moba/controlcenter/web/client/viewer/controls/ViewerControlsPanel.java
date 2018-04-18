package net.wbz.moba.controlcenter.web.client.viewer.controls;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import org.gwtbootstrap3.client.ui.NavTabs;
import org.gwtbootstrap3.client.ui.TabContent;
import org.gwtbootstrap3.client.ui.TabListItem;
import org.gwtbootstrap3.client.ui.TabPane;
import org.gwtbootstrap3.client.ui.constants.TabPosition;

import com.google.gwt.dom.client.Style;

import net.wbz.moba.controlcenter.web.client.viewer.controls.scenario.ScenarioViewerPanel;
import net.wbz.moba.controlcenter.web.client.viewer.controls.train.TrainViewerPanel;

/**
 * @author Daniel Tuerk
 */
public class ViewerControlsPanel extends Composite {
    private static Binder uiBinder = GWT.create(Binder.class);
    private static final String TAB_ID_TRAIN = "train";
    private static final String TAB_ID_SCENARIO = "scenario";

    @UiField
    TabPane trainTab;
    @UiField
    TabPane scenarioTab;

    public ViewerControlsPanel() {
        initWidget(uiBinder.createAndBindUi(this));

        trainTab.add(new TrainViewerPanel());
        // scenarioTab.add(new ScenarioViewerPanel());
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        // tabContent.setHeight("100%");
        // tabContent.getElement().getStyle().setOverflowY(Style.Overflow.SCROLL);
        // tabContent.getElement().getStyle().setOverflowX(Style.Overflow.HIDDEN);

        // navTrainTab.showTab();
    }

    interface Binder extends UiBinder<com.google.gwt.user.client.ui.Widget, ViewerControlsPanel> {
    }

}
