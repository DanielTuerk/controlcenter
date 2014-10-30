package net.wbz.moba.controlcenter.web.client.viewer.track;

import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.constants.LabelType;
import com.google.common.collect.Maps;
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
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
public class TrackViewerPanel extends AbstractTrackPanel {

    public static final int PERCENTAGE_MAX = 99;
    public static final int PERCENTAGE_START_TRACK = 30;

    private Label lblTrackPartConfig = new Label();

    /**
     * TODO: multiple widgets for one configuration
     */
    private Map<Configuration, List<AbstractControlSvgTrackWidget>> controlTrackWidgets = Maps.newConcurrentMap();
    private Map<Configuration, List<AbstractBlockSvgTrackWidget>> blockTrackWidgets = Maps.newConcurrentMap();

    @Override
    protected void onLoad() {
        addStyleName("boundary");

         /* Logic for GWTEventService starts here */
        //add a listener to the SERVER_MESSAGE_DOMAIN
        EventReceiver.getInstance().addListener(TrackPartStateEvent.class, new RemoteEventListener() {
            public void apply(Event anEvent) {
                if (anEvent instanceof TrackPartStateEvent) {
                    TrackPartStateEvent event = (TrackPartStateEvent) anEvent;
                    if (controlTrackWidgets.containsKey(event.getConfiguration())) {
                        for (AbstractControlSvgTrackWidget controlSvgTrackWidget : controlTrackWidgets.get(event.getConfiguration())) {
                            controlSvgTrackWidget.repaint(event.isOn());
                        }
                    }
                    if (blockTrackWidgets.containsKey(event.getConfiguration())) {
                        for (BlockPart blockPart : blockTrackWidgets.get(event.getConfiguration())) {
                            if (event.isOn()) {
                                blockPart.usedBlock();
                            } else {
                                blockPart.freeBlock();
                            }
                        }
                    } else {
                        net.wbz.moba.controlcenter.web.client.util.Log.console("can't find widget of " + event.getConfiguration().toString());
                    }

                }
            }
        });

        // TODO progress bar not working
        DialogBoxUtil.getLoading().setProgressPercentage(0);
        DialogBoxUtil.getLoading().center();
        DialogBoxUtil.getLoading().show();
        DialogBoxUtil.getLoading().setProgressPercentage(1, "remove existing track");

        for (int i = getWidgetCount() - 1; i >= 0; i--) {
            remove(i);
        }

        DialogBoxUtil.getLoading().setProgressPercentage(PERCENTAGE_START_TRACK, "load track");

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
                    DialogBoxUtil.getLoading().setProgressPercentage(percentage, "add trackpart " + (i + 1) + "/" + trackParts.length);

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

                    if (trackWidget instanceof ClickActionViewerWidgetHandler) {
                        ((ClickActionViewerWidgetHandler) trackWidget).repaint();
                    }

                }
                DialogBoxUtil.getLoading().setProgressPercentage(PERCENTAGE_MAX, "set dimension");
                setPixelSize(Window.getClientWidth() - ViewerControlsPanel.WIDTH_PIXEL, maxTop + TrackEditorContainer.draggableOffsetHeight);
                DialogBoxUtil.getLoading().hide();
            }
        });
    }

    @Override
    public void addTrackWidget(Widget widget, int left, int top) {
        add(widget, left, top);
    }
}
