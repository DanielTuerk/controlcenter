package net.wbz.moba.controlcenter.web.client.model.track;

import com.google.common.base.Strings;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import net.wbz.moba.controlcenter.web.client.ServiceUtils;
import net.wbz.moba.controlcenter.web.client.TrackUtils;
import net.wbz.moba.controlcenter.web.client.editor.track.EditTrackWidgetHandler;
import net.wbz.moba.controlcenter.web.shared.track.model.Configuration;
import net.wbz.moba.controlcenter.web.shared.track.model.ConfigurationProxy;
import net.wbz.moba.controlcenter.web.shared.track.model.GridPosition;
import net.wbz.moba.controlcenter.web.shared.track.model.GridPositionProxy;
import net.wbz.moba.controlcenter.web.shared.track.model.TrackPart;
import net.wbz.moba.controlcenter.web.shared.track.model.TrackPartProxy;
import org.gwtbootstrap3.client.ui.*;
import org.gwtbootstrap3.client.ui.constants.FormType;
import org.vectomatic.dom.svg.OMSVGDocument;
import org.vectomatic.dom.svg.OMSVGSVGElement;
import org.vectomatic.dom.svg.utils.OMSVGParser;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Daniel Tuerk
 */
abstract public class AbstractSvgTrackWidget<T extends TrackPartProxy> extends SimplePanel implements EditTrackWidgetHandler {

    public static final String CSS_WIDGET_DISABLED = "widget-disabled";

    /**
     * Model for the widget.
     */
    private T trackPart = null;

    public static final String ID_FORM_ADDRESS = "formAddress";


    private final OMSVGDocument svgDocument = OMSVGParser.currentDocument();
    private final OMSVGSVGElement svgRootElement;
    private boolean enabled;
    private TabContent dialogContentTabContent;
    private NavTabs dialogContentNavTabs;

    public AbstractSvgTrackWidget() {
        addStyleName("widget_track");

        String additionalStyle = getTrackWidgetStyleName();
        if (!Strings.isNullOrEmpty(additionalStyle)) {
            addStyleName(additionalStyle);
        }

        // Create the root svg element
        svgRootElement = svgDocument.createSVGSVGElement();
        svgRootElement.setWidth(Style.Unit.PX, 25);
        svgRootElement.setHeight(Style.Unit.PX, 25);

        setEnabled(false);
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        addSvgContent(svgDocument, svgRootElement);
        getElement().appendChild(svgRootElement.getElement());
    }

    @Override
    protected void onUnload() {
        super.onUnload();
        getElement().removeChild(svgRootElement.getElement());
    }

    /**
     * Add the SVG content for the track part to the given {@link org.vectomatic.dom.svg.OMSVGSVGElement}.
     *
     * @param doc {@link org.vectomatic.dom.svg.OMSVGDocument}
     * @param svg {@link org.vectomatic.dom.svg.OMSVGSVGElement}
     */
    abstract protected void addSvgContent(OMSVGDocument doc, OMSVGSVGElement svg);

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
     *
     * @return
     */
    public Map<String, ConfigurationProxy> getStoredWidgetFunctionConfigs() {
        return new HashMap<>();
    }

    /**
     * TODO: refactor to config handler
     *
     * @return
     */
    protected void initFromTrackPart(T trackPart) {
        this.trackPart = trackPart;
    }

    abstract public void updateFunctionState(Configuration configuration, boolean state);

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

    @Override
    public void onConfirmCallback() {
    }

    /**
     * Check responsibility for the given {@link net.wbz.moba.controlcenter.web.shared.track.model.TrackPart}.
     *
     * @param trackPart {@link net.wbz.moba.controlcenter.web.shared.track.model.TrackPart}
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
     * Return the {@link net.wbz.moba.controlcenter.web.shared.track.model.TrackPart} of the widget with actual grid
     * position.
     *
     * @param containerWidget {@link com.google.gwt.user.client.ui.Widget} parent container
     * @param zoomLevel       level of zoom
     * @return {@link net.wbz.moba.controlcenter.web.shared.track.model.TrackPart}
     * @see {#getGridPosition}
     */
    public TrackPartProxy getTrackPart(Widget containerWidget, int zoomLevel) {
        long gridPositionIdOfExistingGridPos = -1;
        if (trackPart.getGridPosition() != null) {
            gridPositionIdOfExistingGridPos = trackPart.getGridPosition().getId();
        }

        trackPart.setGridPosition(getGridPosition(containerWidget, zoomLevel));

        if (gridPositionIdOfExistingGridPos >= 0) {
            trackPart.getGridPosition().setId(gridPositionIdOfExistingGridPos);
        }
        return trackPart;
    }

    /**
     * @return {@link T} model from initial call of
     * {@link net.wbz.moba.controlcenter.web.client.model.track.AbstractSvgTrackWidget#initFromTrackPart}.
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
     * Create new instance for the given {@link net.wbz.moba.controlcenter.web.shared.track.model.TrackPart}.
     *
     * @param trackPart {@link net.wbz.moba.controlcenter.web.shared.track.model.TrackPart}
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

    public AbsoluteTrackPosition getTrackPosition(GridPositionProxy gridPosition, int zoomLevel) {
        return new AbsoluteTrackPosition(TrackUtils.getLeftPositionFromX(gridPosition.getX(), zoomLevel),
                TrackUtils.getTopPositionFromY(gridPosition.getY(), zoomLevel));
    }

    /**
     * Create and return the {@link net.wbz.moba.controlcenter.web.shared.track.model.GridPosition} of this track
     * part from the absolute position of the container.
     *
     * @param containerWidget {@link com.google.gwt.user.client.ui.Widget} parent container
     * @param zoomLevel       level of zoom
     * @return {@link net.wbz.moba.controlcenter.web.shared.track.model.GridPosition}
     */
    public GridPositionProxy getGridPosition(Widget containerWidget, int zoomLevel) {
        //TODO
        return null;
//        return new GridPosition(
//                TrackUtils.getXFromLeftPosition(getAbsoluteLeft() - containerWidget.getAbsoluteLeft(), zoomLevel),
//                TrackUtils.getYFromTopPosition(getAbsoluteTop() - containerWidget.getAbsoluteTop(), zoomLevel));
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (!enabled) {
            addStyleName(CSS_WIDGET_DISABLED);
        } else {
            removeStyleName(CSS_WIDGET_DISABLED);
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    /**
     * TODO: to avoid same model of clone - palette
     *
     * @return
     */
    abstract public T getNewTrackPart();
}
