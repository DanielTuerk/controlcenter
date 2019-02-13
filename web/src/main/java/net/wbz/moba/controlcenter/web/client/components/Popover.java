package net.wbz.moba.controlcenter.web.client.components;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Heading;
import org.gwtbootstrap3.client.ui.gwt.FlowPanel;

/**
 * Custom popover implementation to show widgets in the content of the popover. Only the style classes are used from the
 * {@link org.gwtbootstrap3.client.ui.Popover}.
 *
 * @author Daniel Tuerk
 */
public class Popover extends Composite {

    private static Binder UI_BINDER = GWT.create(Binder.class);
    private final String parentContainerId;
    private final Widget targetWidget;
    @UiField
    FlowPanel contentPanel;
    @UiField
    SimplePanel arrowPanel;
    @UiField
    Heading heading;
    private Element parentContainerElement = null;

    /**
     * Create a new instance for the given target widget.
     *
     * @param parentContainerId id of the parent in which the popover should be displayed
     * @param targetWidget {@link Widget} to center the position of the popover
     */
    public Popover(String parentContainerId, Widget targetWidget) {
        this(parentContainerId, targetWidget, null);
    }

    /**
     * Create a new instance for the given target widget.
     *
     * @param parentContainerId id of the parent in which the popover should be displayed
     * @param targetWidget {@link Widget} to center the position of the popover
     * @param title {@link java.lang.String} additional title in the popover
     */
    public Popover(String parentContainerId, Widget targetWidget, String title) {
        initWidget(UI_BINDER.createAndBindUi(this));

        this.parentContainerId = parentContainerId;
        this.targetWidget = targetWidget;

        if (title != null) {
            heading.setText(title);
        } else {
            heading.setVisible(false);
        }
    }

    /**
     * Open the popover besides of the target widget. As default is't displayed at the left side if there is enough
     * space left. Otherwise it will be displayed at the right side.
     */
    public void show() {
        if (!isShowing()) {

            DOM.sinkEvents(this.getElement(), Event.ONCLICK);
            parentContainerElement = DOM.getElementById(parentContainerId);
            Panel parentContainerWidget = getWidget(parentContainerElement);
            if (parentContainerWidget != null) {
                parentContainerWidget.add(this);

                arrowPanel.getElement().getStyle().setTop(50, Style.Unit.PCT);

                int left = calcLeft();
                getElement().getStyle().setLeft((double) left, Style.Unit.PX);

                double top = calcTop();
                getElement().getStyle().setTop(top, Style.Unit.PX);
            }
        }
    }

    /**
     * Calculation top position of popover.
     *
     * @return top position
     */
    private double calcTop() {
        double top = targetWidget.getElement().getOffsetTop() - (this.getOffsetHeight() / 2d);
        if (top < 0) {
            top = 0;
        } else if (top + getOffsetHeight() > parentContainerElement.getOffsetHeight()) {
            top -= top + getOffsetHeight() - parentContainerElement.getOffsetHeight();
        }

        // TODO now not working for signals
//        // correct top by overlapping the container and adjust the arrow to the target widget
//        if (top < parentContainerElement.getAbsoluteTop()) {
//            top = parentContainerElement.getOffsetTop();
//
//            arrowPanel.getElement().getStyle().setTop(
//                targetWidget.getAbsoluteTop() - parentContainerElement.getAbsoluteTop()
//                    + targetWidgetHalfHeight, Style.Unit.PX);
//        } else if (top + getOffsetHeight() > trackViewerPanelElementOffsetHeight) {
//            top = targetWidgetTop + targetWidget.getOffsetHeight() + parentContainerElement.getScrollTop() -
//                getOffsetHeight();
//
//            arrowPanel.getElement().getStyle()
//                .setTop(targetWidget.getAbsoluteTop() + targetWidgetHalfHeight + 25
//                    - top, Style.Unit.PX);
//        }
        return top;
    }

    /**
     * Calculate left position of popover.
     *
     * @return left position
     */
    private int calcLeft() {
        int targetWidgetLeft = parentContainerElement.getScrollLeft() + targetWidget.getAbsoluteLeft();
        int trackViewerPanelElementAbsoluteLeft = parentContainerElement.getAbsoluteLeft();
        int left = targetWidgetLeft - trackViewerPanelElementAbsoluteLeft + targetWidget.getOffsetWidth();

        // correct left by overlapping the container
        if (left + getOffsetWidth() > parentContainerElement.getClientWidth()) {
            removeStyleName("right");
            addStyleName("left");
            left = targetWidgetLeft - parentContainerElement.getAbsoluteLeft() - getOffsetWidth();
        } else {
            removeStyleName("left");
            addStyleName("right");
        }
        return left;
    }

    /**
     * Adding the {@link Widget}s as content to the popover.
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
        com.google.gwt.user.client.EventListener listener = DOM.getEventListener(element);
        // No listener attached to the element, so no widget exist for this element
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
     * Hide the popover.
     */
    public void hide() {
        if (isShowing()) {
            Panel widget = getWidget(parentContainerElement);
            if (widget != null) {
                widget.remove(this);
            }
            parentContainerElement = null;
        }
    }

    /**
     * @return <code>true</code> if it's showing
     */
    public boolean isShowing() {
        return parentContainerElement != null;
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

    interface Binder extends UiBinder<Widget, Popover> {

    }
}
