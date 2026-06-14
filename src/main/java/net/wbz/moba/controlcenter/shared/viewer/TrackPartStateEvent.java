package net.wbz.moba.controlcenter.shared.viewer;

import net.wbz.moba.controlcenter.shared.StateEvent;
import net.wbz.moba.controlcenter.shared.track.model.BusDataConfiguration;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

/**
 * @author Daniel Tuerk
 */
@Schema(description = "track status update sent via WebSocket")
@Tag(ref = "websocket")
public record TrackPartStateEvent(long trackPartId,
                                  BusDataConfiguration configuration,
                                  boolean state) implements StateEvent {

    @Override
    public String getCacheKey() {
        return getClass().getName() + ":" + configuration.getIdentifierKey();
    }

}
