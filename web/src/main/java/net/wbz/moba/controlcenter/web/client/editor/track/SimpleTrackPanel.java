package net.wbz.moba.controlcenter.web.client.editor.track;

import com.google.common.base.Strings;
import com.google.gwt.gen2.logging.shared.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import net.wbz.moba.controlcenter.web.client.request.RequestUtils;
import net.wbz.moba.controlcenter.web.client.model.track.AbsoluteTrackPosition;
import net.wbz.moba.controlcenter.web.client.model.track.AbstractSvgTrackWidget;
import net.wbz.moba.controlcenter.web.client.model.track.ModelManager;
import net.wbz.moba.controlcenter.web.shared.track.model.AbstractTrackPart;

/**
 * Panel to display the track in read only mode.
 * No events or state changes are recognized.
 *
 * @author Daniel Tuerk
 */
public class SimpleTrackPanel extends AbstractTrackPanel {

    private static final int TRACK_PANEL_PADDING_IN_PX = 2 * AbstractSvgTrackWidget.WIDGET_HEIGHT;

    protected SimpleTrackPanel() {
        super();
    }

    SimpleTrackPanel(boolean fixedSize) {
        super(fixedSize);
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        if (!isFixedSize()) {
            setWidth("100%");
        }
        loadTrack();
    }

    @Override
    public void addTrackWidget(Widget widget, int left, int top) {
        add(widget, left, top);
    }

    void loadTrack() {
        for (int i = getWidgetCount() - 1; i >= 0; i--) {
            remove(i);
        }

        RequestUtils.getInstance().getTrackEditorService().loadTrack(
                new AsyncCallback<Collection<AbstractTrackPart>>() {
                    @Override
                    public void onFailure(Throwable caught) {

                    }

                    @Override
                    public void onSuccess(Collection<AbstractTrackPart> trackParts) {
                        Log.info("load track success " + new Date().toString());
                        int maxTop = 0;
                        for (AbstractTrackPart trackPart : trackParts) {
                            AbstractSvgTrackWidget trackWidget = ModelManager.getInstance().getWidgetOf(trackPart);
                            trackWidget.setEnabled(true);
                            AbsoluteTrackPosition trackPosition = trackWidget.getTrackPosition(trackPart
                                    .getGridPosition(),
                                    getZoomLevel());

                            if (maxTop < trackPosition.getTop()) {
                                maxTop = trackPosition.getTop();
                            }

                            add(initTrackWidget(trackWidget), trackPosition.getLeft(), trackPosition.getTop());
                        }
                        Log.info("load track done " + new Date().toString());
                        if (!isFixedSize()) {
                            setHeight((maxTop + TRACK_PANEL_PADDING_IN_PX) + "px");
                        }
                        trackLoaded();
                    }
                });
    }

    /**
     * Track was successfully loaded and initialize.
     * Override to add custom action after loading.
     */
    protected void trackLoaded() {
    }

    public List<AbstractSvgTrackWidget> getTrackParts() {
        List<AbstractSvgTrackWidget> widgets = new ArrayList<>();
        for (Widget widget : getChildren()) {
            if (widget instanceof AbstractSvgTrackWidget) {
                widgets.add((AbstractSvgTrackWidget) widget);
            }
        }
        return widgets;
    }

    protected Widget initTrackWidget(AbstractSvgTrackWidget trackWidget) {
        return trackWidget;
    }
}
