package io.github.danieltuerk.controlcenter.shared.track.model;

import io.github.danieltuerk.controlcenter.shared.Event;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

/**
 * @author Daniel Tuerk
 */
@Schema(description = "Track update sent via WebSocket")
@Tag(ref = "websocket")
public record TrackChangedEvent(boolean dirty) implements Event {
}
