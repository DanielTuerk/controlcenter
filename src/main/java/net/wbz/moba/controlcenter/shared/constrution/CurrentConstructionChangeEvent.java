package net.wbz.moba.controlcenter.shared.constrution;

import net.wbz.moba.controlcenter.shared.StateEvent;
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
