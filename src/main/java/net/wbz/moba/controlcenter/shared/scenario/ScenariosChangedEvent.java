package net.wbz.moba.controlcenter.shared.scenario;

import net.wbz.moba.controlcenter.shared.Event;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

/**
 * Event that indicates changes to the {@link Scenario}s.
 *
 * @author Daniel Tuerk
 */
@Schema(description = "scenarios changed update sent via WebSocket")
@Tag(ref = "websocket")
public record ScenariosChangedEvent(boolean dirty) implements Event {
}
