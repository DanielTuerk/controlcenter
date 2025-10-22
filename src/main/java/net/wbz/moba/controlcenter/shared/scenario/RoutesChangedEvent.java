package net.wbz.moba.controlcenter.shared.scenario;

import net.wbz.moba.controlcenter.shared.AbstractItemEvent;
import net.wbz.moba.controlcenter.shared.Event;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

/**
 * @author Daniel Tuerk
 */
@Schema(description = "scenario status update sent via WebSocket")
@Tag(ref = "websocket")
public class RoutesChangedEvent extends AbstractItemEvent {

    public RoutesChangedEvent(long routeId) {
        super(routeId);
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("RoutesChangedEvent{");
        sb.append('}');
        return sb.toString();
    }
}
