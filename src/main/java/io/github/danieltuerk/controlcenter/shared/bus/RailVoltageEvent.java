package io.github.danieltuerk.controlcenter.shared.bus;

import io.github.danieltuerk.controlcenter.shared.StateEvent;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

/**
 * Event for the state change of the rail voltage.
 *
 * @author Daniel Tuerk
 */
@Schema(description = "railvoltage status update sent via WebSocket")
@Tag(ref = "websocket")
public record RailVoltageEvent(boolean state) implements StateEvent {

}
