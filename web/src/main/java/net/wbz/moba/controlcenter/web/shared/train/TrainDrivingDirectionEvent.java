package net.wbz.moba.controlcenter.web.shared.train;

/**
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
public class TrainDrivingDirectionEvent extends TrainStateEvent {
    private DRIVING_DIRECTION direction;

    public enum DRIVING_DIRECTION {
        FORWARD, BACKWARD
    }

    public TrainDrivingDirectionEvent(String driving_direction) {
        this.direction = DRIVING_DIRECTION.valueOf(driving_direction);
    }

    public TrainDrivingDirectionEvent() {
    }

    public DRIVING_DIRECTION getDirection() {
        return direction;
    }
}
