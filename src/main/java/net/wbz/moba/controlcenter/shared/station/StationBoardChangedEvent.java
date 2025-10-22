package net.wbz.moba.controlcenter.shared.station;

import net.wbz.moba.controlcenter.shared.Event;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

/**
 * Event for changed data of an station board for a {@link Station}.
 *
 * @author Daniel Tuerk
 */
@Schema(description = "station status update sent via WebSocket")
@Tag(ref = "websocket")
public class StationBoardChangedEvent implements Event {

    public enum TYPE {
        ARRIVAL, DEPARTURE
    }

    private TYPE type;
    private long stationId;

    public StationBoardChangedEvent() {
    }

    public StationBoardChangedEvent(TYPE type, long stationId) {
        this.type = type;
        this.stationId = stationId;
    }

    public TYPE getType() {
        return type;
    }

    public void setType(TYPE type) {
        this.type = type;
    }

    public long getStationId() {
        return stationId;
    }

    public void setStationId(long stationId) {
        this.stationId = stationId;
    }

    @Override
    public String toString() {
        return "StationBoardChangedEvent{" + "type=" + type
            + ", stationId=" + stationId
            + '}';
    }
}
