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
    private final ACTION_TYPE type;

    public TrackBlockDataChangedEvent(long trainId, ACTION_TYPE type) {
        super(trainId);
        this.type = type;
    }

    public ACTION_TYPE getType() {
        return type;
    }
    @Override
    public String toString() {
        return "TrackBlockDataChangedEvent{} " + super.toString();
    }
}
