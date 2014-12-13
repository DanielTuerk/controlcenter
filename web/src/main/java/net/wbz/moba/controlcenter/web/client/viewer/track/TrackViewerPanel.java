package net.wbz.moba.controlcenter.web.client.viewer.track;

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

    private Map<Configuration, List<AbstractSvgTrackWidget>> trackWidgets = Maps.newConcurrentMap();
//    private Map<Configuration, List<AbstractBlockSvgTrackWidget>> blockTrackWidgets = Maps.newConcurrentMap();

    @Override
    protected void onLoad() {

        addStyleName("boundary");

//        setVisible(false);

        EventReceiver.getInstance().addListener(TrackPartStateEvent.class, new RemoteEventListener() {
            public void apply(Event anEvent) {
                if (anEvent instanceof TrackPartStateEvent) {
                    TrackPartStateEvent event = (TrackPartStateEvent) anEvent;
                    updateTrackPartState(event.getConfiguration(), event.isOn());
                }
            }
        });


        for (int i = getWidgetCount() - 1; i >= 0; i--) {
            remove(i);
        }

        lblTrackPartConfig.setType(LabelType.INFO);
        add(lblTrackPartConfig, 0, 0);

//        <b:Progress active="true" type="STRIPED">
//        <b:ProgressBar type="SUCCESS" percent="40"/>
//        </b:Progress>
//        Progress progress = new Progress();
//        progress.setActive(true);
//        progress.setType(ProgressType.STRIPED);
//        final ProgressBar progressBar = new ProgressBar(0);
//        progress.add(progressBar);

        ServiceUtils.getTrackEditorService().loadTrack(new AsyncCallback<TrackPart[]>() {
            @Override
            public void onFailure(Throwable throwable) {
                Log.severe(throwable.getLocalizedMessage());
            }

            @Override
            public void onSuccess(TrackPart[] trackParts) {
                trackWidgets.clear();
//                blockTrackWidgets.clear();

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
                    if (!trackWidgets.containsKey(trackPart.getDefaultToggleFunctionConfig())) {
                        trackWidgets.put(trackPart.getDefaultToggleFunctionConfig(), new ArrayList<AbstractSvgTrackWidget>());
                    }
                    trackWidgets.get(trackPart.getDefaultToggleFunctionConfig()).add(trackWidget);

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

                    //updateTrackPartState(trackPart.getConfiguration(), trackPart.isInitialState());
                }
            }
        });
    }

    @Override
    public void addTrackWidget(Widget widget, int left, int top) {
        add(widget, left, top);
    }

    private void updateTrackPartState(Configuration configuration, boolean state) {
        if (trackWidgets.containsKey(configuration)) {
            for (AbstractSvgTrackWidget controlSvgTrackWidget : trackWidgets.get(configuration)) {
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
                } else if (controlSvgTrackWidget instanceof AbstractControlSvgTrackWidget) {
                    ((AbstractControlSvgTrackWidget) controlSvgTrackWidget).repaint(state);
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
