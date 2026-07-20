package io.github.danieltuerk.controlcenter.shared.constrution;

import io.github.danieltuerk.controlcenter.shared.StateEvent;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

/**
 * @author Daniel Tuerk
 */
@Schema(description = "Train status update sent via WebSocket")
@Tag(ref = "websocket")
public record CurrentConstructionChangeEvent(
    @Schema(required = true) Construction construction) implements StateEvent {

}
