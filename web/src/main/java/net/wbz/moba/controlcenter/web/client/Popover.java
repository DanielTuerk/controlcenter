package net.wbz.moba.controlcenter.web.client;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
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
    private boolean showing = false;

    /**
     * Create a new instance for the given target widget.
     *
     * @param targetWidget {@link com.google.gwt.user.client.ui.Widget} to center the position of the popover
     */
    public Popover(Widget targetWidget) {
        this(targetWidget, null);
    }

    /**
     * Create a new instance for the given target widget.
     *
     * @param targetWidget {@link com.google.gwt.user.client.ui.Widget} to center the position of the popover
     * @param title {@link java.lang.String} additional title in the popover
     */
    public Popover(Widget targetWidget, String title) {
        this.targetWidget = targetWidget;

        addStyleName("popover fade right in");
        getElement().getStyle().setDisplay(Style.Display.BLOCK);


        SimplePanel arrowPanel = new SimplePanel();
        arrowPanel.addStyleName("arrow");
        add(arrowPanel);

        if (title != null) {
            Heading heading = new Heading(HeadingSize.H3);
            heading.addStyleName("popover-title");
            heading.setText("test");
            add(heading);
        }

        contentPanel = new FlowPanel();
        contentPanel.addStyleName("popover-content");
        contentPanel.getElement().getStyle().setPadding(5, Style.Unit.PX);
        add(contentPanel);
    }

    /**
     * Adding the {@link com.google.gwt.user.client.ui.Widget}s as content to the popover.
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

    /**
     * Open the popover on the left of the target widget.
     */
    public void show() {
        if (!isShowing()) {
            showing = true;
            int left = targetWidget.getElement().getAbsoluteLeft() + targetWidget.getOffsetWidth();
            getElement().getStyle().setLeft((double) left, Style.Unit.PX);

            double top = targetWidget.getElement().getAbsoluteTop() + targetWidget.getOffsetHeight() - (getOffsetHeight() / 2);
            getElement().getStyle().setTop(top >= 0 ? top : 0, Style.Unit.PX);

            RootPanel.get().add(this);
        }
    }

    /**
     * @return <code>true</code> if it's showing
     */
    public boolean isShowing() {
        return showing;
    }

    /**
     * Hide the popover.
     */
    public void hide() {
        if (isShowing()) {
            showing = false;
            RootPanel.get().remove(this);
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
