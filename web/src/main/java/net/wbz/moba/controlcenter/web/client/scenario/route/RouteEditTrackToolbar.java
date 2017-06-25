package net.wbz.moba.controlcenter.web.client.scenario.route;

import org.gwtbootstrap3.client.ui.ButtonGroup;
import org.gwtbootstrap3.client.ui.RadioButton;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Daniel Tuerk
 */
class RouteEditTrackToolbar extends Composite {
    private static Binder uiBinder = GWT.create(Binder.class);

    @UiField
    ButtonGroup radioMode;
    private RouteEditMode currentMode;

    public RouteEditTrackToolbar() {
        initWidget(uiBinder.createAndBindUi(this));

        for (final RouteEditMode mode : RouteEditMode.values()) {
            RadioButton radioButton = new RadioButton(mode.name(), mode.name());
            radioButton.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    currentMode = mode;
                }
            });
            radioMode.add(radioButton);
        }
    }

    @Override
    protected void onLoad() {
        currentMode = null;

    }

    RouteEditMode getCurrentMode() {
        return currentMode;
    }

    interface Binder extends UiBinder<Widget, RouteEditTrackToolbar> {
    }
}
