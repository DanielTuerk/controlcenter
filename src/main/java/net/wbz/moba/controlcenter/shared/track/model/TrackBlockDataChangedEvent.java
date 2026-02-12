package net.wbz.moba.controlcenter.shared.track.model;

import net.wbz.moba.controlcenter.shared.AbstractItemEvent;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

/**
 * Event for modified data of {@link TrackBlock} or created/deleted entity.
 *
 * @author Daniel Tuerk
 */
@Schema(description = "Track-block update sent via WebSocket")
@Tag(ref = "websocket")
public class TrackBlockDataChangedEvent extends AbstractItemEvent {

    public TrackBlockDataChangedEvent(long trackBlockId) {
        super(trackBlockId);
    }

    public TrackBlockDataChangedEvent() {
    }

    @Override
    public String toString() {
        return "TrackBlockDataChangedEvent{} " + super.toString();
    }
}
