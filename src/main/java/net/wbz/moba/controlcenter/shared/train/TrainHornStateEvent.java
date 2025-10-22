package net.wbz.moba.controlcenter.shared.train;

import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

/**
 * @author Daniel Tuerk
 */
@Schema(description = "Train status update sent via WebSocket")
@Tag(ref = "websocket")
public class TrainHornStateEvent extends TrainStateEvent {

    private boolean state;

    public TrainHornStateEvent(long itemId, boolean state) {
        super(itemId);
        this.state = state;
    }

    public TrainHornStateEvent() {
    }

    public boolean isState() {
        return state;
    }

    @Override
    public String toString() {
        return "TrainHornStateEvent{" +
                "state=" + state +
                "} " + super.toString();
    }

}
