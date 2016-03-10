package net.wbz.moba.controlcenter.web.client.editor.track;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Daniel Tuerk
 */
public class MyPickupDragController extends PickupDragController {
    /**
     * Create a new pickup-and-move style drag controller. Allows widgets or a suitable proxy to be
     * temporarily picked up and moved around the specified boundary panel.
     * <p/>
     * <p>
     * Note: An implicit {@link com.allen_sauer.gwt.dnd.client.drop.BoundaryDropController} is created and registered automatically.
     * </p>
     *
     * @param boundaryPanel                the desired boundary panel or <code>RootPanel.get()</code> (read
     *                                     http://code.google.com/p/gwt-dnd/wiki/GettingStarted) if entire document body is to be
     *                                     the boundary
     * @param allowDroppingOnBoundaryPanel whether or not boundary panel should allow dropping
     */
    public MyPickupDragController(AbsolutePanel boundaryPanel, boolean allowDroppingOnBoundaryPanel) {
        super(boundaryPanel, allowDroppingOnBoundaryPanel);
    }

    @Override
    public void toggleSelection(Widget draggable) {
        super.toggleSelection(draggable);    //To change body of overridden methods use File | Settings | File Templates.
    }
}
