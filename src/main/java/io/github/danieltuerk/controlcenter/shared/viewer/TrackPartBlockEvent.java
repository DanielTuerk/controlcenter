package io.github.danieltuerk.controlcenter.shared.viewer;

import io.github.danieltuerk.controlcenter.shared.StateEvent;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Schema(description = "track status update sent via WebSocket")
@Tag(ref = "websocket")
public record TrackPartBlockEvent(long trackPartId,
                                  boolean occupied) implements StateEvent {

    @Override
    public String getCacheKey() {
        return "%s:%d".formatted(getClass().getName(), trackPartId);
    }

}
