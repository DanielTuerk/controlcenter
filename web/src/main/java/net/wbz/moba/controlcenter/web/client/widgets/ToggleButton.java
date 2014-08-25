package net.wbz.moba.controlcenter.web.client.widgets;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.event.dom.client.ClickHandler;

/**
 * Toggle button for gwt bootstrap.
 * Bootstrap doesn't support it atm. and the gwt implementation is crappy for layouts.
 *
 * TODO: state
 *
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
public class ToggleButton extends Button {

    public ToggleButton() {
    }

    public ToggleButton(ClickHandler handler) {
        super(handler);
    }

    public ToggleButton(String caption) {
        super(caption);
    }

    public ToggleButton(String caption, ClickHandler handler) {
        super(caption, handler);
    }

    public ToggleButton(String caption, IconType icon) {
        super(caption, icon);
    }

    public ToggleButton(String caption, IconType icon, ClickHandler handler) {
        super(caption, icon, handler);
    }
}
