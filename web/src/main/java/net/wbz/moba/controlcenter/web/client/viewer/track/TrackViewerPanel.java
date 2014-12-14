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
import net.wbz.moba.controlcenter.web.client.model.track.*;
import net.wbz.moba.controlcenter.web.shared.bus.DeviceInfoEvent;
import net.wbz.moba.controlcenter.web.shared.track.model.Configuration;
import net.wbz.moba.controlcenter.web.shared.track.model.TrackPart;
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

    private Label lblTrackPartConfig = new Label();

    private Map<Configuration, List<AbstractSvgTrackWidget>> trackWidgetsOfConfiguration = Maps.newConcurrentMap();
    private List<AbstractSvgTrackWidget> trackWidgets = Lists.newArrayList();

    private final RemoteEventListener trackPartStateEventListener;
    private final RemoteEventListener deviceConnectionEventListener;


    public TrackViewerPanel() {
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


    @Override
    protected void onLoad() {
        super.onLoad();
        addStyleName("boundary");

        EventReceiver.getInstance().addListener(TrackPartStateEvent.class, trackPartStateEventListener);
        EventReceiver.getInstance().addListener(DeviceInfoEvent.class, deviceConnectionEventListener);

        for (int i = getWidgetCount() - 1; i >= 0; i--) {
            remove(i);
        }

        lblTrackPartConfig.setType(LabelType.INFO);
        add(lblTrackPartConfig, 0, 0);

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
                            if (!trackWidgetsOfConfiguration.containsKey(trackPart.getDefaultToggleFunctionConfig())) {
                                trackWidgetsOfConfiguration.put(trackPart.getDefaultToggleFunctionConfig(), new ArrayList<AbstractSvgTrackWidget>());
                            }
                            trackWidgetsOfConfiguration.get(trackPart.getDefaultToggleFunctionConfig()).add(trackWidget);

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
                                    lblTrackPartConfig.setText(String.valueOf(trackPart.getDefaultToggleFunctionConfig()));
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
        EventReceiver.getInstance().removeListener(TrackPartStateEvent.class, trackPartStateEventListener);
        EventReceiver.getInstance().removeListener(DeviceInfoEvent.class, deviceConnectionEventListener);
    }

    @Override
    public void addTrackWidget(Widget widget, int left, int top) {
        add(widget, left, top);
    }

    private void updateTrackPartState(Configuration configuration, boolean state) {
        if (trackWidgetsOfConfiguration.containsKey(configuration)) {
            for (AbstractSvgTrackWidget controlSvgTrackWidget : trackWidgetsOfConfiguration.get(configuration)) {
                if (controlSvgTrackWidget instanceof BlockPart) {
                    BlockPart blockPart = (BlockPart) controlSvgTrackWidget;
                    if (configuration.isValid()) {
                        if (state) {
                            blockPart.usedBlock();
                        } else {
                            blockPart.freeBlock();
                        }
                    } else {
                        blockPart.unknownBlock();
                    }
                }
                if (controlSvgTrackWidget instanceof AbstractControlSvgTrackWidget) {
                    ((AbstractControlSvgTrackWidget) controlSvgTrackWidget).updateFunctionState(configuration, state);
                }
            }
        }
//        if (blockTrackWidgets.containsKey(configuration)) {
//            for (BlockPart blockPart : blockTrackWidgets.get(configuration)) {
//                if (configuration.isValid()) {
//                    if (state) {
//                        blockPart.usedBlock();
//                    } else {
//                        blockPart.freeBlock();
//                    }
//                } else {
//                    blockPart.unknownBlock();
//                }
//            }
//        }
    }
}
