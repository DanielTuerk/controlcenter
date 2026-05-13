package net.wbz.moba.controlcenter.shared.bus;

import net.wbz.moba.controlcenter.shared.StateEvent;
import net.wbz.selectrix4java.data.recording.BusDataPlayer;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

/**
 * Event for the state of the {@link BusDataPlayer}.
 *
 * @author Daniel Tuerk
 */
@Schema(description = "bus status update sent via WebSocket")
@Tag(ref = "websocket")
public record PlayerEvent(PLAYER_STATE state) implements StateEvent {

    public enum PLAYER_STATE {
        START, STOP
    }

}
