package net.wbz.moba.controlcenter.web.client.editor.track;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOverHandler;
import net.wbz.moba.controlcenter.web.client.model.track.AbstractImageTrackWidget;

/**
 * Widget wrapper class used by {@link net.wbz.moba.controlcenter.web.client.editor.track.PalettePanel}.
 */
public class ViewerPaletteWidget extends PaletteWidget {

    public ViewerPaletteWidget(AbstractImageTrackWidget widget) {
        super(widget);
    }

    @Override
    protected void onLoad() {
        super.onLoad();

        if (getWidget() instanceof ClickActionViewerWidgetHandler) {
            getShim().addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    ((ClickActionViewerWidgetHandler) getWidget()).onClick();
                }
            });
        }
    }

    public void addMouseOverHandler(MouseOverHandler handler) {
        getShim().addMouseOverHandler(handler);
    }
}
