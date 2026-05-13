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

    public enum CATEGORY {
        ARRIVAL, DEPARTURE
    }

    private CATEGORY CATEGORY;
    private long stationId;

    public StationBoardChangedEvent() {
    }

    public StationBoardChangedEvent(CATEGORY CATEGORY, long stationId) {
        this.CATEGORY = CATEGORY;
        this.stationId = stationId;
    }

    public CATEGORY getType() {
        return CATEGORY;
    }

    public void setType(CATEGORY CATEGORY) {
        this.CATEGORY = CATEGORY;
    }

    public long getStationId() {
        return stationId;
    }

    public void setStationId(long stationId) {
        this.stationId = stationId;
    }

    @Override
    public String toString() {
        return "StationBoardChangedEvent{" + "type=" + CATEGORY
            + ", stationId=" + stationId
            + '}';
    }
}
