package net.wbz.moba.controlcenter.web.client.scenario.route;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import java.util.ArrayList;
import java.util.List;
import org.gwtbootstrap3.client.ui.ButtonGroup;
import org.gwtbootstrap3.client.ui.RadioButton;

/**
 * Toolbar for the track viewer in the {@link RouteEditModalBody} to select trackparts by the {@link RouteEditMode}.
 *
 * @author Daniel Tuerk
 */
class RouteEditTrackToolbar extends Composite {
    private static Binder uiBinder = GWT.create(Binder.class);

    private final List<ModeChangeListener> listeners = new ArrayList<>();
    @UiField
    ButtonGroup radioMode;
    private RouteEditMode currentMode;

    public RouteEditTrackToolbar() {
        initWidget(uiBinder.createAndBindUi(this));

        for (final RouteEditMode mode : RouteEditMode.values()) {
            RadioButton radioButton = new RadioButton(mode.name(), mode.name());
            radioButton.addClickHandler(event -> {
                currentMode = mode;
                fireModeChange();
            });
            radioMode.add(radioButton);
        }
    }

    @Override
    protected void onLoad() {
        currentMode = null;
    }

    void addListener(ModeChangeListener listener) {
        listeners.add(listener);
    }

    void removeListener(ModeChangeListener listener) {
        listeners.remove(listener);
    }
    RouteEditMode getCurrentMode() {
        return currentMode;
    }

    private void fireModeChange() {
        listeners.forEach(listener -> listener.modeChanged(currentMode));
    }
    interface Binder extends UiBinder<Widget, RouteEditTrackToolbar> {
    }

    interface ModeChangeListener {

        void modeChanged(RouteEditMode newMode);
    }
}
