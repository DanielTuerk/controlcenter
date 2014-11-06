package net.wbz.moba.controlcenter.web.client.widgets;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

/**
 * Toggle button for gwt bootstrap.
 * Bootstrap doesn't use an extra class (#setToggle() for normal button).
 * The gwt implementation is crappy for layouts.
 *
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
public class ToggleButton extends Button {

    private String toggledCaption;
    private String tempCaption;

    public ToggleButton() {
        this(null);
    }

    public ToggleButton(String caption) {
        this(caption, null);
    }

    public ToggleButton(String caption, String toggledCaption) {
        super(caption);
        this.toggledCaption = toggledCaption;
        setToggle(true);
    }

    public void addToggleHandler(final ToggleHandler handler) {
        addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                if (!isToggled()) {
                    if (toggledCaption != null) {
                        tempCaption = getText();
                        setText(toggledCaption);
                    }
                    handler.isOn();
                } else {
                    if (toggledCaption != null) {
                        setText(tempCaption);
                    }
                    handler.isOff();
                }
            }
        });
    }


    /**
     * Handler for the state change of an {@link net.wbz.moba.controlcenter.web.client.widgets.ToggleButton}.
     */
    public interface ToggleHandler {

        /**
         * Button is toggled on.
         */
        public void isOn();

        /**
         * Button is toggled off.
         */
        public void isOff();
    }
}
