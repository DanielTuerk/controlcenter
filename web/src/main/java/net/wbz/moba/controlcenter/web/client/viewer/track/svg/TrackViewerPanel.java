package net.wbz.moba.controlcenter.web.client.viewer.track.svg;

import com.google.common.collect.Lists;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.wbz.moba.controlcenter.web.client.editor.track.ViewerPaletteWidget;
import net.wbz.moba.controlcenter.web.client.model.track.AbsoluteTrackPosition;
import net.wbz.moba.controlcenter.web.client.model.track.AbstractSvgTrackWidget;
import net.wbz.moba.controlcenter.web.client.model.track.ModelManager;
import net.wbz.moba.controlcenter.web.client.model.track.block.AbstractBlockStraightWidget;
import net.wbz.moba.controlcenter.web.client.request.RequestUtils;
import net.wbz.moba.controlcenter.web.client.viewer.track.AbstractTrackViewerPanel;
import net.wbz.moba.controlcenter.web.server.persist.construction.track.AbstractTrackPartEntity;
import net.wbz.moba.controlcenter.web.shared.track.model.AbstractTrackPart;
import org.gwtbootstrap3.client.ui.Label;
import org.gwtbootstrap3.client.ui.constants.LabelType;

/**
 * Panel for the track viewer.
 * Loading the track and add all {@link AbstractTrackPartEntity}s to the panel. Register the event listener to receive
 * state changes of all added {@link AbstractTrackPartEntity}s.
 *
 * @author Daniel Tuerk
 */
public class TrackViewerPanel extends AbstractTrackViewerPanel {

    private Label lblTrackPartConfig = new Label();

    private List<AbstractSvgTrackWidget> trackWidgets = new ArrayList<>();

    private List<AbstractTrackPart> loadedTrackParts;

    public TrackViewerPanel() {
    }

    @Override
    protected void onLoad() {
        super.onLoad();

        for (int i = getWidgetCount() - 1; i >= 0; i--) {
            remove(i);
        }

        lblTrackPartConfig.setType(LabelType.INFO);
        add(lblTrackPartConfig, 0, 0);

        // load the connection state to toggle the state of the widgets
        RequestUtils.getInstance().getBusService().isBusConnected(new AsyncCallback<Boolean>() {
            @Override
            public void onFailure(Throwable caught) {

            }

            @Override
            public void onSuccess(final Boolean connected) {

                RequestUtils.getInstance().getTrackEditorService()
                    .loadTrack(new AsyncCallback<Collection<AbstractTrackPart>>() {
                        @Override
                        public void onFailure(Throwable caught) {

                        }

                        @Override
                        public void onSuccess(Collection<AbstractTrackPart> trackParts) {
                            loadedTrackParts = Lists.newArrayList(trackParts);

                            int maxTop = 0;
                            int maxLeft = 0;
                            int percentage = PERCENTAGE_START_TRACK;
                            for (int i = 0; i < trackParts.size(); i++) {
                                final AbstractTrackPart trackPart = loadedTrackParts.get(i);
                                if (i + 1 % 6 == 0) {
                                    percentage += 10;
                                    if (percentage > PERCENTAGE_MAX) {
                                        percentage = PERCENTAGE_MAX;
                                    }
                                }

                                final AbstractSvgTrackWidget trackWidget = ModelManager.getInstance()
                                    .getWidgetOf(trackPart);
                                trackWidget.setEnabled(connected);

                                trackWidgets.add(trackWidget);

                                AbsoluteTrackPosition trackPosition = trackWidget
                                    .getTrackPosition(trackPart.getGridPosition(), getZoomLevel());
                                if (maxTop < trackPosition.getTop()) {
                                    maxTop = trackPosition.getTop();
                                }
                                if (maxLeft < trackPosition.getLeft()) {
                                    maxLeft = trackPosition.getLeft();
                                }

                                ViewerPaletteWidget widget = new ViewerPaletteWidget(trackWidget);
                                widget.addMouseOverHandler(
                                    event -> lblTrackPartConfig.setText(trackWidget.getConfigurationInfo()));
                                addTrackWidget(widget, trackPosition.getLeft(), trackPosition.getTop());

                                if (trackWidget instanceof AbstractBlockStraightWidget) {
                                    ((AbstractBlockStraightWidget) trackWidget).unknownBlock();
                                }
                            }
                        }
                    });
            }
        });
    }

    @Override
    protected void resetTrackForRailVoltage(boolean railVoltageOn) {
        if (!railVoltageOn) {
            for (AbstractSvgTrackWidget trackWidget : trackWidgets) {
                if (trackWidget instanceof AbstractBlockStraightWidget) {
                    ((AbstractBlockStraightWidget) trackWidget).resetBlock();
                }
            }
        }
    }

    @Override
    protected void enableTrackWidgets() {
        updateTrackWidgetsState(true);
    }

    @Override
    protected void disableTrackWidgets() {
        updateTrackWidgetsState(false);
    }

    @Override
    public void addTrackWidget(Widget widget, int left, int top) {
        add(widget, left, top);
    }

    private void updateTrackWidgetsState(boolean state) {
        for (AbstractSvgTrackWidget trackWidget : trackWidgets) {
            trackWidget.setEnabled(state);
        }
    }

}
