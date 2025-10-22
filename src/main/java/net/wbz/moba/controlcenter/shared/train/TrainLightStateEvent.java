package net.wbz.moba.controlcenter.shared.train;

import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

/**
 * @author Daniel Tuerk
 */
@Schema(description = "Train status update sent via WebSocket")
@Tag(ref = "websocket")
public class TrainLightStateEvent extends TrainStateEvent {

    private boolean state;

    public TrainLightStateEvent(long itemId, boolean state) {
        super(itemId);
        this.state = state;
    }

    public TrainLightStateEvent() {
    }

    public boolean isState() {
        return state;
    }

    @Override
    public String toString() {
        return "TrainLightStateEvent{" +
                "state=" + state +
                "} " + super.toString();
    }
}
