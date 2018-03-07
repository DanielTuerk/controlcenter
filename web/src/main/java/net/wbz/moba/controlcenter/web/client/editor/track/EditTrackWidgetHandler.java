package net.wbz.moba.controlcenter.web.client.editor.track;

import com.google.gwt.user.client.ui.Widget;

/**
 * @author Daniel Tuerk
 */
public interface EditTrackWidgetHandler {

    Widget getDialogContent();

    void onConfirmCallback();
}
