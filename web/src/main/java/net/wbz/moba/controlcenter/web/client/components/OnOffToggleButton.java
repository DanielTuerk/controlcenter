package net.wbz.moba.controlcenter.web.client.components;

import org.gwtbootstrap3.extras.toggleswitch.client.ui.ToggleSwitch;
import org.gwtbootstrap3.extras.toggleswitch.client.ui.base.constants.ColorType;

import com.google.gwt.event.logical.shared.ValueChangeHandler;

/**
 * Toggle button with text and state text 'on' and 'off' colored.
 *
 * @author Daniel Tuerk
 */
public class OnOffToggleButton extends ToggleSwitch {

    public OnOffToggleButton() {
        setOffColor(ColorType.DANGER);
        setOnColor(ColorType.SUCCESS);
    }

    public OnOffToggleButton(String text, ValueChangeHandler<Boolean> valueChangeHandler) {
        this();
        setLabelText(text);
        addValueChangeHandler(valueChangeHandler);
    }

    /**
     * Quick fix for the fire events state. Bootstrap lib also fire the value change event if the
     * parameter is {@code false}.
     *
     * @param value value to set
     */
    public void updateValue(Boolean value) {
        super.setValue(value, false);
    }
}
