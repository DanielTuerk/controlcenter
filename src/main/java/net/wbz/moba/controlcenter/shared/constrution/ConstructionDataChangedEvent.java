package net.wbz.moba.controlcenter.shared.constrution;

import net.wbz.moba.controlcenter.shared.AbstractItemEvent;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

/**
 * Event for modified data of {@link Construction} or created/deleted entity.
 *
 * @author Daniel Tuerk
 */
@Schema(description = "Construction update sent via WebSocket")
@Tag(ref = "websocket")
public class ConstructionDataChangedEvent extends AbstractItemEvent {

    public ConstructionDataChangedEvent(long constructionId) {
        super(constructionId);
    }

    public ConstructionDataChangedEvent() {
    }

    @Override
    public String toString() {
        return "ConstructionDataChangedEvent{} " + super.toString();
    }
}
