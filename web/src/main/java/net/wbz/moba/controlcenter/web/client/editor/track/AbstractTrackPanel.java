package net.wbz.moba.controlcenter.web.client.editor.track;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
abstract public class AbstractTrackPanel extends AbsolutePanel {

    protected AbstractTrackPanel() {
        addStyleName("contentPanel");
        setTitle("abstractTrackPanel");
        getElement().getStyle().setOverflow(Style.Overflow.AUTO);
    }

    @Override
    protected void onLoad() {
        super.onLoad();
    }

    public int getZoomLevel() {
        return 0;
    }

    abstract public void addTrackWidget(Widget widget, int left, int top);
}
