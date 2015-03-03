package net.wbz.moba.controlcenter.web.client.viewer.track;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.gen2.logging.shared.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;
import de.novanic.eventservice.client.event.Event;
import de.novanic.eventservice.client.event.listener.RemoteEventListener;
import net.wbz.moba.controlcenter.web.client.EventReceiver;
import net.wbz.moba.controlcenter.web.client.ServiceUtils;
import net.wbz.moba.controlcenter.web.client.editor.track.AbstractTrackPanel;
import net.wbz.moba.controlcenter.web.client.editor.track.ViewerPaletteWidget;
import net.wbz.moba.controlcenter.web.client.model.track.AbsoluteTrackPosition;
import net.wbz.moba.controlcenter.web.client.model.track.AbstractBlockSvgTrackWidget;
import net.wbz.moba.controlcenter.web.client.model.track.AbstractSvgTrackWidget;
import net.wbz.moba.controlcenter.web.client.model.track.ModelManager;
import net.wbz.moba.controlcenter.web.client.model.track.signal.AbstractSignalWidget;
import net.wbz.moba.controlcenter.web.client.util.EmptyCallback;
import net.wbz.moba.controlcenter.web.shared.bus.DeviceInfoEvent;
import net.wbz.moba.controlcenter.web.shared.bus.FeedbackBlockEvent;
import net.wbz.moba.controlcenter.web.shared.track.model.Configuration;
import net.wbz.moba.controlcenter.web.shared.track.model.TrackPart;
import net.wbz.moba.controlcenter.web.shared.train.Train;
import net.wbz.moba.controlcenter.web.shared.viewer.SignalFunctionStateEvent;
import net.wbz.moba.controlcenter.web.shared.viewer.TrackPartStateEvent;
import org.gwtbootstrap3.client.ui.Label;
import org.gwtbootstrap3.client.ui.constants.LabelType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Panel for the track viewer.
 * <p/>
 * Loading the track and add all {@link net.wbz.moba.controlcenter.web.shared.track.model.TrackPart}s to the panel.
 * Register the event listener to receive state changes of all added
 * {@link net.wbz.moba.controlcenter.web.shared.track.model.TrackPart}s.
 *
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
public class TrackViewerPanel extends AbstractTrackPanel {

    public static final int PERCENTAGE_MAX = 99;
    public static final int PERCENTAGE_START_TRACK = 30;
    public static final String ID = "trackViewerPanel";

    private Label lblTrackPartConfig = new Label();

    private Map<Configuration, List<AbstractSvgTrackWidget>> trackWidgetsOfConfiguration = Maps.newConcurrentMap();
    private List<AbstractSvgTrackWidget> trackWidgets = Lists.newArrayList();
    private List<AbstractSignalWidget> signalTrackWidgets = Lists.newArrayList();

    private TrackPart[] loadedTrackParts;

    private final RemoteEventListener trackPartStateEventListener;
    private final RemoteEventListener signalFunctionStateEventListener;
    private final RemoteEventListener deviceConnectionEventListener;
    private final RemoteEventListener feedbackBlockEventListener;

    public TrackViewerPanel() {
        signalFunctionStateEventListener = new RemoteEventListener() {
            public void apply(Event anEvent) {
                if (anEvent instanceof SignalFunctionStateEvent) {
                    for (AbstractSignalWidget signalTrackWidget : signalTrackWidgets) {
                        SignalFunctionStateEvent signalFunctionStateEvent = (SignalFunctionStateEvent) anEvent;
                        if (signalTrackWidget.getTrackPart().getSignalConfiguration().equals(signalFunctionStateEvent.getConfiguration())) {
                            signalTrackWidget.showSignalFunction(signalFunctionStateEvent.getSignalFunction());
                        }
                    }
                }
            }
        };
        trackPartStateEventListener = new RemoteEventListener() {
            public void apply(Event anEvent) {
                if (anEvent instanceof TrackPartStateEvent) {
                    TrackPartStateEvent event = (TrackPartStateEvent) anEvent;
                    updateTrackPartState(event.getConfiguration(), event.isOn());
                }
            }
        };
        deviceConnectionEventListener = new RemoteEventListener() {
            public void apply(Event anEvent) {
                if (anEvent instanceof DeviceInfoEvent) {
                    DeviceInfoEvent event = (DeviceInfoEvent) anEvent;
                    if (event.getEventType() == DeviceInfoEvent.TYPE.CONNECTED) {
                        enableTrackWidgets();
                    } else if (event.getEventType() == DeviceInfoEvent.TYPE.DISCONNECTED) {
                        disableTrackWidgets();
                    }
                }
            }
        };
        feedbackBlockEventListener = new RemoteEventListener() {
            public void apply(Event anEvent) {
                if (anEvent instanceof FeedbackBlockEvent) {
                    FeedbackBlockEvent event = (FeedbackBlockEvent) anEvent;
                    updateTrainOnTrack(event.getAddress(), event.getBlock(), event.getTrain(), event.getState());
                }
            }
        };
        getElement().setId(ID);
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        addStyleName("boundary");

        EventReceiver.getInstance().addListener(TrackPartStateEvent.class, trackPartStateEventListener);
        EventReceiver.getInstance().addListener(SignalFunctionStateEvent.class, signalFunctionStateEventListener);
        EventReceiver.getInstance().addListener(DeviceInfoEvent.class, deviceConnectionEventListener);
        EventReceiver.getInstance().addListener(FeedbackBlockEvent.class, feedbackBlockEventListener);

        for (int i = getWidgetCount() - 1; i >= 0; i--) {
            remove(i);
        }

        lblTrackPartConfig.setType(LabelType.INFO);
        add(lblTrackPartConfig, 0, 0);

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
                        loadedTrackParts = trackParts;
                        trackWidgetsOfConfiguration.clear();
                        signalTrackWidgets.clear();

                        int maxTop = 0;
                        int maxLeft = 0;
                        int percentage = PERCENTAGE_START_TRACK;
                        for (int i = 0; i < trackParts.length; i++) {
                            final TrackPart trackPart = trackParts[i];
                            if (i + 1 % 6 == 0) {
                                percentage += 10;
                                if (percentage > PERCENTAGE_MAX) {
                                    percentage = PERCENTAGE_MAX;
                                }
                            }

                            AbstractSvgTrackWidget trackWidget = ModelManager.getInstance().getWidgetOf(trackPart);
                            trackWidget.setEnabled(result);

                            trackWidgets.add(trackWidget);

                            if (trackWidget instanceof AbstractSignalWidget) {
                                signalTrackWidgets.add((AbstractSignalWidget) trackWidget);
                            } else {
                                for (Configuration configuration : trackPart.getFunctionConfigs().values()) {

                                    // ignore default configs of track widget to register event handler
                                    if (configuration.isValid()) {
                                        if (!trackWidgetsOfConfiguration.containsKey(configuration)) {
                                            trackWidgetsOfConfiguration.put(configuration, new ArrayList<AbstractSvgTrackWidget>());
                                        }
                                        // avoid same widget for equal bit state configuration
                                        if (!trackWidgetsOfConfiguration.get(configuration).contains(trackWidget)) {
                                            trackWidgetsOfConfiguration.get(configuration).add(trackWidget);
                                        }
                                    }
                                }
                            }
                            AbsoluteTrackPosition trackPosition = trackWidget.getTrackPosition(trackPart.getGridPosition(), getZoomLevel());
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
                                    lblTrackPartConfig.setText(trackPart.getConfigurationInfo());
                                }
                            });
                            addTrackWidget(widget, trackPosition.getLeft(), trackPosition.getTop());
                        }
                        registerWidgetsToReceiveEvents();
                    }
                });
            }
        });
    }

    @Override
    protected void onUnload() {
        super.onUnload();
        EventReceiver.getInstance().removeListener(TrackPartStateEvent.class, trackPartStateEventListener);
        EventReceiver.getInstance().removeListener(SignalFunctionStateEvent.class, signalFunctionStateEventListener);
        EventReceiver.getInstance().removeListener(DeviceInfoEvent.class, deviceConnectionEventListener);
        EventReceiver.getInstance().removeListener(FeedbackBlockEvent.class, feedbackBlockEventListener);
    }

    /**
     * Show train label on the given block.
     *
     * @param address address of the block
     * @param block   number of the block
     * @param train   address of the train
     * @param state   {@link net.wbz.moba.controlcenter.web.shared.bus.FeedbackBlockEvent.STATE} enter or exit the block
     */
    private void updateTrainOnTrack(final int address, final int block, final int train, final FeedbackBlockEvent.STATE state) {
        ServiceUtils.getTrainEditorService().getTrain(train, new AsyncCallback<Train>() {
            @Override
            public void onFailure(Throwable caught) {
            }

            @Override
            public void onSuccess(Train result) {
                Configuration configAsIdentifier = new Configuration(1, address, block, true);
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

    private void enableTrackWidgets() {
        updateTrackWidgetsState(true);
    }

    private void disableTrackWidgets() {
        updateTrackWidgetsState(false);
    }

    private void updateTrackWidgetsState(boolean state) {
        for (AbstractSvgTrackWidget trackWidget : trackWidgets) {
            trackWidget.setEnabled(state);
        }
    }


    private void registerWidgetsToReceiveEvents() {
        ServiceUtils.getTrackEditorService().registerConsumersByConnectedDeviceForTrackParts(loadedTrackParts,
                new EmptyCallback<Void>());
    }


    @Override
    public void addTrackWidget(Widget widget, int left, int top) {
        add(widget, left, top);
    }

    private void updateTrackPartState(Configuration configuration, boolean state) {
        if (trackWidgetsOfConfiguration.containsKey(configuration)) {
            for (AbstractSvgTrackWidget controlSvgTrackWidget : trackWidgetsOfConfiguration.get(configuration)) {
                controlSvgTrackWidget.updateFunctionState(configuration, state);
            }
        }
    }
}
