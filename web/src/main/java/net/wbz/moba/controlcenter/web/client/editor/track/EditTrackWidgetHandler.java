package net.wbz.moba.controlcenter.web.client.editor.track;

import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Daniel Tuerk
 */
public interface EditTrackWidgetHandler {

    public Widget getDialogContent();

    public void onConfirmCallback();
}
