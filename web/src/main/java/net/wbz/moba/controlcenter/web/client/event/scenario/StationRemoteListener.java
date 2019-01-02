package net.wbz.moba.controlcenter.web.client.event.scenario;

import net.wbz.moba.controlcenter.web.client.event.RemoteEventListener;
import net.wbz.moba.controlcenter.web.shared.scenario.StationDataChangedEvent;

/**
 * {@link RemoteEventListener} for the {@link StationDataChangedEvent}s.
 *
 * @author Daniel Tuerk
 */
public interface StationRemoteListener extends RemoteEventListener<StationDataChangedEvent> {

    @Override
    default void applyEvent(StationDataChangedEvent event) {
        stationsChanged(event);
    }

    @Override
    default Class<StationDataChangedEvent> getRemoteClass() {
        return StationDataChangedEvent.class;
    }

    void stationsChanged(StationDataChangedEvent event);
}
