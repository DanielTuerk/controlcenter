package net.wbz.moba.controlcenter.web.client.editor.track;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Panel for the track.
 *
 * @author Daniel Tuerk
 */
abstract public class AbstractTrackPanel extends AbsolutePanel {

    private final boolean fixedSize;

    protected AbstractTrackPanel() {
        this(false);
    }

    AbstractTrackPanel(boolean fixedSize) {
        this.fixedSize = fixedSize;
        addStyleName("boundary");
        getElement().getStyle().setOverflow(Style.Overflow.AUTO);
    }

    boolean isFixedSize() {
        return fixedSize;
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
