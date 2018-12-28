package net.wbz.moba.controlcenter.web.client.event.train;

import net.wbz.moba.controlcenter.web.shared.train.TrainHornStateEvent;

/**
 * Remote listener for the state oif the horn of the train.
 *
 * @author Daniel Tuerk
 */
public interface TrainHornStateRemoteListener extends TrainStateRemoteListener<TrainHornStateEvent> {

    @Override
    default void applyTrainEvent(TrainHornStateEvent event) {
        hornChanged(event.isState());
    }

    void hornChanged(boolean state);

    @Override
    default Class<TrainHornStateEvent> getRemoteClass() {
        return TrainHornStateEvent.class;
    }
}
