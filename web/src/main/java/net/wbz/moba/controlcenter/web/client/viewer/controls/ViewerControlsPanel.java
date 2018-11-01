package net.wbz.moba.controlcenter.web.client.viewer.controls;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ScrollPanel;
import org.gwtbootstrap3.client.ui.Container;
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

    @UiField
    TabPane trainTab;
    @UiField
    TabPane scenarioTab;
    @UiField
    TabContent tabContent;

    public ViewerControlsPanel() {
        initWidget(uiBinder.createAndBindUi(this));
        trainTab.add(new TrainViewerPanel());

        scenarioTab.add(new ScenarioViewerPanel());

//        trainTab.addStyleName("scrollContent");
    }

    interface Binder extends UiBinder<com.google.gwt.user.client.ui.Widget, ViewerControlsPanel> {
    }

}
