package net.wbz.moba.controlcenter.web.client.viewer;

import com.github.gwtbootstrap.client.ui.Tab;
import com.github.gwtbootstrap.client.ui.TabPane;
import com.github.gwtbootstrap.client.ui.TabPanel;
import com.github.gwtbootstrap.client.ui.resources.Bootstrap;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;

/**
 * @author Daniel Tuerk (daniel.tuerk@jambit.com)
 */
public class TrackViewerContainer extends DockPanel {

    public TrackViewerContainer() {
        TabPanel tabPanel = new TabPanel(Bootstrap.Tabs.ABOVE);

        TabPane trainTab = new TabPane();
        trainTab.setHeading("Train");
        trainTab.add(new TrainViewerPanel());
        tabPanel.add(trainTab);

        TabPane scenarioTab = new TabPane();
        scenarioTab.setHeading("Scenario");
        scenarioTab.add(new ScenarioViewerPanel());
        tabPanel.add(scenarioTab);

        tabPanel.selectTab(0);
        add(tabPanel, DockPanel.EAST);

        ScrollPanel trackViewerScrollPanel = new ScrollPanel(new TrackViewerPanel());
//        trackViewerScrollPanel.setWidth(String.valueOf((int) (Window.getClientWidth() * 0.8))+"px");
        add(trackViewerScrollPanel, DockPanel.CENTER);

    }

    @Override
    protected void onLoad() {
        super.onLoad();
    }
}
