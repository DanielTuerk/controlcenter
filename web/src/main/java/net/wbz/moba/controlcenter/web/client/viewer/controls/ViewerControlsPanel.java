package net.wbz.moba.controlcenter.web.client.viewer.controls;

import com.github.gwtbootstrap.client.ui.TabPane;
import com.github.gwtbootstrap.client.ui.TabPanel;
import com.github.gwtbootstrap.client.ui.resources.Bootstrap;

/**
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
public class ViewerControlsPanel extends TabPanel {

    public static int WIDTH_PIXEL = 400;

    public ViewerControlsPanel() {
        super(Bootstrap.Tabs.ABOVE);
        setWidth(WIDTH_PIXEL+"px");

        TabPane trainTab = new TabPane();
        trainTab.setHeading("Train");
        trainTab.add(new TrainViewerPanel());
        add(trainTab);

        TabPane scenarioTab = new TabPane();
        scenarioTab.setHeading("Scenario");
        scenarioTab.add(new ScenarioViewerPanel());
        add(scenarioTab);

        selectTab(0);
    }
}
