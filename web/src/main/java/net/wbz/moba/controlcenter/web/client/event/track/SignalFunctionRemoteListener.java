package net.wbz.moba.controlcenter.web.client.event.track;

import net.wbz.moba.controlcenter.web.client.event.RemoteEventListener;
import net.wbz.moba.controlcenter.web.shared.viewer.SignalFunctionStateEvent;

/**
 * {@link RemoteEventListener} for the {@link SignalFunctionStateEvent}s.
 *
 * @author Daniel Tuerk
 */
public interface SignalFunctionRemoteListener extends RemoteEventListener<SignalFunctionStateEvent> {

    @Override
    default void applyEvent(SignalFunctionStateEvent event) {
        signalFunctionStateChanged(event);
    }

    @Override
    default Class<SignalFunctionStateEvent> getRemoteClass() {
        return SignalFunctionStateEvent.class;
    }

    void signalFunctionStateChanged(SignalFunctionStateEvent event);
}
