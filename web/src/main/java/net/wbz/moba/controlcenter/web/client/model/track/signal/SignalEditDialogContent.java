package net.wbz.moba.controlcenter.web.client.model.track.signal;

import java.util.Map;

import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Column;
import org.gwtbootstrap3.client.ui.Container;
import org.gwtbootstrap3.client.ui.NavTabs;
import org.gwtbootstrap3.client.ui.PanelBody;
import org.gwtbootstrap3.client.ui.Row;
import org.gwtbootstrap3.client.ui.TabContent;
import org.gwtbootstrap3.client.ui.TabListItem;
import org.gwtbootstrap3.client.ui.TabPane;
import org.gwtbootstrap3.client.ui.TabPanel;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.Well;
import org.gwtbootstrap3.client.ui.constants.ColumnSize;
import org.gwtbootstrap3.client.ui.constants.WellSize;
import org.gwtbootstrap3.client.ui.gwt.FlowPanel;
import org.gwtbootstrap3.client.ui.html.ClearFix;
import org.gwtbootstrap3.client.ui.html.Text;
import org.gwtbootstrap3.extras.select.client.ui.Option;
import org.gwtbootstrap3.extras.select.client.ui.Select;
import org.vectomatic.dom.svg.OMSVGDocument;
import org.vectomatic.dom.svg.OMSVGSVGElement;
import org.vectomatic.dom.svg.utils.OMSVGParser;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

import net.wbz.moba.controlcenter.web.client.components.TrackBlockSelect;
import net.wbz.moba.controlcenter.web.client.components.BitStateToggleButton;
import net.wbz.moba.controlcenter.web.shared.track.model.BusDataConfiguration;
import net.wbz.moba.controlcenter.web.shared.track.model.Signal;

/**
 * @author Daniel Tuerk
 */
public class SignalEditDialogContent extends Composite {

    interface Binder extends UiBinder<Widget, SignalEditDialogContent> {
    }

    private static Binder uiBinder = GWT.create(Binder.class);

    @UiField
    PanelBody panelBodyLightsConfig;

    @UiField
    TrackBlockSelect selectEnteringBlock;
    @UiField
    TrackBlockSelect selectBreakingBlock;
    @UiField
    TrackBlockSelect selectStopBlock;
    @UiField
    TrackBlockSelect selectMonitoringBlock;

    private static final String ID_TXT_ADDRESS = "txtAddress";
    private static final String ID_SELECT_BIT = "selectBit";
    private static final String ID_BTN_BIT_STATE = "btnBitState";
    private Signal signal;

    private Map<Signal.TYPE, TabListItem> signalTypesTabs = Maps.newHashMap();
    private Map<String, Widget> idWidgets = Maps.newHashMap();

    public SignalEditDialogContent() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    public void setSignal(Signal signal) {
        this.signal = signal;
        initBlocksConfigPanel();
        initLightsConfigPanel();
    }

    private void initBlocksConfigPanel() {
        // TODO

        selectEnteringBlock.setSelectedTrackBlockValue(signal.getEnteringBlock());
        selectBreakingBlock.setSelectedTrackBlockValue(signal.getBreakingBlock());
        selectStopBlock.setSelectedTrackBlockValue(signal.getStopBlock());
        selectMonitoringBlock.setSelectedTrackBlockValue(signal.getMonitoringBlock());
    }

    private void initLightsConfigPanel() {
        // TODO refactor to split init and create
        panelBodyLightsConfig.clear();
        TabPanel tabPanel = new TabPanel();
        NavTabs navTabs = new NavTabs();
        TabContent tabContent = new TabContent();
        // tab for each signal type
        for (Signal.TYPE signalType : Signal.TYPE.values()) {
            // tab content
            TabPane tabPane = new TabPane();
            tabPane.setActive(signal.getType() == signalType);
            tabContent.add(tabPane);

            FlowPanel panel = new FlowPanel();
            panel.getElement().getStyle().setPaddingTop(10, Style.Unit.PX);
            panel.add(createPreview(signalType));
            panel.add(createLightConfig(signalType));
            panel.add(new ClearFix());
            tabPane.add(panel);

            // tab nav
            TabListItem tabListItem = new TabListItem(signalType.name());
            signalTypesTabs.put(signalType, tabListItem);
            tabListItem.setDataTargetWidget(tabPane);

            navTabs.add(tabListItem);
        }
        tabPanel.add(navTabs);
        tabPanel.add(tabContent);

        panelBodyLightsConfig.add(tabPanel);
    }


    private Widget createPreview(Signal.TYPE signalType) {
        FlowPanel panel = new FlowPanel();
        panel.getElement().getStyle().setFloat(Style.Float.LEFT);
        panel.setWidth("25%");

        // svg preview
        OMSVGDocument svgDocument = OMSVGParser.currentDocument();
        OMSVGSVGElement svgRootElement = svgDocument.createSVGSVGElement();
        SignalSvgBuilder.getInstance().addPreview(signalType, svgDocument, svgRootElement);
        panel.getElement().appendChild(svgRootElement.getElement());
        return panel;
    }

    private Widget createLightConfig(Signal.TYPE signalType) {

        FlowPanel configPanel = new FlowPanel();
        configPanel.getElement().getStyle().setFloat(Style.Float.LEFT);
        configPanel.setWidth("75%");

        // light configuration header
        Container container = new Container();
        container.setWidth("100%");
        Row headersRow = new Row();
        headersRow.add(new Column(ColumnSize.MD_3, new Text("Light")));
        headersRow.add(new Column(ColumnSize.MD_3, new Text("Address")));
        headersRow.add(new Column(ColumnSize.MD_2, new Text("Bit")));
        headersRow.add(new Column(ColumnSize.MD_2, new Text("Bit State")));
        container.add(headersRow);

        for (Signal.LIGHT light : signalType.getLights()) {

            BusDataConfiguration existingLightConfig = signal.getSignalConfiguration(light);
            if (existingLightConfig == null) {
                existingLightConfig = new BusDataConfiguration();
            }

            Row functionRow = new Row();

            Well wellLightName = new Well();
            wellLightName.add(new Text(light.name()));
            wellLightName.setSize(WellSize.SMALL);
            functionRow.add(new Column(ColumnSize.MD_3, wellLightName));

            TextBox txtAddress = new TextBox();
            idWidgets.put(getElementId(signalType, light, ID_TXT_ADDRESS), txtAddress);
            if (existingLightConfig.getAddress() != null) {
                txtAddress.setText(String.valueOf(existingLightConfig.getAddress()));
            }
            functionRow.add(new Column(ColumnSize.MD_3, txtAddress));

            Select selectBit = new Select();
            idWidgets.put(getElementId(signalType, light, ID_SELECT_BIT), selectBit);
            selectBit.setWidth("auto");
            for (int i = 1; i <= 8; i++) {
                Option bitOption = new Option();
                String value = String.valueOf(i);
                bitOption.setText(value);
                selectBit.add(bitOption);
                if (existingLightConfig.getBit() != null && i == existingLightConfig.getBit()) {
                    selectBit.setValue(value);
                }
            }
            functionRow.add(new Column(ColumnSize.MD_2, selectBit));

            final Button btnBitState = new BitStateToggleButton();
            if (existingLightConfig.getBitState() != null) {
                btnBitState.setActive(existingLightConfig.getBitState());
            }
            idWidgets.put(getElementId(signalType, light, ID_BTN_BIT_STATE), btnBitState);
            functionRow.add(new Column(ColumnSize.MD_2, btnBitState));

            container.add(functionRow);
        }

        configPanel.add(container);
        return configPanel;
    }

    public void onConfirmCallback() {
        // type
        for (Map.Entry<Signal.TYPE, TabListItem> signalTypeTab : signalTypesTabs.entrySet()) {
            if (signalTypeTab.getValue().isActive()) {
                signal.setType(signalTypeTab.getKey());
                break;
            }
        }

        // lights
        for (Signal.LIGHT light : signal.getType().getLights()) {
            TextBox txtAddress = (TextBox) idWidgets.get(getElementId(signal.getType(), light, ID_TXT_ADDRESS));
            Select selectBit = (Select) idWidgets.get(getElementId(signal.getType(), light, ID_SELECT_BIT));
            Button btnBitState = (Button) idWidgets.get(getElementId(signal.getType(), light, ID_BTN_BIT_STATE));

            Integer addressValue = null;
            String txtAddressText = txtAddress.getText();
            if (!Strings.isNullOrEmpty(txtAddressText)) {
                addressValue = Integer.parseInt(txtAddressText.trim());
            }
            BusDataConfiguration signalConfiguration = signal.getSignalConfiguration(light);
            if (signalConfiguration == null) {
                signalConfiguration = new BusDataConfiguration();
                signal.updateSignalConfiguration(light, signalConfiguration);
            }
            signalConfiguration.setBus(1);
            signalConfiguration.setAddress(addressValue);
            signalConfiguration.setBit(Integer.parseInt(selectBit.getValue()));
            signalConfiguration.setBitState(btnBitState.isActive());
        }

        // blocks
        signal.setEnteringBlock(selectEnteringBlock.getSelectedTrackBlockValue());
        signal.setBreakingBlock(selectBreakingBlock.getSelectedTrackBlockValue());
        signal.setStopBlock(selectStopBlock.getSelectedTrackBlockValue());
        signal.setMonitoringBlock(selectMonitoringBlock.getSelectedTrackBlockValue());
    }

    private String getElementId(Signal.TYPE signalType, Signal.LIGHT signalLight, String elementId) {
        return signalType.name() + "." + signalLight.name() + "." + elementId;
    }
}
