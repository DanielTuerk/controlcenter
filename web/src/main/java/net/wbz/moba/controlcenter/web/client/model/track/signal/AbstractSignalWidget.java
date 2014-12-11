package net.wbz.moba.controlcenter.web.client.model.track.signal;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Widget;
import net.wbz.moba.controlcenter.web.client.Popover;
import net.wbz.moba.controlcenter.web.client.model.track.AbstractControlSvgTrackWidget;
import net.wbz.moba.controlcenter.web.shared.track.model.Configuration;
import net.wbz.moba.controlcenter.web.shared.track.model.Signal;
import net.wbz.moba.controlcenter.web.shared.track.model.Straight;
import net.wbz.moba.controlcenter.web.shared.track.model.TrackPart;
import org.gwtbootstrap3.client.ui.*;
import org.gwtbootstrap3.client.ui.gwt.FlowPanel;
import org.vectomatic.dom.svg.OMSVGDocument;
import org.vectomatic.dom.svg.OMSVGSVGElement;
import org.vectomatic.dom.svg.utils.OMSVGParser;

/**
 * Abstract widget for an signal.
 * This signal can be toggled to change the state of the configured bit.
 *
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
abstract public class AbstractSignalWidget extends AbstractControlSvgTrackWidget<Signal> {

    private Configuration[] storedConfigurations;
    private Signal.TYPE signalType;
    private Popover popover;

    public AbstractSignalWidget() {
        popover = new Popover(this);
    }

    @Override
    protected void addSvgContent(OMSVGDocument doc, OMSVGSVGElement svg) {
        // TODO: ugly
        if (signalType == null) {
            signalType = Signal.TYPE.BLOCK;
        }
        SignalSvgBuilder.getInstance().addSvgContent(signalType, doc, svg);
    }

    @Override
    protected void addActiveStateSvgContent(OMSVGDocument doc, OMSVGSVGElement svg) {
        SignalSvgBuilder.getInstance().addActiveStateSvgContent(signalType, doc, svg);
    }

    @Override
    protected void initFromTrackPart(Signal trackPart) {
        super.initFromTrackPart(trackPart);
        if (trackPart.getType() != null) {
            signalType = trackPart.getType();
        } else {
            signalType = Signal.TYPE.BLOCK;
        }

        Button btnDrive = new Button("drive");
        btnDrive.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                popover.hide();
            }
        });
        popover.addContent(btnDrive);

        if (signalType == Signal.TYPE.EXIT || signalType == Signal.TYPE.ENTER || signalType == Signal.TYPE.BEFORE) {
            Button btnSlowDrive = new Button("slow drive");
            btnSlowDrive.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    popover.hide();
                }
            });
            popover.addContent(btnSlowDrive);
        }

        if (signalType == Signal.TYPE.EXIT) {
            Button btnRouting = new Button("routing");
            btnRouting.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    popover.hide();
                }
            });
            popover.addContent(btnRouting);
        }

        Button btnStop = new Button("stop");
        btnStop.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                popover.hide();
            }
        });
        popover.addContent(btnStop);
    }

    @Override
    public TrackPart getTrackPart(Widget containerWidget, int zoomLevel) {
        Signal signal = new Signal();
        signal.setDirection(getStraightDirection());
        signal.setGridPosition(getGridPosition(containerWidget, zoomLevel));
        signal.setConfiguration(getStoredWidgetConfiguration());
        signal.setAdditionalConfigurations(storedConfigurations);
        signal.setType(signalType);
        return signal;
    }

    @Override
    public Widget getDialogContent() {
        TabListItem active = null;

        TabPanel tabPanel = new TabPanel();
        NavTabs navTabs = new NavTabs();
        TabContent tabContent = new TabContent();
        for (Signal.TYPE signalType : Signal.TYPE.values()) {
            TabPane tabPane = new TabPane();
            tabContent.add(tabPane);

            FlowPanel signalContent = new FlowPanel();
            signalContent.getElement().getStyle().setPaddingTop(10, Style.Unit.PX);
            tabPane.add(signalContent);

            OMSVGDocument svgDocument = OMSVGParser.currentDocument();
            OMSVGSVGElement svgRootElement = svgDocument.createSVGSVGElement();

            SignalSvgBuilder.getInstance().addPreview(signalType, svgDocument, svgRootElement);

            signalContent.getElement().appendChild(svgRootElement.getElement());
            signalContent.add(new Label("config"));


            TabListItem tabListItem = new TabListItem(signalType.name());
            tabListItem.setDataTargetWidget(tabPane);
            tabListItem.setActive(this.signalType == signalType);
            navTabs.add(tabListItem);
        }

        tabPanel.add(navTabs);
        tabPanel.add(tabContent);
        return tabPanel;
    }

    @Override
    public void onClick() {
//        if(signalType== Signal.TYPE.BLOCK) {
//            super.onClick();
//        } else {

        //TODO

//        popover.reconfigure();
        popover.toggle();

//        }
    }

    abstract public Straight.DIRECTION getStraightDirection();

    @Override
    public String getPaletteTitle() {
        return "Signal";
    }

}
