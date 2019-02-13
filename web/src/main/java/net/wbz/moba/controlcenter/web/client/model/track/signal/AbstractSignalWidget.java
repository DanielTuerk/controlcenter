package net.wbz.moba.controlcenter.web.client.model.track.signal;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Widget;
import java.util.Objects;
import net.wbz.moba.controlcenter.web.client.components.Popover;
import net.wbz.moba.controlcenter.web.client.event.EventReceiver;
import net.wbz.moba.controlcenter.web.client.event.track.SignalFunctionRemoteListener;
import net.wbz.moba.controlcenter.web.client.model.track.AbstractControlSvgTrackWidget;
import net.wbz.moba.controlcenter.web.client.request.RequestUtils;
import net.wbz.moba.controlcenter.web.client.viewer.track.svg.TrackViewerPanel;
import net.wbz.moba.controlcenter.web.shared.track.model.Signal;
import net.wbz.moba.controlcenter.web.shared.track.model.Straight;
import net.wbz.moba.controlcenter.web.shared.viewer.SignalFunctionStateEvent;
import org.gwtbootstrap3.client.ui.Button;
import org.vectomatic.dom.svg.OMSVGDocument;
import org.vectomatic.dom.svg.OMSVGSVGElement;

/**
 * Abstract widget for an signal.
 * This signal can be toggled to change the state of the configured bit.
 *
 * @author Daniel Tuerk
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

    private final SignalFunctionRemoteListener signalFunctionStateEventListener;

    AbstractSignalWidget() {
        super();
        popover = new Popover(TrackViewerPanel.ID, this);
        signalFunctionStateEventListener = this::updateSignalState;
    }

    private void updateSignalState(SignalFunctionStateEvent signalFunctionStateEvent) {
        if (Objects.equals(getTrackPart().getId(), signalFunctionStateEvent.getSignalId())) {
            showSignalFunction(signalFunctionStateEvent.getSignalFunction());
        }
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        EventReceiver.getInstance().addListener(signalFunctionStateEventListener);
    }

    @Override
    protected void onUnload() {
        super.onUnload();

        EventReceiver.getInstance().removeListener(signalFunctionStateEventListener);

        if (popover.isShowing()) {
            popover.hide();
        }
    }

    @Override
    public Signal getNewTrackPart() {
        Signal signal = new Signal();
        signal.setDirection(getStraightDirection());
        signal.setType(Signal.TYPE.BLOCK);
        return signal;
    }

    @Override
    protected void addSvgContent(OMSVGDocument doc, OMSVGSVGElement svg, String color) {
        SignalSvgBuilder.getInstance().addSvgContent(signalType, Signal.FUNCTION.HP0, doc, svg, color);
    }

    @Override
    protected void addActiveStateSvgContent(OMSVGDocument doc, OMSVGSVGElement svg, String color) {
        SignalSvgBuilder.getInstance().addSvgContent(signalType, activeFunction, doc, svg, color);
    }

    @Override
    protected void initFromTrackPart(Signal trackPart) {
        super.initFromTrackPart(trackPart);
        if (trackPart.getType() != null) {
            signalType = trackPart.getType();
        } else {
            signalType = Signal.TYPE.BLOCK;
        }

        dialogContent = new SignalEditDialogContent();

        Button btnDrive = new Button("drive");
        btnDrive.addClickHandler(event -> {
            switchSignalFunction(Signal.FUNCTION.HP1);
            popover.hide();
        });
        popover.addContent(btnDrive);

        if (signalType == Signal.TYPE.EXIT || signalType == Signal.TYPE.ENTER || signalType == Signal.TYPE.BEFORE) {
            Button btnSlowDrive = new Button("slow drive");
            btnSlowDrive.addClickHandler(event -> {
                switchSignalFunction(Signal.FUNCTION.HP2);
                popover.hide();
            });
            popover.addContent(btnSlowDrive);
        }

        if (signalType == Signal.TYPE.EXIT) {
            Button btnRouting = new Button("routing");
            btnRouting.addClickHandler(event -> {
                switchSignalFunction(Signal.FUNCTION.HP0_SH1);
                popover.hide();
            });
            popover.addContent(btnRouting);
        }

        Button btnStop = new Button("stop");
        btnStop.addClickHandler(event -> {
            switchSignalFunction(Signal.FUNCTION.HP0);
            popover.hide();
        });
        popover.addContent(btnStop);
    }

    private void switchSignalFunction(Signal.FUNCTION function) {
        Signal signal = getTrackPart();
        RequestUtils.getInstance().getTrackViewerService().switchSignal(signal, function,
                RequestUtils.VOID_ASYNC_CALLBACK);
    }

    @Override
    public Widget getDialogContent() {
        dialogContent.setSignal(getTrackPart());
        return dialogContent;
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

    abstract public Straight.DIRECTION getStraightDirection();

    @Override
    public String getPaletteTitle() {
        return "Signal";
    }

    public void showSignalFunction(Signal.FUNCTION signalFunction) {
        clearSvgContent();
        SignalSvgBuilder.getInstance().addSvgContent(signalType, signalFunction, getSvgDocument(), getSvgRootElement(),
                getColor());
        activeFunction = signalFunction;
    }
}
