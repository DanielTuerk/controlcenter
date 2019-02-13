package net.wbz.moba.controlcenter.web.client.viewer.track.parallax;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;
import java.util.Collection;
import net.wbz.moba.controlcenter.web.client.request.RequestUtils;
import net.wbz.moba.controlcenter.web.client.viewer.track.AbstractTrackViewerPanel;
import net.wbz.moba.controlcenter.web.shared.track.model.AbstractTrackPart;
import thothbot.parallax.core.client.RenderingPanel;

/**
 * Viewer panel for the 3D scene.
 *
 * @deprecated not used, was just a research
 * @author Daniel Tuerk
 */
@Deprecated
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
        RequestUtils.getInstance().getBusService().isBusConnected(new AsyncCallback<Boolean>() {
            @Override
            public void onFailure(Throwable caught) {

            }

            @Override
            public void onSuccess(Boolean response) {
                RequestUtils.getInstance().getTrackEditorService().loadTrack(
                        new AsyncCallback<Collection<AbstractTrackPart>>() {
                            @Override
                            public void onFailure(Throwable caught) {

                            }

                            @Override
                            public void onSuccess(Collection<AbstractTrackPart> trackParts) {
                                for (AbstractTrackPart trackPart : trackParts) {
                                    animatedScene.addTrackWidget(trackPart);
                                }
                                animatedScene.centerCamera();
                            }
                        });
            }
        });
    }

    @Override
    public void addTrackWidget(Widget widget, int left, int top) {
    }

    @Override
    protected void onUnload() {
        super.onUnload();
        remove(renderingPanel);
    }

}
