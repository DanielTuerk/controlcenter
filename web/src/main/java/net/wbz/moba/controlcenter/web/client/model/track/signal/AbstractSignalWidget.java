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
import org.gwtbootstrap3.client.ui.constants.ColumnSize;
import org.gwtbootstrap3.client.ui.constants.Pull;
import org.gwtbootstrap3.client.ui.constants.Toggle;
import org.gwtbootstrap3.client.ui.constants.WellSize;
import org.gwtbootstrap3.client.ui.gwt.FlowPanel;
import org.gwtbootstrap3.client.ui.html.ClearFix;
import org.gwtbootstrap3.client.ui.html.Text;
import org.gwtbootstrap3.extras.select.client.ui.Option;
import org.gwtbootstrap3.extras.select.client.ui.Select;
import org.gwtbootstrap3.extras.toggleswitch.client.ui.ToggleSwitch;
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
    private SignalEditDialogContent dialogContent;

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

        dialogContent = new SignalEditDialogContent(trackPart);

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
       return dialogContent.getDialogContent();
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