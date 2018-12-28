package net.wbz.moba.controlcenter.web.client.event.train;

import net.wbz.moba.controlcenter.web.shared.train.TrainDrivingLevelEvent;

/**
 * Remote listener for the driving level of the train.
 *
 * @author Daniel Tuerk
 */
public interface TrainDrivingLevelRemoteListener extends TrainStateRemoteListener<TrainDrivingLevelEvent> {

    @Override
    default void applyTrainEvent(TrainDrivingLevelEvent event) {
        drivingLevelChanged(event.getSpeed());
    }

    void drivingLevelChanged(int drivingLevel);

    @Override
    default Class<TrainDrivingLevelEvent> getRemoteClass() {
        return TrainDrivingLevelEvent.class;
    }
}
