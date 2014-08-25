package net.wbz.moba.controlcenter.web.shared.train;

import de.novanic.eventservice.client.event.Event;
import net.wbz.selectrix4java.api.train.TrainModule;

/**
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
public class TrainDrivingDirectionEvent extends TrainStateEvent {
    private TrainModule.DRIVING_DIRECTION direction;

    public TrainDrivingDirectionEvent(TrainModule.DRIVING_DIRECTION driving_direction) {
        this.direction = driving_direction;
    }

    public TrainDrivingDirectionEvent() {
    }

    public TrainModule.DRIVING_DIRECTION getDirection() {
        return direction;
    }
}
