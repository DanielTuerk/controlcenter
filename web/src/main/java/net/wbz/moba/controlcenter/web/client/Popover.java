package net.wbz.moba.controlcenter.web.client;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.*;
import com.google.gwt.user.client.ui.*;
import net.wbz.moba.controlcenter.web.client.viewer.track.TrackViewerPanel;
import org.gwtbootstrap3.client.ui.Heading;
import org.gwtbootstrap3.client.ui.constants.HeadingSize;
import org.gwtbootstrap3.client.ui.gwt.FlowPanel;

/**
 * Custom popover implementation to show widgets in the content of the popover.
 * Only the style classes are used from the {@link org.gwtbootstrap3.client.ui.Popover}.
 *
 * @author Daniel Tuerk
 */
public class Popover extends FlowPanel {

    private final Widget targetWidget;
    private final FlowPanel contentPanel;
    private SimplePanel arrowPanel;

    private Element trackViewerPanelElement = null;
    private String parentContainerId;

    /**
     * Create a new instance for the given target widget.
     *
     * @param targetWidget {@link com.google.gwt.user.client.ui.Widget} to center the position of the popover
     */
    public Popover(String parentContainerId, Widget targetWidget) {
        this(parentContainerId, targetWidget, null);
    }

    /**
     * Create a new instance for the given target widget.
     *
     * @param targetWidget {@link com.google.gwt.user.client.ui.Widget} to center the position of the popover
     * @param title        {@link java.lang.String} additional title in the popover
     */
    public Popover(String parentContainerId, Widget targetWidget, String title) {
        this.parentContainerId = parentContainerId;
        this.targetWidget = targetWidget;

        addStyleName("popover fade in");
        getElement().getStyle().setDisplay(Style.Display.BLOCK);


        arrowPanel = new SimplePanel();
        arrowPanel.addStyleName("arrow");
        add(arrowPanel);

        if (title != null) {
            Heading heading = new Heading(HeadingSize.H3);
            heading.addStyleName("popover-title");
            heading.setText(title);
            add(heading);
        }

        contentPanel = new FlowPanel();
        contentPanel.addStyleName("popover-content");
        contentPanel.getElement().getStyle().setPadding(5, Style.Unit.PX);
        add(contentPanel);
    }

    /**
     * Adding the {@link com.google.gwt.user.client.ui.Widget}s as content to the popover.
     *
     * @param widgets components as content
     */
    public void addContent(Widget... widgets) {
        for (Widget w : widgets) {
            w.getElement().getStyle().setDisplay(Style.Display.BLOCK);
            contentPanel.add(w);
        }
    }

    @Override
    protected void onLoad() {
        super.onLoad();
    }

    private Panel getWidget(Element element) {
        com.google.gwt.user.client.EventListener listener = DOM.getEventListener((com.google.gwt.user.client.Element) element);
        // No listener attached to the element, so no widget exist for this
        // element
        if (listener == null) {
            return null;
        }
        if (listener instanceof Panel) {
            // GWT uses the widget as event listener
            return (Panel) listener;
        }
        return null;
    }

    /**
     * Open the popover on the left of the target widget.
     */
    public void show() {
        if (!isShowing()) {

            DOM.sinkEvents(this.getElement(), Event.ONCLICK);
            trackViewerPanelElement = DOM.getElementById(parentContainerId);
            getWidget(trackViewerPanelElement).add(this);

            arrowPanel.getElement().getStyle().setTop(50, Style.Unit.PCT);

            // calc left position of popover
            int targetWidgetLeft = trackViewerPanelElement.getScrollLeft() + targetWidget.getAbsoluteLeft();
            int trackViewerPanelElementAbsoluteLeft = trackViewerPanelElement.getAbsoluteLeft();
            int offsetWidth = targetWidget.getOffsetWidth();
            int left = targetWidgetLeft - trackViewerPanelElementAbsoluteLeft + offsetWidth;

            // correct left by overlapping the container
            if (left + getOffsetWidth() > trackViewerPanelElement.getClientWidth()) {
                removeStyleName("right");
                addStyleName("left");
                left = targetWidgetLeft - trackViewerPanelElement.getAbsoluteLeft() - getOffsetWidth();
            } else {
                removeStyleName("left");
                addStyleName("right");
            }
            getElement().getStyle().setLeft((double) left, Style.Unit.PX);

            // calc top position of popover
            int targetWidgetTop = targetWidget.getAbsoluteTop() - trackViewerPanelElement.getAbsoluteTop();
            int trackViewerPanelElementOffsetHeight = trackViewerPanelElement.getOffsetHeight();

            int targetWidgetHalfHeight = targetWidget.getOffsetHeight() / 2;
            double top = targetWidgetTop + targetWidgetHalfHeight -
                    (getOffsetHeight() / 2);

            // correct top by overlapping the container and adjust the arrow to the target widget
            if (top < trackViewerPanelElement.getAbsoluteTop()) {
                top = trackViewerPanelElement.getOffsetTop();

                arrowPanel.getElement().getStyle().setTop(
                        targetWidget.getAbsoluteTop() - trackViewerPanelElement.getAbsoluteTop()
                                + targetWidgetHalfHeight, Style.Unit.PX);
            } else if (top + getOffsetHeight() > trackViewerPanelElementOffsetHeight) {
                top = targetWidgetTop + targetWidget.getOffsetHeight() + trackViewerPanelElement.getScrollTop() -
                        getOffsetHeight();

                arrowPanel.getElement().getStyle().setTop(targetWidget.getAbsoluteTop() + targetWidgetHalfHeight + 25
                        - top, Style.Unit.PX);
            }
            getElement().getStyle().setTop(top, Style.Unit.PX);

        }
    }

    /**
     * @return <code>true</code> if it's showing
     */
    public boolean isShowing() {
        return trackViewerPanelElement != null;
    }

    /**
     * Hide the popover.
     */
    public void hide() {
        if (isShowing()) {
            getWidget(trackViewerPanelElement).remove(this);
            trackViewerPanelElement = null;
        }
    }

    /**
     * Toggle visibility.
     */
    public void toggle() {
        if (isShowing()) {
            hide();
        } else {
            show();
        }
    }
}
