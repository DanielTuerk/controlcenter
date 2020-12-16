package net.wbz.moba.controlcenter.web.client.event.station;

import net.wbz.moba.controlcenter.web.client.event.RemoteEventListener;
import net.wbz.moba.controlcenter.web.shared.station.StationBoardChangedEvent;

/**
 * {@link RemoteEventListener} for the {@link StationBoardChangedEvent}s.
 *
 * @author Daniel Tuerk
 */
public interface StationBoardListener extends RemoteEventListener<StationBoardChangedEvent> {

    @Override
    default void applyEvent(StationBoardChangedEvent event) {
        switch (event.getType()) {
            case ARRIVAL:
                arrivalBoardChanged(event);
                break;
            case DEPARTURE:
                departureBoardChanged(event);
                break;
        }
    }

    void arrivalBoardChanged(StationBoardChangedEvent event);

    void departureBoardChanged(StationBoardChangedEvent event);

    @Override
    default Class<StationBoardChangedEvent> getRemoteClass() {
        return StationBoardChangedEvent.class;
    }

}

