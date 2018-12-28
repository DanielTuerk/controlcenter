package net.wbz.moba.controlcenter.web.client.event.train;

import net.wbz.moba.controlcenter.web.client.event.RemoteEventListener;
import net.wbz.moba.controlcenter.web.shared.train.TrainStateEvent;

/**
 * Remote listener for a train.
 *
 * @author Daniel Tuerk
 */
interface TrainStateRemoteListener<E extends TrainStateEvent> extends RemoteEventListener<E> {

    @Override
    default void applyEvent(E event) {
        if (event.itemId == getTrainId()) {

            applyTrainEvent((E) event);
        }
    }

    /**
     * Unique ID of the train to identify the events which belongs to the train.
     *
     * @return id of {@link net.wbz.moba.controlcenter.web.shared.train.Train}
     */
    long getTrainId();

    void applyTrainEvent(E event);
}
