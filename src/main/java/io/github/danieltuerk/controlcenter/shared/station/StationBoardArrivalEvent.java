package io.github.danieltuerk.controlcenter.shared.station;

import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

/**
 * @author Daniel Tuerk
 */
@Schema(description = "station board event for arrival via WebSocket")
@Tag(ref = "websocket")
public record StationBoardArrivalEvent(StationInfo stationInfo,
                                       String sourceStationName,
                                       long ttlInMs) implements StationBoardEvent {
    @Override
    public String getCacheKey() {
        return stationInfo.key();
    }
}
