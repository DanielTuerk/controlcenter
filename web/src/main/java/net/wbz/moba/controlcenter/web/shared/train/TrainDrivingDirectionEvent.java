package net.wbz.moba.controlcenter.web.shared.train;

/**
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
public class TrainDrivingDirectionEvent extends TrainStateEvent {
    private DRIVING_DIRECTION direction;

    public enum DRIVING_DIRECTION {
        FORWARD, BACKWARD
    }

    public TrainDrivingDirectionEvent(long itemId, DRIVING_DIRECTION direction) {
        super(itemId);
        this.direction = direction;
    }

    public TrainDrivingDirectionEvent() {
    }

    public DRIVING_DIRECTION getDirection() {
        return direction;
    }
}
