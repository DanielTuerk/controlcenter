package net.wbz.moba.controlcenter.web.client.model.track;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import net.wbz.moba.controlcenter.web.client.editor.track.EditTrackWidgetHandler;
import net.wbz.moba.controlcenter.web.client.editor.track.TrackEditorContainer;
import net.wbz.moba.controlcenter.web.client.util.SvgTrackUtil;
import net.wbz.moba.controlcenter.web.shared.track.model.AbstractTrackPart;
import net.wbz.moba.controlcenter.web.shared.track.model.GridPosition;
import org.gwtbootstrap3.client.ui.Form;
import org.gwtbootstrap3.client.ui.NavTabs;
import org.gwtbootstrap3.client.ui.TabContent;
import org.gwtbootstrap3.client.ui.TabListItem;
import org.gwtbootstrap3.client.ui.TabPane;
import org.gwtbootstrap3.client.ui.TabPanel;
import org.gwtbootstrap3.client.ui.constants.FormType;
import org.vectomatic.dom.svg.OMSVGDocument;
import org.vectomatic.dom.svg.OMSVGSVGElement;
import org.vectomatic.dom.svg.utils.OMSVGParser;

/**
 * TODO EditTrackWidgetHandler nur fuer widget mit edit content. Der basis dialog wird hier erstellt, aber interface nur an die widgets, die es brauchen
 *
 * @author Daniel Tuerk
 */
abstract public class AbstractSvgTrackWidget<T extends AbstractTrackPart> extends SimplePanel
    implements EditTrackWidgetHandler {

    private static final String CSS_WIDGET_DISABLED = "widget-disabled";
    public static final int WIDGET_WIDTH = 25;
    public static final int WIDGET_HEIGHT = 25;
    private final OMSVGDocument svgDocument;
    private final OMSVGSVGElement svgRootElement;
    /**
     * Model for the widget.
     */
    private T trackPart = null;
    private boolean enabled;
    private TabContent dialogContentTabContent;
    private NavTabs dialogContentNavTabs;
    /**
     * Color of the widget.
     */
    private String color = SvgTrackUtil.DEFAULT_TRACK_COLOR;

    public AbstractSvgTrackWidget() {
        addStyleName("widget_track");

        svgDocument = OMSVGParser.currentDocument();
        // Create the root svg element
        svgRootElement = svgDocument.createSVGSVGElement();

        setEnabled(false);
    }

    /**
     * Repaint the SVG content.
     */
    public void repaint() {
        clearSvgContent();
        initSvg();
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    protected void onLoad() {
        super.onLoad();

        getElement().appendChild(svgRootElement.getElement());

        initSvg();
    }

    private void initSvg() {
        int widgetWidth = getWidgetWidth();
        int widgetHeight = getWidgetHeight();

        svgRootElement.setWidth(Unit.PX, widgetWidth);
        svgRootElement.setHeight(Unit.PX, widgetHeight);

        addSvgContent(svgDocument, svgRootElement, color);
        setWidth(widgetWidth + "px");
        setHeight(widgetHeight + "px");
    }

    @Override
    protected void onUnload() {
        super.onUnload();
        getElement().removeChild(svgRootElement.getElement());
    }

    /**
     * Add the SVG content for the track part to the given {@link org.vectomatic.dom.svg.OMSVGSVGElement}.
     *  @param doc {@link OMSVGDocument}
     * @param svg {@link OMSVGSVGElement}
     * @param color SVG color for content
     */
    abstract protected void addSvgContent(OMSVGDocument doc, OMSVGSVGElement svg, String color );

    /**
     * @return width in pixel for the widget
     */
    protected int getWidgetWidth() {
        return AbstractSvgTrackWidget.WIDGET_WIDTH;
    }

    /**
     * @return height in pixel for the widget
     */
    protected int getWidgetHeight() {
        return AbstractSvgTrackWidget.WIDGET_HEIGHT;
    }

    /**
     * Clear the SVG content from the widget.
     */
    protected void clearSvgContent() {
        for (int i = svgRootElement.getChildNodes().getLength() - 1; i >= 0; i--) {
            svgRootElement.removeChild(svgRootElement.getChildNodes().getItem(i));
        }
    }

    protected OMSVGDocument getSvgDocument() {
        return svgDocument;
    }

    protected OMSVGSVGElement getSvgRootElement() {
        return svgRootElement;
    }

    /**
     * TODO: refactor to config handler
     */
    protected void initFromTrackPart(T trackPart) {
        this.trackPart = trackPart;
    }

    @Override
    public Widget getDialogContent() {
        TabPanel tabPanel = new TabPanel();
        dialogContentNavTabs = new NavTabs();
        dialogContentTabContent = new TabContent();
        tabPanel.add(dialogContentNavTabs);
        tabPanel.add(dialogContentTabContent);

        Form form = new Form();
        form.setType(FormType.HORIZONTAL);
        form.add(tabPanel);

        return form;
    }

    /**
     * Create and add a new tab for the edit mode.
     *
     * @param title {@link String} title of tab
     * @param content {@link Widget} content for the tab
     */
    protected void addDialogContentTab(String title, Widget content) {
        TabPane tabPane = new TabPane();
        tabPane.setActive(dialogContentTabContent.getWidgetCount() == 0);

        TabListItem tabListItem = new TabListItem(title);
        tabListItem.setDataTargetWidget(tabPane);
        tabListItem.setActive(dialogContentTabContent.getWidgetCount() == 0);

        tabPane.add(content);
        dialogContentTabContent.add(tabPane);
        dialogContentNavTabs.add(tabListItem);
    }

    @Override
    public void onConfirmCallback() {
        // TODO edit container needed for every widget? since no more track block for every one
        // default callback to do nothing
    }

    /**
     * Check responsibility for the given {@link AbstractTrackPart}.
     *
     * @param trackPart {@link AbstractTrackPart}
     * @return {@link net.wbz.moba.controlcenter.web.client.model.track.AbstractSvgTrackWidget}
     */
    abstract public boolean isRepresentationOf(T trackPart);

    /**
     * CSS class name for the implementation.
     *
     * @return {@link java.lang.String}
     */
    abstract public String getTrackWidgetStyleName();

    /**
     * Return the {@link AbstractTrackPart} of the widget with actual grid position.
     *
     * @param containerWidget {@link Widget} parent container
     * @param zoomLevel level of zoom
     * @return {@link AbstractTrackPart}
     * @see #getGridPosition
     */
    public AbstractTrackPart getTrackPart(Widget containerWidget, int zoomLevel) {
        if (trackPart.getGridPosition() == null) {
            trackPart.setGridPosition(new GridPosition());
        }
        GridPosition gridPositionFromPanel = getGridPosition(containerWidget, zoomLevel);
        trackPart.getGridPosition().setX(gridPositionFromPanel.getX());
        trackPart.getGridPosition().setY(gridPositionFromPanel.getY());
        return trackPart;
    }

    /**
     * @return {@link T} model from initial call of {@link net.wbz.moba.controlcenter.web.client.model.track.AbstractSvgTrackWidget#initFromTrackPart}.
     */
    public T getTrackPart() {
        return trackPart;
    }

    /**
     * Title of the widget in the palette of the editor.
     *
     * @return {@link java.lang.String}
     */
    abstract public String getPaletteTitle();

    /**
     * Create new instance for the given {@link AbstractTrackPart}.
     *
     * @param trackPart {@link AbstractTrackPart}
     * @return {@link net.wbz.moba.controlcenter.web.client.model.track.AbstractSvgTrackWidget}
     */
    public AbstractSvgTrackWidget<T> getClone(T trackPart) {
        AbstractSvgTrackWidget<T> clone = getClone();
        clone.initFromTrackPart(trackPart);
        return clone;
    }

    /**
     * New instance of the implementation.
     *
     * @return {@link net.wbz.moba.controlcenter.web.client.model.track.AbstractSvgTrackWidget}
     */
    protected abstract AbstractSvgTrackWidget<T> getClone();

    private static int getTopPositionFromY(int y, int zoomLevel) {
        return (TrackEditorContainer.DRAGGABLE_OFFSET_HEIGHT + (zoomLevel * TrackEditorContainer.ZOOM_STEP)) * y;
    }

    private static int getLeftPositionFromX(int x, int zoomLevel) {
        return (TrackEditorContainer.DRAGGABLE_OFFSET_WIDTH + (zoomLevel * TrackEditorContainer.ZOOM_STEP)) * x;
    }

    protected boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (!enabled) {
            addStyleName(CSS_WIDGET_DISABLED);
        } else {
            removeStyleName(CSS_WIDGET_DISABLED);
        }
    }

    /**
     * TODO: to avoid same model of clone - palette
     *
     * @return T copy
     */
    abstract public T getNewTrackPart();

    public String getConfigurationInfo() {
        return "[ " + getTrackPart().getId() + " ]";
    }

    private static int getYFromTopPosition(int topPos, int zoomLevel) {
        return topPos / TrackEditorContainer.DRAGGABLE_OFFSET_HEIGHT + (zoomLevel * TrackEditorContainer.ZOOM_STEP);
    }

    private static int getXFromLeftPosition(int leftPos, int zoomLevel) {
        return leftPos / (TrackEditorContainer.DRAGGABLE_OFFSET_WIDTH + (zoomLevel * TrackEditorContainer.ZOOM_STEP));
    }

    public AbsoluteTrackPosition getTrackPosition(GridPosition gridPosition, int zoomLevel) {
        return new AbsoluteTrackPosition(getLeftPositionFromX(gridPosition.getX(), zoomLevel),
            getTopPositionFromY(gridPosition.getY(), zoomLevel));
    }

    /**
     * Create and return the {@link net.wbz.moba.controlcenter.web.shared.track.model.GridPosition} of this track part
     * from the absolute position of the container.
     *
     * @param containerWidget {@link Widget} parent container
     * @param zoomLevel level of zoom
     * @return {@link net.wbz.moba.controlcenter.web.shared.track.model.GridPosition}
     */
    public GridPosition getGridPosition(Widget containerWidget, int zoomLevel) {
        return new GridPosition(
            getXFromLeftPosition(getAbsoluteLeft() - containerWidget.getAbsoluteLeft(), zoomLevel),
            getYFromTopPosition(getAbsoluteTop() - containerWidget.getAbsoluteTop(), zoomLevel));
    }
}
