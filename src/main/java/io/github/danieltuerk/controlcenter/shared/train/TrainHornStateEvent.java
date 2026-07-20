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
public class TrainHornStateEvent extends TrainStateEvent {

    private final boolean state;

    public TrainHornStateEvent(long itemId, boolean state) {
        super(itemId);
        this.state = state;
    }


    @Override
    public String toString() {
        return "TrainHornStateEvent{" +
                "state=" + state +
                "} " + super.toString();
    }

}
