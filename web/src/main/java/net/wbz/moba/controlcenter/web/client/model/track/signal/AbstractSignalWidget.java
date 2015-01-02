package net.wbz.moba.controlcenter.web.client.model.track.signal;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Widget;
import net.wbz.moba.controlcenter.web.client.Popover;
import net.wbz.moba.controlcenter.web.client.ServiceUtils;
import net.wbz.moba.controlcenter.web.client.model.track.AbstractControlSvgTrackWidget;
import net.wbz.moba.controlcenter.web.client.util.EmptyCallback;
import net.wbz.moba.controlcenter.web.client.viewer.track.TrackViewerPanel;
import net.wbz.moba.controlcenter.web.shared.track.model.Signal;
import net.wbz.moba.controlcenter.web.shared.track.model.Straight;
import org.gwtbootstrap3.client.ui.Button;
import org.vectomatic.dom.svg.OMSVGDocument;
import org.vectomatic.dom.svg.OMSVGSVGElement;

/**
 * Abstract widget for an signal.
 * This signal can be toggled to change the state of the configured bit.
 *
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
abstract public class AbstractSignalWidget extends AbstractControlSvgTrackWidget<Signal> {

    /**
     * Type of the signal. Default is BLOCK.
     */
    private Signal.TYPE signalType = Signal.TYPE.BLOCK;
    private Popover popover;
    private SignalEditDialogContent dialogContent;

    /**
     * Current active function of the signal.
     */
    private Signal.FUNCTION activeFunction = null;

    public AbstractSignalWidget() {
        popover = new Popover(TrackViewerPanel.ID, this);
    }

    @Override
    protected void addSvgContent(OMSVGDocument doc, OMSVGSVGElement svg) {
        SignalSvgBuilder.getInstance().addSvgContent(signalType, Signal.FUNCTION.HP0, doc, svg);
    }

    @Override
    protected void addActiveStateSvgContent(OMSVGDocument doc, OMSVGSVGElement svg) {
        SignalSvgBuilder.getInstance().addSvgContent(signalType, activeFunction, doc, svg);
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
                switchSignalFunction(Signal.FUNCTION.HP1);
                popover.hide();
            }
        });
        popover.addContent(btnDrive);

        if (signalType == Signal.TYPE.EXIT || signalType == Signal.TYPE.ENTER || signalType == Signal.TYPE.BEFORE) {
            Button btnSlowDrive = new Button("slow drive");
            btnSlowDrive.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    switchSignalFunction(Signal.FUNCTION.HP2);
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
                    switchSignalFunction(Signal.FUNCTION.HP0_SH1);
                    popover.hide();
                }
            });
            popover.addContent(btnRouting);
        }

        Button btnStop = new Button("stop");
        btnStop.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                switchSignalFunction(Signal.FUNCTION.HP0);
                popover.hide();
            }
        });
        popover.addContent(btnStop);
    }

    private void switchSignalFunction(Signal.FUNCTION function) {
        Signal signal = getTrackPart();

        ServiceUtils.getTrackViewerService().switchSignal(signalType, function, signal.getSignalConfiguration(), new EmptyCallback<Void>());

    }

    @Override
    public Widget getDialogContent() {
        return dialogContent.getDialogContent();
    }

    @Override
    public void onConfirmCallback() {
        dialogContent.onConfirmCallback();
    }

    @Override
    public void onClick() {
        if (isEnabled()) {
            if (signalType == Signal.TYPE.BLOCK) {
                if (activeFunction == Signal.FUNCTION.HP0) {
                    switchSignalFunction(Signal.FUNCTION.HP1);
                } else {
                    switchSignalFunction(Signal.FUNCTION.HP0);
                }
            } else {
                popover.toggle();
            }
        }
    }

    @Override
    protected void onUnload() {
        super.onUnload();
        if (popover.isShowing()) {
            popover.hide();
        }
    }

    abstract public Straight.DIRECTION getStraightDirection();

    @Override
    public String getPaletteTitle() {
        return "Signal";
    }

    public void showSignalFunction(Signal.FUNCTION signalFunction) {
        clearSvgContent();
        SignalSvgBuilder.getInstance().addSvgContent(signalType, signalFunction, getSvgDocument(), getSvgRootElement());
        activeFunction = signalFunction;
    }
}
