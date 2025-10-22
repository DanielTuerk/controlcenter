package net.wbz.moba.controlcenter.shared.station;

import net.wbz.moba.controlcenter.shared.AbstractItemEvent;
import net.wbz.moba.controlcenter.shared.Event;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

/**
 * Event if the data of a {@link Station} has changed.
 *
 * @author Daniel Tuerk
 */
@Schema(description = "station status update sent via WebSocket")
@Tag(ref = "websocket")
public class StationDataChangedEvent extends AbstractItemEvent {

    public StationDataChangedEvent(long stationId) {
        super(stationId);
    }

    @Override
    public String toString() {
        return "StationDataChangedEvent{}";
    }
}
