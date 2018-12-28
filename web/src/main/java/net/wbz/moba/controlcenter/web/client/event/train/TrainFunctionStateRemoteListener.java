package net.wbz.moba.controlcenter.web.client.event.train;

import net.wbz.moba.controlcenter.web.shared.train.TrainFunctionStateEvent;

/**
 * Remote listener for the state of the function of the train.
 *
 * @author Daniel Tuerk
 */
public interface TrainFunctionStateRemoteListener extends TrainStateRemoteListener<TrainFunctionStateEvent> {

    @Override
    default void applyTrainEvent(TrainFunctionStateEvent event) {
        functionStateChanged(event);
    }

    void functionStateChanged(TrainFunctionStateEvent event);

    @Override
    default Class<TrainFunctionStateEvent> getRemoteClass() {
        return TrainFunctionStateEvent.class;
    }
}
