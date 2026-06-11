package net.wbz.moba.controlcenter.shared.track.model;

import net.wbz.moba.controlcenter.shared.Event;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

/**
 * @author Daniel Tuerk
 */
@Schema(description = "Track update sent via WebSocket")
@Tag(ref = "websocket")
public record TrackChangedEvent(boolean dirty) implements Event {
}
