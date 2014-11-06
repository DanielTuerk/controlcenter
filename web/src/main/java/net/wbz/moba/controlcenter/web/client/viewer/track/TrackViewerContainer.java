package net.wbz.moba.controlcenter.web.client.viewer.track;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import net.wbz.moba.controlcenter.web.client.viewer.controls.ViewerControlsPanel;

/**
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
public class TrackViewerContainer extends DockLayoutPanel {

    public TrackViewerContainer() {
        super(Style.Unit.PX);

        setHeight("100%");

        addEast(new ViewerControlsPanel(), 400);
        add(new TrackViewerPanel());
    }

    @Override
    protected void onLoad() {
        super.onLoad();
    }
}
