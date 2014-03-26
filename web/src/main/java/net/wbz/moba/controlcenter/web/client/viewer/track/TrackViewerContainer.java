package net.wbz.moba.controlcenter.web.client.viewer.track;

import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import net.wbz.moba.controlcenter.web.client.viewer.controls.ViewerControlsPanel;

/**
 * @author Daniel Tuerk (daniel.tuerk@jambit.com)
 */
public class TrackViewerContainer extends DockPanel {

    public TrackViewerContainer() {
        add(new ViewerControlsPanel(), DockPanel.EAST);

        ScrollPanel trackViewerScrollPanel = new ScrollPanel(new TrackViewerPanel());
        add(trackViewerScrollPanel, DockPanel.CENTER);
    }

    @Override
    protected void onLoad() {
        super.onLoad();
    }
}
