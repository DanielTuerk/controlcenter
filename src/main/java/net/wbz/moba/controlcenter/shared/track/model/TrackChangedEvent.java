package net.wbz.moba.controlcenter.shared.track.model;

import jakarta.validation.constraints.NotNull;
import java.util.Collection;
import net.wbz.moba.controlcenter.shared.Event;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

/**
 * @author Daniel Tuerk
 */
@Schema(description = "Track update sent via WebSocket")
@Tag(ref = "websocket")
public record TrackChangedEvent(@NotNull Collection<AbstractTrackPart> trackParts) implements Event {

//    @Override
    public String toString() {
        return "TrackChangedEvent{ %d track parts }".formatted(trackParts.size());
    }
}
