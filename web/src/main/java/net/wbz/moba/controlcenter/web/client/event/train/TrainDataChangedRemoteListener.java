package net.wbz.moba.controlcenter.web.client.event.train;

import net.wbz.moba.controlcenter.web.client.event.RemoteEventListener;
import net.wbz.moba.controlcenter.web.shared.train.TrainDataChangedEvent;

/**
 * {@link RemoteEventListener} for the {@link TrainDataChangedEvent}s.
 *
 * @author Daniel Tuerk
 */
public interface TrainDataChangedRemoteListener extends RemoteEventListener<TrainDataChangedEvent> {

    @Override
    default void applyEvent(TrainDataChangedEvent event) {
        trainDataChanged(event);
    }

    @Override
    default Class<TrainDataChangedEvent> getRemoteClass() {
        return TrainDataChangedEvent.class;
    }

    void trainDataChanged(TrainDataChangedEvent event);
}
