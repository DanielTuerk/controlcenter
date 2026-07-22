package io.github.danieltuerk.controlcenter.shared.bus;

import io.github.danieltuerk.controlcenter.shared.StateEvent;
import io.github.danieltuerk.selectrix4java.data.recording.BusDataPlayer;
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
