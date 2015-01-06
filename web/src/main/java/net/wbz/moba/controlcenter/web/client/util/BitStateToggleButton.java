package net.wbz.moba.controlcenter.web.client.util;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.constants.Toggle;

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
                if (isActive()) {
                    setText(STATE_OFF);
                } else {
                    setText(STATE_ON);
                }
            }
        });
    }
}
