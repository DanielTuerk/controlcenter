package net.wbz.moba.controlcenter.web.client.event.train;

import net.wbz.moba.controlcenter.web.shared.train.TrainLightStateEvent;

/**
 * Remote listener for the state of the light of the train.
 *
 * @author Daniel Tuerk
 */
public interface TrainLightStateRemoteListener extends TrainStateRemoteListener<TrainLightStateEvent> {

    @Override
    default void applyTrainEvent(TrainLightStateEvent event) {
        lightChanged(event.isState());
    }

    void lightChanged(boolean state);

    @Override
    default Class<TrainLightStateEvent> getRemoteClass() {
        return TrainLightStateEvent.class;
    }
}
