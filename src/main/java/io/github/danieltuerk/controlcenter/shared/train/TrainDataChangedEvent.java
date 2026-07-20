package io.github.danieltuerk.controlcenter.shared.train;

import io.github.danieltuerk.controlcenter.shared.AbstractItemEvent;
import lombok.Getter;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

/**
 * Event for modified data of {@link Train} or created/deleted entity.
 *
 * @author Daniel Tuerk
 */
@Getter
@Schema(description = "Trains update sent via WebSocket")
@Tag(ref = "websocket")
public class TrainDataChangedEvent extends AbstractItemEvent {

    private final ACTION_TYPE type;

    public TrainDataChangedEvent(long trainId, ACTION_TYPE type) {
        super(trainId);
        this.type = type;
    }

    @Override
    public String toString() {
        return "TrainDataChangedEvent{" +
            "type=" + type +
            ", itemId=" + itemId +
            '}';
    }
}
