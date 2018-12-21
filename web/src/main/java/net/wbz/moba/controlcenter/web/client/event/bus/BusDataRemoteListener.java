package net.wbz.moba.controlcenter.web.client.event.bus;

import net.wbz.moba.controlcenter.web.client.event.RemoteEventListener;
import net.wbz.moba.controlcenter.web.shared.bus.BusDataEvent;

/**
 * {@link RemoteEventListener} for the {@link BusDataEvent}s.
 *
 * @author Daniel Tuerk
 */
public interface BusDataRemoteListener extends RemoteEventListener<BusDataEvent> {

    @Override
    default void applyEvent(BusDataEvent event) {
        busDataChanged(event);
    }

    @Override
    default Class<BusDataEvent> getRemoteClass() {
        return BusDataEvent.class;
    }

    void busDataChanged(BusDataEvent event);
}
