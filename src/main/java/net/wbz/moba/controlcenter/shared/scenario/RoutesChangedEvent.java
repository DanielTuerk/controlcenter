package net.wbz.moba.controlcenter.shared.scenario;

import net.wbz.moba.controlcenter.shared.Event;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

/**
 * Event that indicates changes to the {@link Route}s.
 *
 * @author Daniel Tuerk
 */
@Schema(description = "routes changed update sent via WebSocket")
@Tag(ref = "websocket")
public record RoutesChangedEvent(boolean dirty) implements Event {
}
