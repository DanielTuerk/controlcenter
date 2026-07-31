package io.github.danieltuerk.controlcenter.shared.station;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.danieltuerk.controlcenter.shared.StateEvent;

/**
 * Event for changed data of the station board for a {@link Station}.
 *
 * @author Daniel Tuerk
 */
public sealed interface StationBoardEvent extends StateEvent
        permits StationBoardArrivalEvent, StationBoardDepartureEvent {

    record StationInfo(long stationId,
                       String platform,
                       String train,
                       String timeText,
                       String information) {

        @JsonProperty
        public String key() {
            return "%s:%s:%s".formatted(stationId, platform, train);
        }
    }

    @JsonProperty
    long ttlInMs();

}
