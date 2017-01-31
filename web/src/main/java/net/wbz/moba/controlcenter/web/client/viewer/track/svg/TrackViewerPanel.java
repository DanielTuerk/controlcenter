package net.wbz.moba.controlcenter.web.client.viewer.track.svg;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.gwtbootstrap3.client.ui.Label;
import org.gwtbootstrap3.client.ui.constants.LabelType;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;

import net.wbz.moba.controlcenter.web.client.RequestUtils;
import net.wbz.moba.controlcenter.web.client.editor.track.ViewerPaletteWidget;
import net.wbz.moba.controlcenter.web.client.model.track.AbsoluteTrackPosition;
import net.wbz.moba.controlcenter.web.client.model.track.AbstractBlockSvgTrackWidget;
import net.wbz.moba.controlcenter.web.client.model.track.AbstractControlSvgTrackWidget;
import net.wbz.moba.controlcenter.web.client.model.track.AbstractSvgTrackWidget;
import net.wbz.moba.controlcenter.web.client.model.track.ModelManager;
import net.wbz.moba.controlcenter.web.client.model.track.signal.AbstractSignalWidget;
import net.wbz.moba.controlcenter.web.client.viewer.track.AbstractTrackViewerPanel;
import net.wbz.moba.controlcenter.web.server.persist.construction.track.AbstractTrackPartEntity;
import net.wbz.moba.controlcenter.web.shared.bus.FeedbackBlockEvent;
import net.wbz.moba.controlcenter.web.shared.track.model.AbstractTrackPart;
import net.wbz.moba.controlcenter.web.shared.track.model.BusDataConfiguration;
import net.wbz.moba.controlcenter.web.shared.train.Train;
import net.wbz.moba.controlcenter.web.shared.viewer.SignalFunctionStateEvent;

/**
 * Panel for the track viewer.
 * <p/>
 * Loading the track and add all {@link AbstractTrackPartEntity}s to the panel.
 * Register the event listener to receive state changes of all added
 * {@link AbstractTrackPartEntity}s.
 *
 * @author Daniel Tuerk
 */
public class TrackViewerPanel extends AbstractTrackViewerPanel {

    private Label lblTrackPartConfig = new Label();

    private Map<BusDataConfiguration, List<AbstractSvgTrackWidget>> trackWidgetsOfConfiguration = Maps
            .newConcurrentMap();
    private List<AbstractSvgTrackWidget> trackWidgets = Lists.newArrayList();
    private List<AbstractSignalWidget> signalTrackWidgets = Lists.newArrayList();

    private List<AbstractTrackPart> loadedTrackParts;

    public TrackViewerPanel() {

    }

    @Override
    protected void onLoad() {
        super.onLoad();
        addStyleName("boundary");

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
            public void onSuccess(final Boolean response) {

                RequestUtils.getInstance().getTrackEditorService().loadTrack(
                        new AsyncCallback<Collection<AbstractTrackPart>>() {
                            @Override
                            public void onFailure(Throwable caught) {

                            }

                            @Override
                            public void onSuccess(Collection<AbstractTrackPart> trackParts) {
                                loadedTrackParts = Lists.newArrayList(trackParts);
                                trackWidgetsOfConfiguration.clear();
                                signalTrackWidgets.clear();

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

                                    final AbstractSvgTrackWidget trackWidget = ModelManager.getInstance().getWidgetOf(
                                            trackPart);
                                    trackWidget.setEnabled(response);

                                    trackWidgets.add(trackWidget);

                                    if(trackWidget instanceof AbstractBlockSvgTrackWidget){
                                        ((AbstractBlockSvgTrackWidget) trackWidget).unknownBlock();
                                    }
                                    if (trackWidget instanceof AbstractSignalWidget) {
                                        signalTrackWidgets.add((AbstractSignalWidget) trackWidget);
                                    }
                                    for (BusDataConfiguration configuration : trackPart
                                            .getConfigurationsOfFunctions()) {
                                        // ignore default configs of track widget to register event handler
                                        if (configuration != null && configuration.isValid()) {
                                            if (!trackWidgetsOfConfiguration.containsKey(configuration)) {
                                                trackWidgetsOfConfiguration.put(configuration,
                                                        new ArrayList<AbstractSvgTrackWidget>());
                                            }
                                            // avoid same widget for equal bit state configuration
                                            if (!trackWidgetsOfConfiguration.get(configuration).contains(trackWidget)) {
                                                trackWidgetsOfConfiguration.get(configuration).add(trackWidget);
                                            }
                                        }
                                    }
                                    AbsoluteTrackPosition trackPosition = trackWidget.getTrackPosition(trackPart
                                            .getGridPosition(), getZoomLevel());
                                    if (maxTop < trackPosition.getTop()) {
                                        maxTop = trackPosition.getTop();
                                    }
                                    if (maxLeft < trackPosition.getLeft()) {
                                        maxLeft = trackPosition.getLeft();
                                    }
                                    ViewerPaletteWidget widget = new ViewerPaletteWidget(trackWidget);
                                    widget.addMouseOverHandler(new MouseOverHandler() {
                                        @Override
                                        public void onMouseOver(MouseOverEvent event) {
                                            lblTrackPartConfig.setText(trackWidget.getConfigurationInfo());
                                        }
                                    });
                                    addTrackWidget(widget, trackPosition.getLeft(), trackPosition.getTop());
                                }
                            }
                        });
            }
        });
    }

    @Override
    protected void onUnload() {
        super.onUnload();
    }

    @Override
    protected void updateSignalState(SignalFunctionStateEvent signalFunctionStateEvent) {
        for (AbstractSignalWidget signalTrackWidget : signalTrackWidgets) {
            if (signalTrackWidget.getTrackPart().getSignalConfigurations().containsAll(signalFunctionStateEvent
                    .getConfigurations())) {
                signalTrackWidget.showSignalFunction(signalFunctionStateEvent.getSignalFunction());
            }
        }
    }

    @Override
    protected void updateTrackPartState(BusDataConfiguration configuration, boolean state) {
        if (trackWidgetsOfConfiguration.containsKey(configuration)) {
            for (AbstractSvgTrackWidget controlSvgTrackWidget : trackWidgetsOfConfiguration.get(configuration)) {
                if (controlSvgTrackWidget instanceof AbstractControlSvgTrackWidget) {
                    ((AbstractControlSvgTrackWidget) controlSvgTrackWidget).updateFunctionState(configuration, state);
                }
            }
        }
    }

    @Override
    protected void updateTrackPartBlockState(BusDataConfiguration configuration, boolean state) {
        if (trackWidgetsOfConfiguration.containsKey(configuration)) {
            for (AbstractSvgTrackWidget controlSvgTrackWidget : trackWidgetsOfConfiguration.get(configuration)) {
                if (controlSvgTrackWidget instanceof AbstractBlockSvgTrackWidget) {
                    ((AbstractBlockSvgTrackWidget) controlSvgTrackWidget).updateBlockState(configuration, state);
                }
            }
        }
    }

    /**
     * Show train label on the given block.
     *
     * @param address address of the block
     * @param block number of the block
     * @param train address of the train
     * @param state {@link net.wbz.moba.controlcenter.web.shared.bus.FeedbackBlockEvent.STATE} enter or exit the block
     */
    @Override
    protected void updateTrainOnTrack(final int address, final int block, final int train,
            final FeedbackBlockEvent.STATE state) {
        RequestUtils.getInstance().getTrainEditorService().getTrain(train, new AsyncCallback<Train>() {
            @Override
            public void onFailure(Throwable caught) {

            }

            @Override
            public void onSuccess(Train result) {
                BusDataConfiguration configAsIdentifier = new BusDataConfiguration(1, address, block, true);
                if (trackWidgetsOfConfiguration.containsKey(configAsIdentifier)) {
                    for (AbstractSvgTrackWidget svgTrackWidget : trackWidgetsOfConfiguration.get(configAsIdentifier)) {
                        if (svgTrackWidget instanceof AbstractBlockSvgTrackWidget) {
                            switch (state) {
                                case ENTER:
                                    ((AbstractBlockSvgTrackWidget) svgTrackWidget).showTrainOnBlock(result);
                                    break;
                                case EXIT:
                                    ((AbstractBlockSvgTrackWidget) svgTrackWidget).removeTrainOnBlock(result);
                                    break;
                            }
                        }
                    }
                }
            }
        });
    }

    @Override
    protected void enableTrackWidgets() {
        updateTrackWidgetsState(true);
    }

    @Override
    protected void disableTrackWidgets() {
        updateTrackWidgetsState(false);
    }

    private void updateTrackWidgetsState(boolean state) {
        for (AbstractSvgTrackWidget trackWidget : trackWidgets) {
            trackWidget.setEnabled(state);
        }
    }

    @Override
    public void addTrackWidget(Widget widget, int left, int top) {
        add(widget, left, top);
    }

}
