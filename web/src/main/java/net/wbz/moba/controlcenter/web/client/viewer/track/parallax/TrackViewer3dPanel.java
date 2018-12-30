package net.wbz.moba.controlcenter.web.client.viewer.track.parallax;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;

import net.wbz.moba.controlcenter.web.client.request.RequestUtils;
import net.wbz.moba.controlcenter.web.client.viewer.track.AbstractTrackViewerPanel;
import net.wbz.moba.controlcenter.web.client.viewer.track.parallax.trackparts.Basic3dTrackWidget;
import net.wbz.moba.controlcenter.web.shared.track.model.AbstractTrackPart;
import net.wbz.moba.controlcenter.web.shared.track.model.BusDataConfiguration;
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

    private Map<BusDataConfiguration, List<Basic3dTrackWidget>> trackWidgetsOfConfiguration = Maps.newConcurrentMap();

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
                                trackWidgetsOfConfiguration.clear();

                                for (AbstractTrackPart trackPart : trackParts) {
                                    Basic3dTrackWidget trackWidget = animatedScene.addTrackWidget(trackPart);

                                    for (BusDataConfiguration configuration : trackPart
                                            .getConfigurationsOfFunctions()) {

                                        // ignore default configs of track widget to register event handler
                                        if (configuration.isValid()) {
                                            if (!trackWidgetsOfConfiguration.containsKey(configuration)) {
                                                trackWidgetsOfConfiguration.put(configuration,
                                                    new ArrayList<>());
                                            }
                                            // avoid same widget for equal bit state configuration
                                            if (!trackWidgetsOfConfiguration.get(configuration).contains(trackWidget)) {
                                                trackWidgetsOfConfiguration.get(configuration).add(trackWidget);
                                            }
                                        }
                                    }

                                }
                                animatedScene.centerCamera();
                            }
                        });
            }
        });
    }

    @Override
    protected void updateTrackPartState(BusDataConfiguration configuration, boolean state) {
        if (trackWidgetsOfConfiguration.containsKey(configuration)) {
            for (Basic3dTrackWidget controlSvgTrackWidget : trackWidgetsOfConfiguration.get(configuration)) {
                controlSvgTrackWidget.updateFunctionState(configuration, state);
            }
        }
    }

    @Override
    protected void updateTrackPartBlockState(BusDataConfiguration configuration, boolean state) {
        // TODO
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
