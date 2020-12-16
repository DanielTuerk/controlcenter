package net.wbz.moba.controlcenter.web.shared.station;

import com.google.gwt.user.client.rpc.IsSerializable;
import de.novanic.eventservice.client.event.Event;

/**
 * Event for changed data of an station board for a {@link Station}.
 *
 * @author Daniel Tuerk
 */
public class StationBoardChangedEvent implements Event {

    public enum TYPE implements IsSerializable {
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
