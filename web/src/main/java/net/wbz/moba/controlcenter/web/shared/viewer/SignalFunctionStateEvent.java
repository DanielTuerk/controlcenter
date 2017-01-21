package net.wbz.moba.controlcenter.web.shared.viewer;

import java.util.List;

import de.novanic.eventservice.client.event.Event;
import net.wbz.moba.controlcenter.web.shared.track.model.BusDataConfiguration;
import net.wbz.moba.controlcenter.web.shared.track.model.Signal;

/**
 * @author Daniel Tuerk
 */
public class SignalFunctionStateEvent implements Event {

    private List<BusDataConfiguration> configurations;
    private Signal.FUNCTION signalFunction;

    public SignalFunctionStateEvent() {
    }

    public SignalFunctionStateEvent(List<BusDataConfiguration> configurations, Signal.FUNCTION signalFunction) {
        this.configurations = configurations;
        this.signalFunction = signalFunction;
    }

    public List<BusDataConfiguration> getConfigurations() {
        return configurations;
    }

    public Signal.FUNCTION getSignalFunction() {
        return signalFunction;
    }

    public void setSignalFunction(Signal.FUNCTION signalFunction) {
        this.signalFunction = signalFunction;
    }
}
