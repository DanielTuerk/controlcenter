package net.wbz.moba.controlcenter.web.shared.viewer;

import de.novanic.eventservice.client.event.Event;
import net.wbz.moba.controlcenter.web.shared.track.model.Signal;

/**
 * @author Daniel Tuerk
 */
public class SignalFunctionStateEvent implements Event {

    private Long signalId;
    private Signal.FUNCTION signalFunction;

    public SignalFunctionStateEvent() {
    }

    public SignalFunctionStateEvent(Long signalId, Signal.FUNCTION signalFunction) {
        this.signalId = signalId;
        this.signalFunction = signalFunction;
    }

    public Long getSignalId() {
        return signalId;
    }

    public Signal.FUNCTION getSignalFunction() {
        return signalFunction;
    }

    @Override
    public String toString() {
        return "SignalFunctionStateEvent{" +
                "signalId=" + signalId +
                ", signalFunction=" + signalFunction +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SignalFunctionStateEvent that = (SignalFunctionStateEvent) o;
        return com.google.common.base.Objects.equal(signalId, that.signalId);
    }

    @Override
    public int hashCode() {
        return com.google.common.base.Objects.hashCode(signalId);
    }
}
