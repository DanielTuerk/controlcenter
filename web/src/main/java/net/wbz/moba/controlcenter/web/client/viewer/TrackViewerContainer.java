package net.wbz.moba.controlcenter.web.client.viewer;

import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.ScrollPanel;

/**
 * @author Daniel Tuerk (daniel.tuerk@jambit.com)
 */
public class TrackViewerContainer extends DockPanel {



    @Override
    protected void onLoad() {
        super.onLoad();

        add(new ScenarioViewerPanel(), DockPanel.EAST);
        add(new ScrollPanel(new TrackViewerPanel()),DockPanel.CENTER);
    }
}
