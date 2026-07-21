package io.github.danieltuerk.controlcenter.shared.train;

import io.github.danieltuerk.controlcenter.shared.CacheKeyHelper;
import io.github.danieltuerk.controlcenter.shared.StateEvent;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

/**
 * @author Daniel Tuerk
 */
@Schema(description = "Train status update sent via WebSocket")
@Tag(ref = "websocket")
public record TrainDrivingDirectionEvent(long trainId, DRIVING_DIRECTION direction) implements StateEvent {

    public enum DRIVING_DIRECTION {
        FORWARD, BACKWARD
    }

    @Override
    public String getCacheKey() {
        return CacheKeyHelper.createCacheKey(this.getClass(), this.trainId);
    }
}
