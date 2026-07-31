package io.github.danieltuerk.controlcenter.shared.station;

import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

/**
 * @author Daniel Tuerk
 */
@Schema(description = "station board event for departure via WebSocket")
@Tag(ref = "websocket")
public record StationBoardDepartureEvent(StationInfo stationInfo,
                                         String target,
                                         long ttlInMs) implements StationBoardEvent {

    @Override
    public String getCacheKey() {
        return stationInfo.key();
    }

}
