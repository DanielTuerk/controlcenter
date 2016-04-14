package net.wbz.moba.controlcenter.web.client.model.track;

import java.util.Map;

import net.wbz.moba.controlcenter.web.client.ServiceUtils;
import net.wbz.moba.controlcenter.web.client.editor.track.ClickActionViewerWidgetHandler;
import net.wbz.moba.controlcenter.web.client.util.BitStateToggleButton;
import net.wbz.moba.controlcenter.web.client.util.Log;
import net.wbz.moba.controlcenter.web.shared.track.model.Configuration;
import net.wbz.moba.controlcenter.web.shared.track.model.ConfigurationProxy;
import net.wbz.moba.controlcenter.web.shared.track.model.EventConfigurationProxy;
import net.wbz.moba.controlcenter.web.shared.track.model.TrackModelConstants;
import net.wbz.moba.controlcenter.web.shared.track.model.TrackPartProxy;

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

import com.google.gwt.user.client.ui.Widget;

/**
 * A {@link AbstractSvgTrackWidget} with click control
 * to toggle the state of the {@link net.wbz.moba.controlcenter.web.shared.track.model.TrackPart}.
 * <p/>
 *
 * @author Daniel Tuerk
 */
abstract public class AbstractControlSvgTrackWidget<T extends TrackPartProxy> extends AbstractBlockSvgTrackWidget<T>
        implements ClickActionViewerWidgetHandler {

    private static final String ID_FORM_BIT = "formBit";
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
     * @param doc {@link org.vectomatic.dom.svg.OMSVGDocument}
     * @param svg {@link org.vectomatic.dom.svg.OMSVGSVGElement}
     */
    abstract protected void addActiveStateSvgContent(OMSVGDocument doc, OMSVGSVGElement svg);

    @Override
    public void onClick() {
        if (isEnabled()) {
            ConfigurationProxy toggleFunctionConfig = getStoredWidgetFunctionConfigs().get(
                    TrackModelConstants.DEFAULT_TOGGLE_FUNCTION);
            ServiceUtils.getInstance().getTrackViewerService().toggleTrackPart(toggleFunctionConfig, !trackPartState)
                    .fire();
        }
    }

    @Override
    public void updateFunctionState(Configuration configuration, boolean state) {
        super.updateFunctionState(configuration, state);
        // update the SVG for the state of the {@link TrackPart#DEFAULT_TOGGLE_FUNCTION}
        ConfigurationProxy toggleFunctionConfig = getStoredWidgetFunctionConfigs().get(
                TrackModelConstants.DEFAULT_TOGGLE_FUNCTION);
        if (toggleFunctionConfig != null && toggleFunctionConfig.equals(configuration)) {
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
    public Map<String, ConfigurationProxy> getStoredWidgetFunctionConfigs() {
        Map<String, ConfigurationProxy> functionConfigs = super.getStoredWidgetFunctionConfigs();
//        Configuration configuration = new Configuration();
//        configuration.setBus(1); // TODO
//        configuration.setAddress(getTrackPart().getDefaultToggleFunctionConfig().getAddress());
//        configuration.setBit(getTrackPart().getDefaultToggleFunctionConfig().getBit());
//        configuration.setBitState(true);
//        functionConfigs.put(TrackModelConstants.DEFAULT_TOGGLE_FUNCTION, configuration);
        return functionConfigs;
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
        if (getTrackPart().getDefaultToggleFunctionConfig().getAddress() >= 0) {
            txtAddress.setText(String.valueOf(getTrackPart().getDefaultToggleFunctionConfig().getAddress()));
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
            if (index == getTrackPart().getDefaultToggleFunctionConfig().getBit()) {
                selectBit.setValue(option);
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

        EventConfigurationProxy eventConfiguration = getTrackPart().getEventConfiguration();
        if (eventConfiguration != null) {
            ConfigurationProxy eventConfigOn = eventConfiguration.getStateOnConfig();
            ConfigurationProxy eventConfigOff = eventConfiguration.getStateOffConfig();
            if (eventConfigOn != null) {
                txtEventConfigOnAddress.setValue(String.valueOf(eventConfigOn.getAddress()));
                txtEventConfigOnBit.setValue(String.valueOf(eventConfigOn.getBit()));
                toggleEventConfigOnBitState.setActive(eventConfigOn.isBitState());
            }
            if (eventConfigOff != null) {
                txtEventConfigOffAddress.setValue(String.valueOf(eventConfigOff.getAddress()));
                txtEventConfigOffBit.setValue(String.valueOf(eventConfigOff.getBit()));
                toggleEventConfigOffBitState.setActive(eventConfigOff.isBitState());
            }
        }

        addDialogContentTab("Event", fieldSet);
    }

    @Override
    public void onConfirmCallback() {
        super.onConfirmCallback();

        // save event config
        EventConfigurationProxy eventConfiguration;
        if (getTrackPart().getEventConfiguration() != null) {
            eventConfiguration = getTrackPart().getEventConfiguration();
        } else {
            // TODO
            // eventConfiguration = new EventConfiguration();
        }

        // TODO

        // // state ON
        // if (!Strings.isNullOrEmpty(txtEventConfigOnAddress.getValue())
        // && !Strings.isNullOrEmpty(txtEventConfigOnBit.getValue())) {
        // if (eventConfiguration.getStateOnConfig() == null) {
        // eventConfiguration.setStateOnConfig(new Configuration());
        // }
        // eventConfiguration.getStateOnConfig().setBus(1);
        // eventConfiguration.getStateOnConfig().setAddress(Integer.parseInt(txtEventConfigOnAddress.getValue()));
        // eventConfiguration.getStateOnConfig().setBit(Integer.parseInt(txtEventConfigOnBit.getValue()));
        // eventConfiguration.getStateOnConfig().setBitState(toggleEventConfigOnBitState.isActive());
        // }
        // // state OFF
        // if (!Strings.isNullOrEmpty(txtEventConfigOffAddress.getValue())
        // && !Strings.isNullOrEmpty(txtEventConfigOffBit.getValue())) {
        // if (eventConfiguration.getStateOffConfig() == null) {
        // eventConfiguration.setStateOffConfig(new Configuration());
        // }
        // eventConfiguration.getStateOffConfig().setBus(1);
        // eventConfiguration.getStateOffConfig().setAddress(Integer.parseInt(txtEventConfigOffAddress.getValue()));
        // eventConfiguration.getStateOffConfig().setBit(Integer.parseInt(txtEventConfigOffBit.getValue()));
        // eventConfiguration.getStateOffConfig().setBitState(toggleEventConfigOffBitState.isActive());
        // }

        // getTrackPart().setEventConfiguration(eventConfiguration);

        // save toggle config
        getTrackPart().getDefaultToggleFunctionConfig().setBus(1);
        getTrackPart().getDefaultToggleFunctionConfig().setAddress(Integer.parseInt(txtAddress.getText()));
        getTrackPart().getDefaultToggleFunctionConfig().setBit(Integer.parseInt(selectBit.getValue()));
    }
}
