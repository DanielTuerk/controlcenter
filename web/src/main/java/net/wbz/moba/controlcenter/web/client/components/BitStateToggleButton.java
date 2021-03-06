package net.wbz.moba.controlcenter.web.client.components;

import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.constants.Toggle;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

/**
 * @author Daniel Tuerk
 */
public class BitStateToggleButton extends Button {

    public static final String STATE_OFF = "OFF";
    public static final String STATE_ON = "ON";

    public BitStateToggleButton() {
        super(STATE_OFF);
        setDataToggle(Toggle.BUTTON);
        addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                updateState();
            }
        });
    }

    private void updateState() {
        if (isActive()) {
            setText(STATE_ON);
        } else {
            setText(STATE_OFF);
        }
    }

    @Override
    public void setActive(boolean active) {
        super.setActive(active);
        updateState();
    }
}
