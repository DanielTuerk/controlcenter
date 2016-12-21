package net.wbz.moba.controlcenter.web.shared.viewer;

import de.novanic.eventservice.client.event.Event;
import net.wbz.moba.controlcenter.web.server.persist.construction.track.SignalEntity;
import net.wbz.moba.controlcenter.web.server.persist.construction.track.TrackPartConfigurationEntity;

import java.util.Map;

/**
 * @author Daniel Tuerk
 */
public class SignalFunctionStateEvent implements Event {

    private Map<SignalEntity.LIGHT, TrackPartConfigurationEntity> configuration;
    private SignalEntity.FUNCTION signalFunction;

    public SignalFunctionStateEvent() {
    }

    public SignalFunctionStateEvent(Map<SignalEntity.LIGHT, TrackPartConfigurationEntity> configuration, SignalEntity.FUNCTION signalFunction) {
        this.configuration = configuration;
        this.signalFunction = signalFunction;
    }

    public Map<SignalEntity.LIGHT, TrackPartConfigurationEntity> getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Map<SignalEntity.LIGHT, TrackPartConfigurationEntity> configuration) {
        this.configuration = configuration;
    }

    public SignalEntity.FUNCTION getSignalFunction() {
        return signalFunction;
    }

    public void setSignalFunction(SignalEntity.FUNCTION signalFunction) {
        this.signalFunction = signalFunction;
    }
}
