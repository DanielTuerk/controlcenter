package net.wbz.moba.controlcenter.web.client.viewer.track;

import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.constants.LabelType;
import com.google.common.collect.Maps;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.gen2.logging.shared.Log;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;
import de.novanic.eventservice.client.event.Event;
import de.novanic.eventservice.client.event.listener.RemoteEventListener;
import net.wbz.moba.controlcenter.web.client.EventReceiver;
import net.wbz.moba.controlcenter.web.client.ServiceUtils;
import net.wbz.moba.controlcenter.web.client.editor.track.AbstractTrackPanel;
import net.wbz.moba.controlcenter.web.client.editor.track.ClickActionViewerWidgetHandler;
import net.wbz.moba.controlcenter.web.client.editor.track.TrackEditorContainer;
import net.wbz.moba.controlcenter.web.client.editor.track.ViewerPaletteWidget;
import net.wbz.moba.controlcenter.web.client.model.track.*;
import net.wbz.moba.controlcenter.web.client.util.DialogBoxUtil;
import net.wbz.moba.controlcenter.web.client.viewer.controls.ViewerControlsPanel;
import net.wbz.moba.controlcenter.web.shared.track.model.Configuration;
import net.wbz.moba.controlcenter.web.shared.track.model.TrackPart;
import net.wbz.moba.controlcenter.web.shared.viewer.TrackPartStateEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Panel for the track viewer.
 *
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

    private Map<Configuration, List<AbstractControlSvgTrackWidget>> controlTrackWidgets = Maps.newConcurrentMap();
    private Map<Configuration, List<AbstractBlockSvgTrackWidget>> blockTrackWidgets = Maps.newConcurrentMap();

    @Override
    protected void onLoad() {

        addStyleName("boundary");

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

        ServiceUtils.getTrackEditorService().loadTrack(new AsyncCallback<TrackPart[]>() {
            @Override
            public void onFailure(Throwable throwable) {
                Log.severe(throwable.getLocalizedMessage());
            }

            @Override
            public void onSuccess(TrackPart[] trackParts) {
                controlTrackWidgets.clear();
                blockTrackWidgets.clear();

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
                    if (trackWidget instanceof AbstractControlSvgTrackWidget) {
                        if (!controlTrackWidgets.containsKey(trackPart.getConfiguration())) {
                            controlTrackWidgets.put(trackPart.getConfiguration(), new ArrayList<AbstractControlSvgTrackWidget>());
                        }
                        controlTrackWidgets.get(trackPart.getConfiguration()).add((AbstractControlSvgTrackWidget) trackWidget);
                    } else if (trackWidget instanceof AbstractBlockSvgTrackWidget && trackPart.getConfiguration() !=null) {
                        if (!blockTrackWidgets.containsKey(trackPart.getConfiguration())) {
                            blockTrackWidgets.put(trackPart.getConfiguration(), new ArrayList<AbstractBlockSvgTrackWidget>());
                        }
                        blockTrackWidgets.get(trackPart.getConfiguration()).add((AbstractBlockSvgTrackWidget) trackWidget);
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
                            lblTrackPartConfig.setText(String.valueOf(trackPart.getConfiguration()));
                        }
                    });
                    addTrackWidget(widget, trackPosition.getLeft(), trackPosition.getTop());

                    updateTrackPartState(trackPart.getConfiguration(), trackPart.isInitialState());
                }
            }
        });
    }

    @Override
    public void addTrackWidget(Widget widget, int left, int top) {
        add(widget, left, top);
    }

    private void updateTrackPartState(Configuration configuration, boolean state) {
        if (controlTrackWidgets.containsKey(configuration)) {
            for (AbstractControlSvgTrackWidget controlSvgTrackWidget : controlTrackWidgets.get(configuration)) {
                controlSvgTrackWidget.repaint(state);
            }
        }
        if (blockTrackWidgets.containsKey(configuration)) {
            for (BlockPart blockPart : blockTrackWidgets.get(configuration)) {
                if (state) {
                    blockPart.usedBlock();
                } else {
                    blockPart.freeBlock();
                }
            }
        }
    }
}
