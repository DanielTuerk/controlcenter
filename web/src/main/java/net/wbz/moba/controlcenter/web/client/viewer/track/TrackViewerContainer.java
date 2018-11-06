package net.wbz.moba.controlcenter.web.client.viewer.track;


import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
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
public class TrackViewerContainer extends Composite {

    private static Binder uiBinder = GWT.create(Binder.class);

    @UiField
    HTMLPanel container;

     @UiField
    HTMLPanel trackContainer;

     @UiField
    HTMLPanel controlsContainer;

    public TrackViewerContainer() {
        initWidget(uiBinder.createAndBindUi(this));
        controlsContainer.add(new ViewerControlsPanel());
    }

    @Override
    protected void onLoad() {
        super.onLoad();

        Widget trackViewer;
        if (Settings.getInstance().getUse3dViewer().getValue()) {
            trackViewer = new TrackViewer3dPanel();
        } else {
            trackViewer = new TrackViewerPanel();
        }
        trackContainer.add(trackViewer);
    }

    @Override
    protected void onUnload() {
        super.onUnload();
        trackContainer.clear();
//         TODO needed?
//        controlsContainer.clear();
    }

    interface Binder extends UiBinder<Widget, TrackViewerContainer> {

    }
}
