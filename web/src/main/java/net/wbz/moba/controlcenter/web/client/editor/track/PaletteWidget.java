package net.wbz.moba.controlcenter.web.client.editor.track;

import com.allen_sauer.gwt.dnd.client.HasDragHandle;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Widget;
import net.wbz.moba.controlcenter.web.client.RequestUtils;
import net.wbz.moba.controlcenter.web.client.model.track.AbstractSvgTrackWidget;

/**
 * Widget wrapper class used by {@link PalettePanel}.
 */
public class PaletteWidget extends AbsolutePanel implements HasDragHandle {

    private FocusPanel shim = new FocusPanel();

    private final AbstractSvgTrackWidget widget;

    public FocusPanel getShim() {
        return shim;
    }

    public AbstractSvgTrackWidget getWidget() {
        return widget;
    }

    /**
     * Default constructor to wrap the provided widget.
     *
     * @param widget the widget to be wrapped
     */
    public PaletteWidget(AbstractSvgTrackWidget widget) {
        this.widget = widget;
        add(widget);
    }

    public AbstractSvgTrackWidget getPaletteWidgetItem() {
        return widget;
    }

    public PaletteWidget cloneWidget() {
        Widget clone = widget.getClone(widget.getNewTrackPart(RequestUtils.getInstance().getTrackEditorRequest()));

        // Copy a few obvious common widget properties
        clone.setStyleName(widget.getStyleName());
        clone.setTitle(widget.getTitle());

        // Wrap the cloned widget in a new PaletteWidget instance
        return new EditorPaletteWidget((AbstractSvgTrackWidget) clone);
    }

    @Override
    public Widget getDragHandle() {
        return shim;
    }

    /**
     * Let shim size match our size.
     *
     * @param width  the desired pixel width
     * @param height the desired pixel height
     */
    @Override
    public void setPixelSize(int width, int height) {
        super.setPixelSize(width, height);
        shim.setPixelSize(width, height);
    }

    /**
     * Let shim size match our size.
     *
     * @param width  the desired CSS width
     * @param height the desired CSS height
     */
    @Override
    public void setSize(String width, String height) {
        super.setSize(width, height);
        shim.setSize(width, height);
    }

    /**
     * Adjust the shim size and attach once our widget dimensions are known.
     */
    @Override
    protected void onLoad() {
        super.onLoad();
        shim.setPixelSize(getOffsetWidth(), getOffsetHeight());
        add(shim, 0, 0);
    }

    /**
     * Remove the shim to allow the widget to size itself when reattached.
     */
    @Override
    protected void onUnload() {
        super.onUnload();
        shim.removeFromParent();
    }

}
