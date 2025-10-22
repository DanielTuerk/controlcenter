package net.wbz.moba.controlcenter.shared.train;

import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

/**
 * @author Daniel Tuerk
 */
@Schema(description = "Train status update sent via WebSocket")
@Tag(ref = "websocket")
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
