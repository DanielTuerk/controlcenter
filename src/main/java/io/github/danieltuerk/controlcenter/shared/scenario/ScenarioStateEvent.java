package io.github.danieltuerk.controlcenter.shared.scenario;

import io.github.danieltuerk.controlcenter.shared.AbstractItemStateEvent;
import io.github.danieltuerk.controlcenter.shared.scenario.Scenario.RUN_STATE;
import lombok.Getter;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

/**
 * @author Daniel Tuerk
 */
@Getter
@Schema(description = "scenario status update sent via WebSocket")
@Tag(ref = "websocket")
public class ScenarioStateEvent extends AbstractItemStateEvent {

    public RUN_STATE state;

    public ScenarioStateEvent() {
    }

    public ScenarioStateEvent(long scenarioId, RUN_STATE state) {
        super(scenarioId);
        this.state = state;
    }

    @Override
    public String toString() {
        return "ScenarioStateEvent{" + "state=" + state +
                ", itemId=" + itemId +
                '}';
    }
}
