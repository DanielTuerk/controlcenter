package io.github.danieltuerk.controlcenter.shared.viewer;

import io.github.danieltuerk.controlcenter.shared.StateEvent;
import io.github.danieltuerk.controlcenter.shared.track.model.Signal;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

/**
 * @author Daniel Tuerk
 */
@Schema(description = "track status update sent via WebSocket")
@Tag(ref = "websocket")
public record SignalFunctionStateEvent(long signalId, Signal.FUNCTION signalFunction) implements StateEvent {

    @Override
    public String getCacheKey() {
        return getClass().getName() + ":" + signalId;
    }
}
