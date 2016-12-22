package net.wbz.moba.controlcenter.web.shared.viewer;

import de.novanic.eventservice.client.event.Event;
import net.wbz.moba.controlcenter.web.server.persist.construction.track.TrackPartConfigurationEntity;
import net.wbz.moba.controlcenter.web.shared.track.model.Signal;
import net.wbz.moba.controlcenter.web.shared.track.model.TrackPartConfiguration;

import java.util.Map;

/**
 * @author Daniel Tuerk
 */
public class SignalFunctionStateEvent implements Event {

    private Map<Signal.LIGHT, TrackPartConfiguration> configuration;
    private Signal.FUNCTION signalFunction;

    public SignalFunctionStateEvent() {
    }

    public SignalFunctionStateEvent(Map<Signal.LIGHT, TrackPartConfiguration> configuration, Signal.FUNCTION signalFunction) {
        this.configuration = configuration;
        this.signalFunction = signalFunction;
    }

    public Map<Signal.LIGHT, TrackPartConfiguration> getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Map<Signal.LIGHT, TrackPartConfiguration> configuration) {
        this.configuration = configuration;
    }

    public Signal.FUNCTION getSignalFunction() {
        return signalFunction;
    }

    public void setSignalFunction(Signal.FUNCTION signalFunction) {
        this.signalFunction = signalFunction;
    }
}
