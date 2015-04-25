package net.wbz.moba.controlcenter.web.client.viewer.track.parallax;

import com.google.gwt.gen2.logging.shared.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;
import net.wbz.moba.controlcenter.web.client.ServiceUtils;
import net.wbz.moba.controlcenter.web.client.editor.track.ViewerPaletteWidget;
import net.wbz.moba.controlcenter.web.client.model.track.AbsoluteTrackPosition;
import net.wbz.moba.controlcenter.web.client.model.track.AbstractSvgTrackWidget;
import net.wbz.moba.controlcenter.web.client.model.track.ModelManager;
import net.wbz.moba.controlcenter.web.client.viewer.track.AbstractTrackViewerPanel;
import net.wbz.moba.controlcenter.web.shared.track.model.TrackPart;
import thothbot.parallax.core.client.RenderingPanel;

/**
 * Viewer panel for the 3D scene.
 *
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
public class TrackViewer3dPanel extends AbstractTrackViewerPanel {

    private TrackViewerScene animatedScene;
    private RenderingPanel renderingPanel;

    public TrackViewer3dPanel() {
        renderingPanel = new RenderingPanel();
        // Background color
        renderingPanel.setBackground(0x111111);

        animatedScene = new TrackViewerScene();
        renderingPanel.setAnimatedScene(animatedScene);
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        add(renderingPanel);

        // load the connection state to toggle the state of the widgets
        ServiceUtils.getBusService().isBusConnected(new AsyncCallback<Boolean>() {
            @Override
            public void onFailure(Throwable caught) {
            }

            @Override
            public void onSuccess(final Boolean result) {
                ServiceUtils.getTrackEditorService().loadTrack(new AsyncCallback<TrackPart[]>() {
                    @Override
                    public void onFailure(Throwable throwable) {
                        Log.severe(throwable.getLocalizedMessage());
                    }

                    @Override
                    public void onSuccess(TrackPart[] trackParts) {
                        for (TrackPart trackPart : trackParts) {
                            AbstractSvgTrackWidget trackWidget = ModelManager.getInstance().getWidgetOf(trackPart);
                            trackWidget.setEnabled(result);
                            animatedScene.addTrackWidget(trackWidget.getTrackPart());
                        }
                        animatedScene.centerCamera();
                    }
                });
            }
        });
    }

    @Override
    public void addTrackWidget(Widget widget, int left, int top) {
        // TODO refactor
    }

    @Override
    protected void onUnload() {
        super.onUnload();
        remove(renderingPanel);
    }

}
