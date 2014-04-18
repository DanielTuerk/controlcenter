package net.wbz.moba.controlcenter.web.client.editor.track;

import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
public interface EditTrackWidgetHandler {

    public VerticalPanel getDialogContent();

    public void onConfirmCallback();
}
