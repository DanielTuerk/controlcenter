package net.wbz.moba.controlcenter.web.client.model.track;

import com.google.common.base.Strings;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import net.wbz.moba.controlcenter.web.client.TrackUtils;
import net.wbz.moba.controlcenter.web.client.editor.track.EditTrackWidgetHandler;
import net.wbz.moba.controlcenter.web.shared.track.model.Configuration;
import net.wbz.moba.controlcenter.web.shared.track.model.GridPosition;
import net.wbz.moba.controlcenter.web.shared.track.model.TrackPart;
import org.gwtbootstrap3.client.ui.*;
import org.gwtbootstrap3.client.ui.constants.FormType;
import org.gwtbootstrap3.extras.select.client.ui.Option;
import org.gwtbootstrap3.extras.select.client.ui.Select;
import org.vectomatic.dom.svg.OMSVGDocument;
import org.vectomatic.dom.svg.OMSVGSVGElement;
import org.vectomatic.dom.svg.utils.OMSVGParser;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
abstract public class AbstractSvgTrackWidget<T extends TrackPart> extends SimplePanel implements EditTrackWidgetHandler {

    public static final String CSS_WIDGET_DISABLED = "widget-disabled";
    /**
     * Model for the widget.
     */
    private T trackPart = null;

    public static final String ID_FORM_ADDRESS = "formAddress";
    private static final String ID_FORM_BIT = "formBit";
    private Select selectBit;
    private TextBox txtAddress;

    private final OMSVGDocument svgDocument = OMSVGParser.currentDocument();
    private final OMSVGSVGElement svgRootElement;
    private boolean enabled;

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

        addSvgContent(svgDocument, svgRootElement);

        setEnabled(false);

        getElement().appendChild(svgRootElement.getElement());
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
    public Map<String, Configuration> getStoredWidgetFunctionConfigs() {
        Map<String, Configuration> functionConfigs = new HashMap<>();
        Configuration configuration = new Configuration();
        configuration.setBus(1); //TODO
        configuration.setAddress(trackPart.getDefaultToggleFunctionConfig().getAddress());
        configuration.setBit(trackPart.getDefaultToggleFunctionConfig().getBit());
        configuration.setBitState(true);
        functionConfigs.put(TrackPart.DEFAULT_TOGGLE_FUNCTION, configuration);
        return functionConfigs;
    }

    /**
     * TODO: refactor to config handler
     *
     * @return
     */
    protected void initFromTrackPart(T trackPart) {
        this.trackPart = trackPart;

        setTitle(trackPart.getDefaultToggleFunctionConfig().toString());
    }

    @Override
    public Widget getDialogContent() {
        Form form = new Form();
        form.setType(FormType.HORIZONTAL);

        FieldSet fieldSet = new FieldSet();
        // module address
        FormGroup groupModuleAddress = new FormGroup();
        FormLabel lblAddress = new FormLabel();
        lblAddress.setText("Address");
        lblAddress.setFor(ID_FORM_ADDRESS);
        groupModuleAddress.add(lblAddress);

        txtAddress = new TextBox();
        txtAddress.setId(ID_FORM_ADDRESS);
        if (trackPart.getDefaultToggleFunctionConfig().getAddress() >= 0) {
            txtAddress.setText(String.valueOf(trackPart.getDefaultToggleFunctionConfig().getAddress()));
        }
        groupModuleAddress.add(txtAddress);
        fieldSet.add(groupModuleAddress);

        // module bit
        FormGroup groupBit = new FormGroup();
        FormLabel lblBit = new FormLabel();
        lblBit.setFor(ID_FORM_BIT);
        lblBit.setText("Bit");
        groupBit.add(lblBit);

        selectBit = new Select();
        selectBit.setId(ID_FORM_BIT);
        for (int index = 1; index < 9; index++) {
            Option option = new Option();
            String value = String.valueOf(index);
            option.setValue(value);
            option.setText(value);
            selectBit.add(option);
            if (index == trackPart.getDefaultToggleFunctionConfig().getBit()) {
                selectBit.setValue(option);
            }
        }
        groupBit.add(selectBit);
        fieldSet.add(groupBit);

        form.add(fieldSet);
        return form;
    }

    @Override
    public void onConfirmCallback() {
        trackPart.getDefaultToggleFunctionConfig().setAddress(Integer.parseInt(txtAddress.getText()));
        trackPart.getDefaultToggleFunctionConfig().setBit(Integer.parseInt(selectBit.getValue()));
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
    public TrackPart getTrackPart(Widget containerWidget, int zoomLevel) {
        trackPart.setGridPosition(getGridPosition(containerWidget, zoomLevel));
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

    public AbsoluteTrackPosition getTrackPosition(GridPosition gridPosition, int zoomLevel) {
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
    public GridPosition getGridPosition(Widget containerWidget, int zoomLevel) {
        return new GridPosition(
                TrackUtils.getXFromLeftPosition(getAbsoluteLeft() - containerWidget.getAbsoluteLeft(), zoomLevel),
                TrackUtils.getYFromTopPosition(getAbsoluteTop() - containerWidget.getAbsoluteTop(), zoomLevel));
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
}
