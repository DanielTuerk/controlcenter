package net.wbz.moba.controlcenter.web.shared.viewer;

import de.novanic.eventservice.client.event.Event;
import net.wbz.moba.controlcenter.web.shared.track.model.BusDataConfiguration;
import net.wbz.moba.controlcenter.web.shared.track.model.Signal;

import java.util.Map;

/**
 * @author Daniel Tuerk
 */
public class SignalFunctionStateEvent implements Event {

    private Map<Signal.LIGHT, BusDataConfiguration> configuration;
    private Signal.FUNCTION signalFunction;

    public SignalFunctionStateEvent() {
    }

    public SignalFunctionStateEvent(Map<Signal.LIGHT, BusDataConfiguration> configuration, Signal.FUNCTION signalFunction) {
        this.configuration = configuration;
        this.signalFunction = signalFunction;
    }

    public Map<Signal.LIGHT, BusDataConfiguration> getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Map<Signal.LIGHT, BusDataConfiguration> configuration) {
        this.configuration = configuration;
    }

    public Signal.FUNCTION getSignalFunction() {
        return signalFunction;
    }

    public void setSignalFunction(Signal.FUNCTION signalFunction) {
        this.signalFunction = signalFunction;
    }
}
