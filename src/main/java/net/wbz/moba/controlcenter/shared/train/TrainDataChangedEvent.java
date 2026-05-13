package net.wbz.moba.controlcenter.shared.train;

import net.wbz.moba.controlcenter.shared.AbstractItemEvent;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

/**
 * Event for modified data of {@link Train} or created/deleted entity.
 *
 * @author Daniel Tuerk
 */
@Schema(description = "Trains update sent via WebSocket")
@Tag(ref = "websocket")
public class TrainDataChangedEvent extends AbstractItemEvent {

    private final ACTION_TYPE type;

    public TrainDataChangedEvent(long trainId, ACTION_TYPE type) {
        super(trainId);
        this.type = type;
    }

    public ACTION_TYPE getType() {
        return type;
    }

    @Override
    public String toString() {
        return "TrainDataChangedEvent{" +
            "type=" + type +
            ", itemId=" + itemId +
            '}';
    }
}
