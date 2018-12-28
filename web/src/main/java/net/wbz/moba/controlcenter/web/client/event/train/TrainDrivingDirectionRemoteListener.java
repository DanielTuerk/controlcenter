package net.wbz.moba.controlcenter.web.client.event.train;

import net.wbz.moba.controlcenter.web.shared.train.TrainDrivingDirectionEvent;
import net.wbz.moba.controlcenter.web.shared.train.TrainDrivingDirectionEvent.DRIVING_DIRECTION;

/**
 * Remote listener for the {@link DRIVING_DIRECTION} of the train.
 *
 * @author Daniel Tuerk
 */
public interface TrainDrivingDirectionRemoteListener extends TrainStateRemoteListener<TrainDrivingDirectionEvent> {

    @Override
    default void applyTrainEvent(TrainDrivingDirectionEvent event) {
        drivingDirectionChanged(event.getDirection());
    }

    void drivingDirectionChanged(DRIVING_DIRECTION direction);

    @Override
    default Class<TrainDrivingDirectionEvent> getRemoteClass() {
        return TrainDrivingDirectionEvent.class;
    }
}
