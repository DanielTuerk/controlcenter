package net.wbz.moba.controlcenter.web.client.editor.track;

import net.wbz.moba.controlcenter.web.shared.track.model.Configuration;

/**
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
public interface ClickActionViewerWidgetHandler {

    public void onClick();

    public void updateFunctionState(Configuration configuration, boolean state);
}
