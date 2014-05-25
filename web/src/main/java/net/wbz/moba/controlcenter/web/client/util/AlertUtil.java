package net.wbz.moba.controlcenter.web.client.util;

import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FlowPanel;

/**
 * Created by Daniel on 17.05.2014.
 */
public class AlertUtil {

    public static int DEFAULT_SHOW_TIME_MILLIS = 2000;

    public static void showAlert(final com.google.gwt.user.client.ui.Panel parent, String text, AlertType alertType) {
        showAlert(parent, text, alertType, DEFAULT_SHOW_TIME_MILLIS);
    }

    public static void showAlert(final com.google.gwt.user.client.ui.Panel parent, String text, AlertType alertType, int showTimeMillis) {
        final Alert alert = new Alert(text, alertType, true);
        alert.setAnimation(true);
        alert.getElement().getStyle().setZIndex(1000);
        parent.add(alert);
        Timer timer = new Timer() {
            public void run() {
                alert.close();
            }
        };
        timer.schedule(showTimeMillis);
    }

    public static FlowPanel createAlertContainer() {
        FlowPanel panel = new FlowPanel();
        panel.getElement().getStyle().setPosition(Style.Position.ABSOLUTE);
        return panel;
    }
}
