package io.github.danieltuerk.controlcenter.shared.scenario;

import io.github.danieltuerk.controlcenter.shared.AbstractItemEvent;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

/**
 * Event that indicates changes to the {@link Scenario}s data.
 *
 * @author Daniel Tuerk
 */
@Schema(description = "scenario status update sent via WebSocket")
@Tag(ref = "websocket")
public class ScenarioDataChangedEvent extends AbstractItemEvent {

    private final ACTION_TYPE type;

    public ScenarioDataChangedEvent(long scenarioId, ACTION_TYPE type) {
        super(scenarioId);
        this.type = type;
    }

    public ACTION_TYPE getType() {
        return type;
    }

    @Override
    public String toString() {
        return "ScenarioDataChangedEvent{" +
            "type=" + type +
            ", itemId=" + itemId +
            '}';
    }
}
