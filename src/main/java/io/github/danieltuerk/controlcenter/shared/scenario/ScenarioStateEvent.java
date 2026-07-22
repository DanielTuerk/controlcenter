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
    private String nextScheduleTimeText;

    public ScenarioStateEvent() {
    }

    public ScenarioStateEvent(long scenarioId, RUN_STATE state, String nextScheduleTimeText) {
        super(scenarioId);
        this.state = state;
        this.nextScheduleTimeText = nextScheduleTimeText;
    }

    @Override
    public String toString() {
        return "ScenarioStateEvent{" + "state=" + state +
                ", itemId=" + itemId +
                ", nextScheduleTimeText=" + nextScheduleTimeText +
                '}';
    }
}
