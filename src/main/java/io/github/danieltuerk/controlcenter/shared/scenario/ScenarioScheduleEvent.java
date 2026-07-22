package io.github.danieltuerk.controlcenter.shared.scenario;

import io.github.danieltuerk.controlcenter.shared.Event;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.Optional;

/**
 * Event that indicates change to the scheduling of a {@link Scenario}s.
 *
 * @author Daniel Tuerk
 */
@Schema(description = "scenario scheduling state sent via WebSocket")
@Tag(ref = "websocket")
public record ScenarioScheduleEvent(long scenarioId, boolean on, Optional<String> cron) implements Event {

    public static ScenarioScheduleEvent scheduled(long scenarioId, String cron) {
        return new ScenarioScheduleEvent(scenarioId, true, Optional.of(cron));
    }

    public static ScenarioScheduleEvent unscheduled(long scenarioId) {
        return new ScenarioScheduleEvent(scenarioId, false, Optional.empty());
    }

}
