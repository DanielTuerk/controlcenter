package net.wbz.moba.controlcenter.web.client.model.track.signal;

import com.google.common.collect.Maps;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Widget;
import net.wbz.moba.controlcenter.web.client.util.BitStateToggleButton;
import net.wbz.moba.controlcenter.web.shared.track.model.Signal;
import net.wbz.moba.controlcenter.web.shared.track.model.BusDataConfiguration;
import org.gwtbootstrap3.client.ui.*;
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

import java.util.Map;

/**
 * @author Daniel Tuerk
 */
public class SignalEditDialogContent {

    private static final String ID_TXT_ADDRESS = "txtAddress";
    private static final String ID_SELECT_BIT = "selectBit";
    private static final String ID_BTN_BIT_STATE = "btnBitState";
    private final Signal signal;

    private Map<Signal.TYPE, TabListItem> signalTypesTabs = Maps.newHashMap();
    private Map<String, Widget> idWidgets = Maps.newHashMap();

    public SignalEditDialogContent(Signal signal) {
        this.signal = signal;
    }

    public Widget getDialogContent() {
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
            tabListItem.setActive(signal.getType() == signalType);
            navTabs.add(tabListItem);
        }
        tabPanel.add(navTabs);
        tabPanel.add(tabContent);
        return tabPanel;
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

            BusDataConfiguration existingLightConfig = signal.getSignalConfiguration().get(light);
            // if (existingLightConfig == null) {
            // existingLightConfig = new Configuration();
            // }

            Row functionRow = new Row();

            Well wellLightName = new Well();
            wellLightName.add(new Text(light.name()));
            wellLightName.setSize(WellSize.SMALL);
            functionRow.add(new Column(ColumnSize.MD_3, wellLightName));

            TextBox txtAddress = new TextBox();
            idWidgets.put(getElementId(signalType, light, ID_TXT_ADDRESS), txtAddress);
            txtAddress.setText(String.valueOf(existingLightConfig.getAddress()));
            functionRow.add(new Column(ColumnSize.MD_3, txtAddress));

            Select selectBit = new Select();
            idWidgets.put(getElementId(signalType, light, ID_SELECT_BIT), selectBit);
            selectBit.setWidth("auto");
            for (int i = 1; i <= 8; i++) {
                Option bitOption = new Option();
                String value = String.valueOf(i);
                bitOption.setText(value);
                selectBit.add(bitOption);
                if (i == existingLightConfig.getBit()) {
                    selectBit.setValue(value);
                }
            }
            functionRow.add(new Column(ColumnSize.MD_2, selectBit));

            final Button btnBitState = new BitStateToggleButton();
            btnBitState.setActive(existingLightConfig.isBitState());
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

            // TODO
            // signal.setLightFunctionConfig(light, new Configuration(1, Integer.parseInt
            // (txtAddress.getText().trim()), Integer.parseInt(selectBit.getValue()),
            // btnBitState.isActive()));
        }

    }

    private String getElementId(Signal.TYPE signalType, Signal.LIGHT signalLight, String elementId) {
        return signalType.name() + "." + signalLight.name() + "." + elementId;
    }
}
