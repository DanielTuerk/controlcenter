package net.wbz.moba.controlcenter.web.client.event.bus;

import net.wbz.moba.controlcenter.web.client.event.RemoteEventListener;
import net.wbz.moba.controlcenter.web.shared.bus.PlayerEvent;

/**
 * {@link RemoteEventListener} for the {@link PlayerEvent}s.
 *
 * @author Daniel Tuerk
 */
public interface BusPlayerRemoteListener extends RemoteEventListener<PlayerEvent> {

    @Override
    default void applyEvent(PlayerEvent event) {
        switch (event.getState()) {
            case START:
                start(event);
                break;
            case STOP:
                stop(event);
                break;
        }
    }

    @Override
    default Class<PlayerEvent> getRemoteClass() {
        return PlayerEvent.class;
    }

    void start(PlayerEvent event);

    void stop(PlayerEvent event);
}
