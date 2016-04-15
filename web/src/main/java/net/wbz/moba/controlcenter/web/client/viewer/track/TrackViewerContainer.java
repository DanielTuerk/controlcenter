package net.wbz.moba.controlcenter.web.client.viewer.track;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import net.wbz.moba.controlcenter.web.client.Settings;
import net.wbz.moba.controlcenter.web.client.viewer.controls.ViewerControlsPanel;
import net.wbz.moba.controlcenter.web.client.viewer.track.parallax.TrackViewer3dPanel;
import net.wbz.moba.controlcenter.web.client.viewer.track.svg.TrackViewerPanel;

/**
 * Container of the track viewer to show the track and the controls.
 * Type of the track is configured by config key to switch between the viewer types.
 *
 * @author Daniel Tuerk
 */
public class TrackViewerContainer extends DockLayoutPanel {

    public TrackViewerContainer() {
        super(Style.Unit.PX);
        setHeight("100%");
    }

    @Override
    protected void onLoad() {
        super.onLoad();

        addEast(new ViewerControlsPanel(), 400);

//        Widget trackViewer;
//        if(Settings.getInstance().getUse3dViewer().getValue()) {
//            trackViewer=new TrackViewer3dPanel();
//        } else {
//            trackViewer = new TrackViewerPanel();
//        }
//        add(trackViewer);
    }

    @Override
    protected void onUnload() {
        super.onUnload();
        clear();
    }
}
