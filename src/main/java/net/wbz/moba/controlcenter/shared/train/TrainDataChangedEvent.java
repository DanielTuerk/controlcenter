package net.wbz.moba.controlcenter.shared.train;

import net.wbz.moba.controlcenter.shared.AbstractItemEvent;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

/**
 * Event for modified data of {@link Train} or created/deleted entity.
 *
 * @author Daniel Tuerk
 */
@Schema(description = "Train status update sent via WebSocket")
@Tag(ref = "websocket")
public class TrainDataChangedEvent extends AbstractItemEvent {

    public TrainDataChangedEvent(long trainId) {
        super(trainId);
    }

    public TrainDataChangedEvent() {
    }

    @Override
    public String toString() {
        return "TrainDataChangedEvent{} " + super.toString();
    }
}
