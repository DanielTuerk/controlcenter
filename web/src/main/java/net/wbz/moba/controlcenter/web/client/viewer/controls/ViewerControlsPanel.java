package net.wbz.moba.controlcenter.web.client.viewer.controls;

import com.github.gwtbootstrap.client.ui.TabPane;
import com.github.gwtbootstrap.client.ui.TabPanel;
import com.github.gwtbootstrap.client.ui.resources.Bootstrap;
import net.wbz.moba.controlcenter.web.client.viewer.controls.scenario.ScenarioViewerPanel;
import net.wbz.moba.controlcenter.web.client.viewer.controls.train.TrainViewerPanel;

/**
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
public class ViewerControlsPanel extends TabPanel {

    public ViewerControlsPanel() {
        super(Bootstrap.Tabs.ABOVE);

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
