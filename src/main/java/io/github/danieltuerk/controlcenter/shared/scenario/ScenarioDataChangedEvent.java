package io.github.danieltuerk.controlcenter.shared.scenario;

import io.github.danieltuerk.controlcenter.shared.AbstractItemEvent;
import lombok.Getter;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

/**
 * Event that indicates changes to the {@link Scenario}s data.
 *
 * @author Daniel Tuerk
 */
@Getter
@Schema(description = "scenario status update sent via WebSocket")
@Tag(ref = "websocket")
public class ScenarioDataChangedEvent extends AbstractItemEvent {

    private final ACTION_TYPE type;

    public ScenarioDataChangedEvent(long scenarioId, ACTION_TYPE type) {
        super(scenarioId);
        this.type = type;
    }

    @Override
    public String toString() {
        return "ScenarioDataChangedEvent{" +
            "type=" + type +
            ", itemId=" + itemId +
            '}';
    }
}
