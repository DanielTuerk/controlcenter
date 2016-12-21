package net.wbz.moba.controlcenter.web.client.viewer.track.parallax;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.wbz.moba.controlcenter.web.client.RequestUtils;
import net.wbz.moba.controlcenter.web.client.viewer.track.AbstractTrackViewerPanel;
import net.wbz.moba.controlcenter.web.client.viewer.track.parallax.trackparts.Basic3dTrackWidget;
import net.wbz.moba.controlcenter.web.server.persist.construction.track.TrackPartConfigurationEntity;
import net.wbz.moba.controlcenter.web.shared.track.model.ConfigurationProxy;
import net.wbz.moba.controlcenter.web.server.persist.construction.track.TrackPartProxy;
import thothbot.parallax.core.client.RenderingPanel;

import com.google.common.collect.Maps;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.requestfactory.shared.Receiver;

/**
 * Viewer panel for the 3D scene.
 *
 * @author Daniel Tuerk
 */
public class TrackViewer3dPanel extends AbstractTrackViewerPanel {

    private TrackViewerScene animatedScene;
    private RenderingPanel renderingPanel;

    private Map<ConfigurationProxy, List<Basic3dTrackWidget>> trackWidgetsOfConfiguration = Maps.newConcurrentMap();

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
        RequestUtils.getInstance().getBusRequest().isBusConnected().fire(new Receiver<Boolean>() {
            @Override
            public void onSuccess(Boolean response) {
                RequestUtils.getInstance().getTrackEditorRequest().loadTrack().fire(
                        new Receiver<List<TrackPartProxy>>() {
                    @Override
                    public void onSuccess(List<TrackPartProxy> trackParts) {
                        trackWidgetsOfConfiguration.clear();

                        for (TrackPartProxy trackPart : trackParts) {
                            Basic3dTrackWidget trackWidget = animatedScene.addTrackWidget(trackPart);

                            for (ConfigurationProxy configuration : trackPart.getConfigurationsOfFunctions()) {

                                // ignore default configs of track widget to register event handler
                                if (configuration.isValid()) {
                                    if (!trackWidgetsOfConfiguration.containsKey(configuration)) {
                                        trackWidgetsOfConfiguration.put(configuration,
                                                new ArrayList<Basic3dTrackWidget>());
                                    }
                                    // avoid same widget for equal bit state configuration
                                    if (!trackWidgetsOfConfiguration.get(configuration).contains(trackWidget)) {
                                        trackWidgetsOfConfiguration.get(configuration).add(trackWidget);
                                    }
                                }
                            }

                        }
                        RequestUtils.getInstance().getTrackEditorRequest()
                                .registerConsumersByConnectedDeviceForTrackParts(trackParts).fire();

                        animatedScene.centerCamera();
                    }
                });
            }
        });
    }

    @Override
    protected void updateTrackPartState(TrackPartConfigurationEntity configuration, boolean state) {
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
