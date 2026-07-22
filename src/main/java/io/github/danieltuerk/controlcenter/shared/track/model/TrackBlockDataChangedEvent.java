package io.github.danieltuerk.controlcenter.shared.track.model;

import io.github.danieltuerk.controlcenter.shared.AbstractItemEvent;
import lombok.Getter;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

/**
 * Event for modified data of {@link TrackBlock} or created/deleted entity.
 *
 * @author Daniel Tuerk
 */
@Getter
@Schema(description = "Track-block update sent via WebSocket")
@Tag(ref = "websocket")
public class TrackBlockDataChangedEvent extends AbstractItemEvent {
    private final ACTION_TYPE type;

    public TrackBlockDataChangedEvent(long trainId, ACTION_TYPE type) {
        super(trainId);
        this.type = type;
    }

    @Override
    public String toString() {
        return "TrackBlockDataChangedEvent{} " + super.toString();
    }
}
