package net.wbz.moba.controlcenter.shared.bus;

import net.wbz.moba.controlcenter.shared.StateEvent;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

/**
 * Event for the state of the {@link net.wbz.selectrix4java.data.recording.BusDataPlayer}.
 *
 * @author Daniel Tuerk
 */
@Schema(description = "bus status update sent via WebSocket")
@Tag(ref = "websocket")
public class PlayerEvent implements StateEvent {

    private STATE state;

    public PlayerEvent() {
    }

    public PlayerEvent(STATE state) {
        this.state = state;
    }

    public STATE getState() {
        return state;
    }

    public enum STATE {
        START, STOP
    }

}
