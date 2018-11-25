package net.wbz.moba.controlcenter.web.client.editor.track;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * TODO refactoring, track is build in 2  implementations
 * @author Daniel Tuerk
 */
abstract public class AbstractTrackPanel extends AbsolutePanel {

    private final boolean fixedSize;

    public AbstractTrackPanel() {
        this(false);
    }

    public AbstractTrackPanel(boolean fixedSize) {
        this.fixedSize = fixedSize;
        addStyleName("boundary");
        getElement().getStyle().setOverflow(Style.Overflow.AUTO);
    }

    protected boolean isFixedSize() {
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
