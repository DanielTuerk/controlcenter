package net.wbz.moba.controlcenter.web.client.editor.track;

import com.google.gwt.event.dom.client.MouseOverHandler;
import net.wbz.moba.controlcenter.web.client.model.track.AbstractSvgTrackWidget;

/**
 * Widget wrapper class used by {@link net.wbz.moba.controlcenter.web.client.editor.track.PalettePanel}.
 */
public class ViewerPaletteWidget extends PaletteWidget {

    public ViewerPaletteWidget(AbstractSvgTrackWidget widget) {
        super(widget);
    }

    @Override
    protected void onLoad() {
        super.onLoad();

        if (getWidget() instanceof ClickActionViewerWidgetHandler) {
            getShim().addClickHandler(event -> ((ClickActionViewerWidgetHandler) getWidget()).onClick());
        }
    }

    public void addMouseOverHandler(MouseOverHandler handler) {
        getShim().addMouseOverHandler(handler);
    }
}
