package net.wbz.moba.controlcenter.shared.train;

import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

/**
 * @author Daniel Tuerk
 */
@Schema(description = "Train status update sent via WebSocket")
@Tag(ref = "websocket")
public class TrainDrivingLevelEvent extends TrainStateEvent {
    private int speed;

    public TrainDrivingLevelEvent(long itemId, int speed) {
        super(itemId);
        this.speed = speed;
    }

    public TrainDrivingLevelEvent() {
    }

    public int getSpeed() {
        return speed;
    }

    @Override
    public String toString() {
        return "TrainDrivingLevelEvent{" +
                "speed=" + speed +
                "} " + super.toString();
    }
}
