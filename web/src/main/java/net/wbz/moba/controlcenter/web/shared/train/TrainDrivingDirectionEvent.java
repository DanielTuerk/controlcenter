package net.wbz.moba.controlcenter.web.shared.train;

/**
 * @author Daniel Tuerk
 */
public class TrainDrivingDirectionEvent extends TrainStateEvent {
    private DRIVING_DIRECTION direction;

    public TrainDrivingDirectionEvent(long itemId, DRIVING_DIRECTION direction) {
        super(itemId);
        this.direction = direction;
    }

    public TrainDrivingDirectionEvent() {
    }

    public DRIVING_DIRECTION getDirection() {
        return direction;
    }

    @Override
    public String toString() {
        return "TrainDrivingDirectionEvent{" +
                "direction=" + direction +
                "} " + super.toString();
    }

    public enum DRIVING_DIRECTION {
        FORWARD, BACKWARD
    }
}
