package net.wbz.moba.controlcenter.web.client.editor.track;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.google.gwt.gen2.logging.shared.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;

import net.wbz.moba.controlcenter.web.client.RequestUtils;
import net.wbz.moba.controlcenter.web.client.model.track.AbsoluteTrackPosition;
import net.wbz.moba.controlcenter.web.client.model.track.AbstractSvgTrackWidget;
import net.wbz.moba.controlcenter.web.client.model.track.ModelManager;
import net.wbz.moba.controlcenter.web.shared.track.model.AbstractTrackPart;

/**
 * @author Daniel Tuerk
 */
public class SimpleTrackPanel extends AbstractTrackPanel {

    @Override
    protected void onLoad() {
        addStyleName("boundary");
        setSize("100%", "800px");

        loadTrack();
    }

    @Override
    public void addTrackWidget(Widget widget, int left, int top) {
        add(widget, left, top);
    }

    public void loadTrack() {
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
                        for (AbstractTrackPart trackPart : trackParts) {
                            AbstractSvgTrackWidget trackWidget = ModelManager.getInstance().getWidgetOf(trackPart);
                            trackWidget.setEnabled(true);
                            AbsoluteTrackPosition trackPosition = trackWidget.getTrackPosition(trackPart
                                    .getGridPosition(),
                                    getZoomLevel());

                            add(initTrackWidget(trackWidget), trackPosition.getLeft(), trackPosition.getTop());
                        }
                        Log.info("load track done " + new Date().toString());
                    }
                });
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
