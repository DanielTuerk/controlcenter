package net.wbz.moba.controlcenter.web.client.viewer.controls;

import org.gwtbootstrap3.client.ui.NavTabs;
import org.gwtbootstrap3.client.ui.TabContent;
import org.gwtbootstrap3.client.ui.TabListItem;
import org.gwtbootstrap3.client.ui.TabPane;
import org.gwtbootstrap3.client.ui.TabPanel;
import org.gwtbootstrap3.client.ui.constants.TabPosition;

import com.google.gwt.dom.client.Style;

import net.wbz.moba.controlcenter.web.client.viewer.controls.train.TrainViewerPanel;

/**
 * @author Daniel Tuerk
 */
public class ViewerControlsPanel extends TabPanel {

    private static final String TAB_ID_TRAIN = "train";
    private static final String TAB_ID_SCENARIO = "scenario";
    private final TabContent tabContent;
    private final NavTabs navTabs;

    public ViewerControlsPanel() {
        super();
        setTabPosition(TabPosition.TOP);

        navTabs = new NavTabs();

        TabListItem navTrainTab = new TabListItem("TrainEntity");
        navTrainTab.setDataTarget("#" + TAB_ID_TRAIN);
        navTabs.add(navTrainTab);

        TabListItem nacScenarioTab = new TabListItem("Scenario");
        nacScenarioTab.setDataTarget("#" + TAB_ID_SCENARIO);
        navTabs.add(nacScenarioTab);

        add(navTabs);

        tabContent = new TabContent();

        TabPane trainTab = new TabPane();
        trainTab.addStyleName("viewerControls_tabContent");
        trainTab.setId(TAB_ID_TRAIN);
        trainTab.add(new TrainViewerPanel());
        trainTab.setActive(true);
        tabContent.add(trainTab);

        TabPane scenarioTab = new TabPane();
        scenarioTab.setId(TAB_ID_SCENARIO);
        tabContent.add(scenarioTab);

        add(tabContent);
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        tabContent.setHeight("100%");
        tabContent.getElement().getStyle().setOverflowY(Style.Overflow.SCROLL);
        tabContent.getElement().getStyle().setOverflowX(Style.Overflow.HIDDEN);
    }
}
