package net.wbz.moba.controlcenter.web.client.model.track.signal;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Widget;
import net.wbz.moba.controlcenter.web.shared.track.model.Signal;
import org.gwtbootstrap3.client.ui.*;
import org.gwtbootstrap3.client.ui.constants.ColumnSize;
import org.gwtbootstrap3.client.ui.constants.Toggle;
import org.gwtbootstrap3.client.ui.constants.WellSize;
import org.gwtbootstrap3.client.ui.gwt.FlowPanel;
import org.gwtbootstrap3.client.ui.html.ClearFix;
import org.gwtbootstrap3.client.ui.html.Text;
import org.gwtbootstrap3.extras.select.client.ui.Option;
import org.gwtbootstrap3.extras.select.client.ui.Select;
import org.vectomatic.dom.svg.OMSVGDocument;
import org.vectomatic.dom.svg.OMSVGSVGElement;
import org.vectomatic.dom.svg.utils.OMSVGParser;

/**
 * @author Daniel Tuerk
 */
public class SignalEditDialogContent {

    private final Signal signal;

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

        for (Signal.LIGHT LIGHT : signalType.getLights()) {

            Row functionRow = new Row();

            Well wellLightName = new Well();
            wellLightName.add(new Text(LIGHT.name()));
            wellLightName.setSize(WellSize.SMALL);
            functionRow.add(new Column(ColumnSize.MD_3, wellLightName));

            functionRow.add(new Column(ColumnSize.MD_3, new TextBox()));
            Select selectBit = new Select();
            selectBit.setWidth("auto");
            for (int i = 1; i <= 8; i++) {
                Option bitOption = new Option();
                bitOption.setText(String.valueOf(i));
                selectBit.add(bitOption);
            }
            functionRow.add(new Column(ColumnSize.MD_2, selectBit));

            final Button btnBitState = new Button("OFF");
            btnBitState.setDataToggle(Toggle.BUTTON);
            btnBitState.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    if (btnBitState.isActive()) {
                        btnBitState.setText("OFF");
                    } else {
                        btnBitState.setText("ON");
                    }
                }
            });
            functionRow.add(new Column(ColumnSize.MD_2, btnBitState));

            container.add(functionRow);
        }

        configPanel.add(container);

        return configPanel;
    }
}
