package io.github.danieltuerk.controlcenter.shared.scenario;

import io.github.danieltuerk.controlcenter.shared.AbstractItemEvent;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

/**
 * @author Daniel Tuerk
 */
@Schema(description = "route update sent via WebSocket")
@Tag(ref = "websocket")
public class RouteDataChangedEvent extends AbstractItemEvent {

    private final ACTION_TYPE type;

    public RouteDataChangedEvent(long routeId, ACTION_TYPE type) {
        super(routeId);
        this.type = type;
    }

    public ACTION_TYPE getType() {
        return type;
    }

    @Override
    public String toString() {
        return "RouteDataChangedEvent{" +
            "type=" + type +
            ", itemId=" + itemId +
            '}';
    }
}
