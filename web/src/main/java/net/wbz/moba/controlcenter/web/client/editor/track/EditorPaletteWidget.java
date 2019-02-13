package net.wbz.moba.controlcenter.web.client.editor.track;

import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import net.wbz.moba.controlcenter.web.client.model.track.AbstractSvgTrackWidget;

/**
 * Widget wrapper class used by {@link net.wbz.moba.controlcenter.web.client.editor.track.PalettePanel}.
 */
class EditorPaletteWidget extends PaletteWidget {

    EditorPaletteWidget(AbstractSvgTrackWidget widget) {
        super(widget);
        DoubleClickHandler cc = new EditWidgetDoubleClickHandler(getWidget());
        getWidget().addDomHandler(cc, DoubleClickEvent.getType());
    }

}
