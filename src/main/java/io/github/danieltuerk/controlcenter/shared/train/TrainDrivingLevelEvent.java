package io.github.danieltuerk.controlcenter.shared.train;

import lombok.Getter;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

/**
 * @author Daniel Tuerk
 */
@Getter
@Schema(description = "Train status update sent via WebSocket")
@Tag(ref = "websocket")
public class TrainDrivingLevelEvent extends TrainStateEvent {
    private final int speed;

    public TrainDrivingLevelEvent(long itemId, int speed) {
        super(itemId);
        this.speed = speed;
    }

    @Override
    public String toString() {
        return "TrainDrivingLevelEvent{" +
                "speed=" + speed +
                "} " + super.toString();
    }
}
