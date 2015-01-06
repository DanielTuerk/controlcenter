package net.wbz.moba.controlcenter.web.client.model.track;

import com.google.gwt.user.client.ui.Widget;
import net.wbz.moba.controlcenter.web.client.ServiceUtils;
import net.wbz.moba.controlcenter.web.client.editor.track.ClickActionViewerWidgetHandler;
import net.wbz.moba.controlcenter.web.client.util.BitStateToggleButton;
import net.wbz.moba.controlcenter.web.client.util.EmptyCallback;
import net.wbz.moba.controlcenter.web.client.util.Log;
import net.wbz.moba.controlcenter.web.shared.track.model.Configuration;
import net.wbz.moba.controlcenter.web.shared.track.model.EventConfiguration;
import net.wbz.moba.controlcenter.web.shared.track.model.TrackPart;
import org.gwtbootstrap3.client.ui.*;
import org.gwtbootstrap3.client.ui.constants.ColumnSize;
import org.vectomatic.dom.svg.OMSVGDocument;
import org.vectomatic.dom.svg.OMSVGSVGElement;

/**
 * A {@link AbstractSvgTrackWidget} with click control
 * to toggle the state of the {@link net.wbz.moba.controlcenter.web.shared.track.model.TrackPart}.
 * <p/>
 *
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
abstract public class AbstractControlSvgTrackWidget<T extends TrackPart> extends AbstractSvgTrackWidget<T>
        implements ClickActionViewerWidgetHandler {

    /**
     * Current state of the widget to toggle.
     */
    private boolean trackPartState = false;
    private TextBox txtEventConfigOnAddress;
    private TextBox txtEventConfigOnBit;
    private Button toggleEventConfigOnBitState;
    private TextBox txtEventConfigOffAddress;
    private TextBox txtEventConfigOffBit;
    private Button toggleEventConfigOffBitState;

    /**
     * Add the svg items to the element which represents the active state of the widget.
     *
     * @param doc {@link org.vectomatic.dom.svg.OMSVGDocument}
     * @param svg {@link org.vectomatic.dom.svg.OMSVGSVGElement}
     */
    abstract protected void addActiveStateSvgContent(OMSVGDocument doc, OMSVGSVGElement svg);

    @Override
    public void onClick() {
        if (isEnabled()) {
            Configuration toggleFunctionConfig = getStoredWidgetFunctionConfigs().get(TrackPart.DEFAULT_TOGGLE_FUNCTION);
            ServiceUtils.getTrackViewerService().toggleTrackPart(toggleFunctionConfig, !trackPartState, new
                    EmptyCallback<Void>());
        }
    }

    @Override
    public void updateFunctionState(Configuration configuration, boolean state) {
        // update the SVG for the state of the {@link TrackPart#DEFAULT_TOGGLE_FUNCTION}
        Configuration toggleFunctionConfig = getStoredWidgetFunctionConfigs().get(TrackPart.DEFAULT_TOGGLE_FUNCTION);
        if (toggleFunctionConfig.equals(configuration)) {
            trackPartState = state;
            clearSvgContent();
            if (state == toggleFunctionConfig.isBitState()) {
                addActiveStateSvgContent(getSvgDocument(), getSvgRootElement());
            } else {
                addSvgContent(getSvgDocument(), getSvgRootElement());
            }
        } else {
            Log.warn("received unknown configuration for track widget: " + getClass().getName());
        }
    }

    @Override
    public Widget getDialogContent() {
        Widget dialogContent = super.getDialogContent();

        FieldSet fieldSet = new FieldSet();

        Configuration eventConfigOn = getTrackPart().getEventConfiguration().getStateOnConfig();
        Configuration eventConfigOff = getTrackPart().getEventConfiguration().getStateOffConfig();

        // module address
        FormGroup groupStateOn = new FormGroup();
        FormLabel lblAddress = new FormLabel();
        lblAddress.setText("ON");
        lblAddress.setFor("stateOn");
        groupStateOn.add(lblAddress);

        org.gwtbootstrap3.client.ui.gwt.FlowPanel flowPanel = new org.gwtbootstrap3.client.ui.gwt.FlowPanel();
        flowPanel.addStyleName(ColumnSize.MD_2.getCssName());
        txtEventConfigOnAddress = new TextBox();
        txtEventConfigOnAddress.setValue(String.valueOf(eventConfigOn.getAddress()));
        txtEventConfigOnAddress.setId("stateOn");
//        if (trackPart.getDefaultToggleFunctionConfig().getAddress() >= 0) {
//            txtAddress.setText(String.valueOf(trackPart.getDefaultToggleFunctionConfig().getAddress()));
//        }
        flowPanel.add(new Label("address"));
        flowPanel.add(txtEventConfigOnAddress);

        txtEventConfigOnBit = new TextBox();
        txtEventConfigOnBit.setValue(String.valueOf(eventConfigOn.getBit()));
        flowPanel.add(new Label("bit"));
        flowPanel.add(txtEventConfigOnBit);
        toggleEventConfigOnBitState = new BitStateToggleButton();
        toggleEventConfigOnBitState.setActive(eventConfigOn.isBitState());
        flowPanel.add(new Label("state"));
        flowPanel.add(toggleEventConfigOnBitState);

        groupStateOn.add(flowPanel);
        fieldSet.add(groupStateOn);


        FormGroup groupStateOff = new FormGroup();
        FormLabel lblState = new FormLabel();
        lblState.setText("OFF");
        lblState.setFor("stateOff");
        groupStateOff.add(lblState);

        org.gwtbootstrap3.client.ui.gwt.FlowPanel flowPanelOff = new org.gwtbootstrap3.client.ui.gwt.FlowPanel();
        flowPanelOff.addStyleName(ColumnSize.MD_2.getCssName());

        flowPanelOff.add(new Label("address"));

        txtEventConfigOffAddress = new TextBox();
        txtEventConfigOffAddress.setValue(String.valueOf(eventConfigOff.getAddress()));
        txtEventConfigOffAddress.setId("stateOn");
        flowPanelOff.add(txtEventConfigOffAddress);

        flowPanelOff.add(new Label("bit"));

        txtEventConfigOffBit = new TextBox();
        txtEventConfigOffBit.setValue(String.valueOf(eventConfigOff.getBit()));
        flowPanelOff.add(txtEventConfigOffBit);

        flowPanelOff.add(new Label("state"));

        toggleEventConfigOffBitState = new BitStateToggleButton();
        toggleEventConfigOffBitState.setActive(eventConfigOff.isBitState());
        flowPanelOff.add(toggleEventConfigOffBitState);

        groupStateOff.add(flowPanelOff);
        fieldSet.add(groupStateOff);


        addDialogContentTab("Event", fieldSet);
        return dialogContent;
    }

    @Override
    public void onConfirmCallback() {
        super.onConfirmCallback();

        EventConfiguration eventConfiguration = new EventConfiguration();
        eventConfiguration.setStateOnConfig(
                new Configuration(1, Integer.parseInt(txtEventConfigOnAddress.getValue()),
                        Integer.parseInt(txtEventConfigOnBit.getValue()), toggleEventConfigOnBitState.isActive()));
        eventConfiguration.setStateOffConfig(
                new Configuration(1, Integer.parseInt(txtEventConfigOffAddress.getValue()),
                        Integer.parseInt(txtEventConfigOffBit.getValue()), toggleEventConfigOffBitState.isActive()));
        getTrackPart().setEventConfiguration(eventConfiguration);
    }
}
