package net.wbz.moba.controlcenter.web.client.viewer.track.parallax;

import com.google.common.collect.Maps;
import com.google.gwt.gen2.logging.shared.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;
import net.wbz.moba.controlcenter.web.client.ServiceUtils;
import net.wbz.moba.controlcenter.web.client.editor.track.ViewerPaletteWidget;
import net.wbz.moba.controlcenter.web.client.model.track.AbsoluteTrackPosition;
import net.wbz.moba.controlcenter.web.client.model.track.AbstractSvgTrackWidget;
import net.wbz.moba.controlcenter.web.client.model.track.ModelManager;
import net.wbz.moba.controlcenter.web.client.util.EmptyCallback;
import net.wbz.moba.controlcenter.web.client.viewer.track.AbstractTrackViewerPanel;
import net.wbz.moba.controlcenter.web.client.viewer.track.parallax.trackparts.Basic3dTrackWidget;
import net.wbz.moba.controlcenter.web.shared.track.model.Configuration;
import net.wbz.moba.controlcenter.web.shared.track.model.TrackPart;
import thothbot.parallax.core.client.RenderingPanel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Viewer panel for the 3D scene.
 *
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
public class TrackViewer3dPanel extends AbstractTrackViewerPanel {

    private TrackViewerScene animatedScene;
    private RenderingPanel renderingPanel;

    private Map<Configuration, List<Basic3dTrackWidget>> trackWidgetsOfConfiguration = Maps.newConcurrentMap();


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
                        trackWidgetsOfConfiguration.clear();

                        for (TrackPart trackPart : trackParts) {
                            Basic3dTrackWidget trackWidget = animatedScene.addTrackWidget(trackPart);


                            for (Configuration configuration : trackPart.getConfigurationsOfFunctions()) {

                                // ignore default configs of track widget to register event handler
                                if (configuration.isValid()) {
                                    if (!trackWidgetsOfConfiguration.containsKey(configuration)) {
                                        trackWidgetsOfConfiguration.put(configuration, new ArrayList<Basic3dTrackWidget>());
                                    }
                                    // avoid same widget for equal bit state configuration
                                    if (!trackWidgetsOfConfiguration.get(configuration).contains(trackWidget)) {
                                        trackWidgetsOfConfiguration.get(configuration).add(trackWidget);
                                    }
                                }
                            }

                        }
                        ServiceUtils.getTrackEditorService().registerConsumersByConnectedDeviceForTrackParts(trackParts,
                                new EmptyCallback<Void>());

                        animatedScene.centerCamera();
                    }
                });
            }
        });
    }


    @Override
    protected void updateTrackPartState(Configuration configuration, boolean state) {
        if (trackWidgetsOfConfiguration.containsKey(configuration)) {
            for (Basic3dTrackWidget controlSvgTrackWidget : trackWidgetsOfConfiguration.get(configuration)) {
                controlSvgTrackWidget.updateFunctionState(configuration, state);
            }
        }
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
