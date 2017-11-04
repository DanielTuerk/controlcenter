package net.wbz.moba.controlcenter.web.client.model.track;

import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.FieldSet;
import org.gwtbootstrap3.client.ui.FormGroup;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.Label;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.ColumnSize;
import org.gwtbootstrap3.extras.select.client.ui.Option;
import org.gwtbootstrap3.extras.select.client.ui.Select;
import org.vectomatic.dom.svg.OMSVGDocument;
import org.vectomatic.dom.svg.OMSVGSVGElement;

import com.google.common.base.Strings;
import com.google.gwt.user.client.ui.Widget;

import net.wbz.moba.controlcenter.web.client.RequestUtils;
import net.wbz.moba.controlcenter.web.client.editor.track.ClickActionViewerWidgetHandler;
import net.wbz.moba.controlcenter.web.client.util.BitStateToggleButton;
import net.wbz.moba.controlcenter.web.client.util.Log;
import net.wbz.moba.controlcenter.web.shared.track.model.AbstractTrackPart;
import net.wbz.moba.controlcenter.web.shared.track.model.BusDataConfiguration;
import net.wbz.moba.controlcenter.web.shared.track.model.EventConfiguration;
import net.wbz.moba.controlcenter.web.shared.track.model.HasToggleFunction;

/**
 * A {@link AbstractSvgTrackWidget} with click control
 * to toggle the state of the {@link AbstractTrackPart}.
 * <p/>
 *
 * @author Daniel Tuerk
 */
abstract public class AbstractControlSvgTrackWidget<T extends AbstractTrackPart & HasToggleFunction> extends
        AbstractBlockSvgTrackWidget<T>
        implements ClickActionViewerWidgetHandler {

    private static final String ID_FORM_ADDRESS = "formAddress_control";
    private static final String ID_FORM_BIT = "formBit_control";
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
    private Select selectBit;
    private TextBox txtAddress;

    /**
     * Add the svg items to the element which represents the active state of the widget.
     * 
     * @param doc {@link OMSVGDocument}
     * @param svg {@link OMSVGSVGElement}
     * @param color color of SVG content
     */
    abstract protected void addActiveStateSvgContent(OMSVGDocument doc, OMSVGSVGElement svg, String color);

    @Override
    public void onClick() {
        if (isEnabled()) {
            BusDataConfiguration toggleFunctionConfig = getTrackPart().getToggleFunction();
            RequestUtils.getInstance().getTrackViewerService().toggleTrackPart(toggleFunctionConfig, !trackPartState,
                    RequestUtils.VOID_ASYNC_CALLBACK);
        }
    }

    public void updateFunctionState(BusDataConfiguration configuration, boolean state) {
        // update the SVG for the state of the {@link AbstractTrackPart#DEFAULT_TOGGLE_FUNCTION}
        BusDataConfiguration toggleFunctionConfig = getTrackPart().getToggleFunction();
        if (toggleFunctionConfig != null && toggleFunctionConfig.equals(configuration)) {
            trackPartState = state;
            clearSvgContent();
            if (state == toggleFunctionConfig.getBitState()) {
                addActiveStateSvgContent(getSvgDocument(), getSvgRootElement(), getColor());
            } else {
                addSvgContent(getSvgDocument(), getSvgRootElement(), getColor());
            }
        } else {
            Log.warn("received unknown configuration for track widget: " + getClass().getName());
        }
    }

    private void addConfigContent() {
        FieldSet fieldSet = new FieldSet();
        // module address
        FormGroup groupModuleAddress = new FormGroup();
        FormLabel lblAddress = new FormLabel();
        lblAddress.setText("Address");
        lblAddress.setFor(ID_FORM_ADDRESS);
        groupModuleAddress.add(lblAddress);

        txtAddress = new TextBox();
        txtAddress.setId(ID_FORM_ADDRESS);
        if (getTrackPart().getToggleFunction() != null) {
            txtAddress.setText(String.valueOf(getTrackPart().getToggleFunction().getAddress()));
        }
        org.gwtbootstrap3.client.ui.gwt.FlowPanel flowPanel = new org.gwtbootstrap3.client.ui.gwt.FlowPanel();
        flowPanel.addStyleName(ColumnSize.MD_2.getCssName());
        flowPanel.add(txtAddress);
        groupModuleAddress.add(flowPanel);
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
            if (getTrackPart().getToggleFunction() != null) {
                if (index == getTrackPart().getToggleFunction().getBit()) {
                    selectBit.setValue(value);
                }
            }
        }
        groupBit.add(selectBit);
        fieldSet.add(groupBit);

        addDialogContentTab("Config", fieldSet);
    }

    @Override
    public Widget getDialogContent() {
        Widget dialogContent = super.getDialogContent();

        addConfigContent();

        addEventContent();

        return dialogContent;
    }

    private void addEventContent() {
        FieldSet fieldSet = new FieldSet();

        // module address
        FormGroup groupStateOn = new FormGroup();
        FormLabel lblAddress = new FormLabel();
        lblAddress.setText("ON");
        lblAddress.setFor("stateOn");
        groupStateOn.add(lblAddress);

        org.gwtbootstrap3.client.ui.gwt.FlowPanel flowPanel = new org.gwtbootstrap3.client.ui.gwt.FlowPanel();
        flowPanel.addStyleName(ColumnSize.MD_2.getCssName());
        txtEventConfigOnAddress = new TextBox();
        txtEventConfigOnAddress.setId("stateOn");
        flowPanel.add(new Label("address"));
        flowPanel.add(txtEventConfigOnAddress);

        txtEventConfigOnBit = new TextBox();
        flowPanel.add(new Label("bit"));
        flowPanel.add(txtEventConfigOnBit);
        toggleEventConfigOnBitState = new BitStateToggleButton();
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
        txtEventConfigOffAddress.setId("stateOn");
        flowPanelOff.add(txtEventConfigOffAddress);

        flowPanelOff.add(new Label("bit"));

        txtEventConfigOffBit = new TextBox();
        flowPanelOff.add(txtEventConfigOffBit);

        flowPanelOff.add(new Label("state"));

        toggleEventConfigOffBitState = new BitStateToggleButton();
        flowPanelOff.add(toggleEventConfigOffBitState);

        groupStateOff.add(flowPanelOff);
        fieldSet.add(groupStateOff);

        EventConfiguration eventTrackPartConfiguration = getTrackPart().getEventConfiguration();
        if (eventTrackPartConfiguration != null) {
            BusDataConfiguration eventConfigOn = eventTrackPartConfiguration.getStateOnConfig();
            BusDataConfiguration eventConfigOff = eventTrackPartConfiguration.getStateOffConfig();
            if (eventConfigOn != null) {
                txtEventConfigOnAddress.setValue(String.valueOf(eventConfigOn.getAddress()));
                txtEventConfigOnBit.setValue(String.valueOf(eventConfigOn.getBit()));
                toggleEventConfigOnBitState.setActive(eventConfigOn.getBitState());
            }
            if (eventConfigOff != null) {
                txtEventConfigOffAddress.setValue(String.valueOf(eventConfigOff.getAddress()));
                txtEventConfigOffBit.setValue(String.valueOf(eventConfigOff.getBit()));
                toggleEventConfigOffBitState.setActive(eventConfigOff.getBitState());
            }
        }

        addDialogContentTab("Event", fieldSet);
    }

    @Override
    public void onConfirmCallback() {
        super.onConfirmCallback();

        // save event config
        EventConfiguration eventTrackPartConfiguration = getTrackPart().getEventConfiguration();
        if (eventTrackPartConfiguration == null) {
            eventTrackPartConfiguration = new EventConfiguration();
        }
        // state ON
        if (!Strings.isNullOrEmpty(txtEventConfigOnAddress.getValue())
                && !Strings.isNullOrEmpty(txtEventConfigOnBit.getValue())) {
            if (eventTrackPartConfiguration.getStateOnConfig() == null) {
                eventTrackPartConfiguration.setStateOnConfig(new BusDataConfiguration());
            }
            eventTrackPartConfiguration.getStateOnConfig().setBus(1);
            eventTrackPartConfiguration.getStateOnConfig().setAddress(Integer.parseInt(txtEventConfigOnAddress
                    .getValue()));
            eventTrackPartConfiguration.getStateOnConfig().setBit(Integer.parseInt(txtEventConfigOnBit.getValue()));
            eventTrackPartConfiguration.getStateOnConfig().setBitState(toggleEventConfigOnBitState.isActive());
        }
        // state OFF
        if (!Strings.isNullOrEmpty(txtEventConfigOffAddress.getValue())
                && !Strings.isNullOrEmpty(txtEventConfigOffBit.getValue())) {
            if (eventTrackPartConfiguration.getStateOffConfig() == null) {
                eventTrackPartConfiguration.setStateOffConfig(new BusDataConfiguration());
            }
            eventTrackPartConfiguration.getStateOffConfig().setBus(1);
            eventTrackPartConfiguration.getStateOffConfig().setAddress(Integer.parseInt(txtEventConfigOffAddress
                    .getValue()));
            eventTrackPartConfiguration.getStateOffConfig().setBit(Integer.parseInt(txtEventConfigOffBit.getValue()));
            eventTrackPartConfiguration.getStateOffConfig().setBitState(toggleEventConfigOffBitState.isActive());
        }

        getTrackPart().setEventConfiguration(eventTrackPartConfiguration);

        // save toggle config
        if (Strings.isNullOrEmpty(txtAddress.getText())) {
            getTrackPart().setToggleFunction(null);
        } else {
            if (getTrackPart().getToggleFunction() == null) {
                getTrackPart().setToggleFunction(new BusDataConfiguration());
            }
            getTrackPart().getToggleFunction().setBus(1);
            getTrackPart().getToggleFunction().setAddress(Integer.parseInt(txtAddress.getText()));
            getTrackPart().getToggleFunction().setBit(Integer.parseInt(selectBit.getValue()));
            getTrackPart().getToggleFunction().setBitState(true);
        }
    }

    @Override
    public String getConfigurationInfo() {
        return super.getConfigurationInfo() +
                "toggle: " + getTrackPart().getToggleFunction() + "; " +
                "event: " + getTrackPart().getEventConfiguration() + "; ";
    }
}
