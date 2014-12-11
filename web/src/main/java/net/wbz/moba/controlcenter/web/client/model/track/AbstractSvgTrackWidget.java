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

/**
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
abstract public class AbstractSvgTrackWidget<T extends TrackPart> extends SimplePanel implements EditTrackWidgetHandler {

    public static final String ID_FORM_ADDRESS = "formAddress";
    private static final String ID_FORM_BIT = "formBit";
    private final Select selectBit = new Select();
    private final TextBox txtAddress = new TextBox();

    private int trackPartConfigAddress = -1;
    private int trackPartConfigBit = -1;

    private final OMSVGDocument svgDocument = OMSVGParser.currentDocument();
    private final OMSVGSVGElement svgRootElement;

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

        getElement().appendChild(svgRootElement.getElement());
    }

    abstract protected void addSvgContent(OMSVGDocument doc, OMSVGSVGElement svg);

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


    public Configuration getStoredWidgetConfiguration() {
        Configuration configuration = new Configuration();
        configuration.setAddress(trackPartConfigAddress);
        configuration.setOutput(trackPartConfigBit);
        return configuration;
    }

    protected void initFromTrackPart(T trackPart) {
        // TODO null - should never happen!
//        if (trackPart != null && trackPart.getConfiguration() != null) {
        trackPartConfigAddress = trackPart.getConfiguration().getAddress();
        trackPartConfigBit = trackPart.getConfiguration().getOutput();

        setTitle(trackPart.getConfiguration().toString());
//        }
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
        txtAddress.setId(ID_FORM_ADDRESS);
        if (trackPartConfigAddress >= 0) {
            txtAddress.setText(String.valueOf(trackPartConfigAddress));
        }
        groupModuleAddress.add(txtAddress);
        fieldSet.add(groupModuleAddress);

        // module bit
        FormGroup groupBit = new FormGroup();
        FormLabel lblBit = new FormLabel();
        lblBit.setFor(ID_FORM_BIT);
        lblBit.setText("Bit");
        groupBit.add(lblBit);
        selectBit.setId(ID_FORM_BIT);
        for (int index = 0; index < 8; index++) {
            Option option = new Option();
            String value = String.valueOf(index + 1);
            option.setValue(value);
            option.setText(value);
            selectBit.add(option);
            if (index + 1 == trackPartConfigBit) {
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
        trackPartConfigAddress = Integer.parseInt(txtAddress.getText());
        trackPartConfigBit = Integer.parseInt(selectBit.getValue());
    }

    abstract public boolean isRepresentationOf(T trackPart);

    abstract public String getTrackWidgetStyleName();

    abstract public TrackPart getTrackPart(Widget containerWidget, int zoomLevel);

    abstract public String getPaletteTitle();

    abstract public AbstractSvgTrackWidget<T> getClone(T trackPart);

    public AbsoluteTrackPosition getTrackPosition(GridPosition gridPosition, int zoomLevel) {
        return new AbsoluteTrackPosition(TrackUtils.getLeftPositionFromX(gridPosition.getX(), zoomLevel),
                TrackUtils.getTopPositionFromY(gridPosition.getY(), zoomLevel));
    }

    public GridPosition getGridPosition(Widget containerWidget, int zoomLevel) {
        return new GridPosition(
                TrackUtils.getXFromLeftPosition(getAbsoluteLeft() - containerWidget.getAbsoluteLeft(), zoomLevel),
                TrackUtils.getYFromTopPosition(getAbsoluteTop() - containerWidget.getAbsoluteTop(), zoomLevel));
    }


}
