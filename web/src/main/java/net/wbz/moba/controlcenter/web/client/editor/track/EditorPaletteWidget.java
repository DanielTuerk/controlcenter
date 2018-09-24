package net.wbz.moba.controlcenter.web.client.editor.track;

import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import net.wbz.moba.controlcenter.web.client.model.track.AbstractSvgTrackWidget;

/**
 * Widget wrapper class used by {@link net.wbz.moba.controlcenter.web.client.editor.track.PalettePanel}.
 */
public class EditorPaletteWidget extends PaletteWidget {

    public EditorPaletteWidget(AbstractSvgTrackWidget widget) {
        super(widget);
        /*
          TODO clean code
         */
        DoubleClickHandler cc = new EditWidgetDoubleClickHandler(getWidget());
        getWidget().addDomHandler(cc, DoubleClickEvent.getType());
    }

}
