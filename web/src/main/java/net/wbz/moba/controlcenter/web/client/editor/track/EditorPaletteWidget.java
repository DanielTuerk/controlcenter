package net.wbz.moba.controlcenter.web.client.editor.track;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.user.client.ui.Widget;
import net.wbz.moba.controlcenter.web.client.model.track.AbstractSvgTrackWidget;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Modal;
import org.gwtbootstrap3.client.ui.ModalBody;
import org.gwtbootstrap3.client.ui.ModalFooter;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.Pull;

/**
 * Widget wrapper class used by {@link net.wbz.moba.controlcenter.web.client.editor.track.PalettePanel}.
 */
public class EditorPaletteWidget extends PaletteWidget {

    public EditorPaletteWidget(AbstractSvgTrackWidget widget) {
        super(widget);
        /**
         * TODO clean code
         */
        DoubleClickHandler cc =(DoubleClickHandler)new EditWidgetDoubleClickHandler((EditTrackWidgetHandler) getWidget());
            getWidget().addDomHandler(cc,DoubleClickEvent.getType());
    }

}
