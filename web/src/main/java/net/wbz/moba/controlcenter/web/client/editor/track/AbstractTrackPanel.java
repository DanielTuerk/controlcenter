package net.wbz.moba.controlcenter.web.client.editor.track;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Daniel Tuerk (daniel.tuerk@jambit.com)
 */
abstract public class AbstractTrackPanel extends AbsolutePanel {

    public int getZoomLevel() {
        return 0;
    }

    abstract public void addTrackWidget(Widget widget, int left, int top);
}
