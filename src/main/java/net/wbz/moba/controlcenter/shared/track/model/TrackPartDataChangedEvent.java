package net.wbz.moba.controlcenter.shared.track.model;

import net.wbz.moba.controlcenter.shared.AbstractItemEvent;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

/**
 * Event for modified data of {@link AbstractTrackPart} or created/deleted entity.
 *
 * @author Daniel Tuerk
 */
@Schema(description = "Track-part update sent via WebSocket")
@Tag(ref = "websocket")
public class TrackPartDataChangedEvent extends AbstractItemEvent {

    public TrackPartDataChangedEvent(long trackPartId) {
        super(trackPartId);
    }

    @Override
    public String toString() {
        return "TrackPartDataChangedEvent{} " + super.toString();
    }
}
