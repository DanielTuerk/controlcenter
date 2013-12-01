package net.wbz.moba.controlcenter.web.client.viewer;

import com.google.gwt.user.client.ui.DockPanel;

/**
 * @author Daniel Tuerk (daniel.tuerk@jambit.com)
 */
public class ViewerPanel extends DockPanel {

    private final TrackViewerPanel trackViewerPanel = new TrackViewerPanel();

    @Override
    protected void onLoad() {
        add(trackViewerPanel);
    }
}
