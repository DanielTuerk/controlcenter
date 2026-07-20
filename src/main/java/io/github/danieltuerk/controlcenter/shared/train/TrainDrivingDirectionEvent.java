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
public class TrainDrivingDirectionEvent extends TrainStateEvent {
    private final DRIVING_DIRECTION direction;

    public TrainDrivingDirectionEvent(long itemId, DRIVING_DIRECTION direction) {
        super(itemId);
        this.direction = direction;
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
