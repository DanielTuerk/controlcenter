package net.wbz.moba.controlcenter.web.shared.viewer;

import java.util.Objects;
import net.wbz.moba.controlcenter.web.client.event.StateEvent;
import net.wbz.moba.controlcenter.web.shared.track.model.Signal;

/**
 * @author Daniel Tuerk
 */
public class SignalFunctionStateEvent implements StateEvent {

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
        final StringBuffer sb = new StringBuffer("SignalFunctionStateEvent{");
        sb.append("signalId=").append(signalId);
        sb.append(", signalFunction=").append(signalFunction);
        sb.append('}');
        return sb.toString();
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
        return Objects.equals(signalId, that.signalId) && signalFunction == that.signalFunction;
    }

    @Override
    public int hashCode() {

        return Objects.hash(signalId, signalFunction);
    }
}
