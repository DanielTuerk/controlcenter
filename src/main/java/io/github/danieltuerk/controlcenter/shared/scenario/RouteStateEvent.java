package io.github.danieltuerk.controlcenter.shared.scenario;

import io.github.danieltuerk.controlcenter.shared.StateEvent;
import io.github.danieltuerk.controlcenter.shared.scenario.Route.ROUTE_RUN_STATE;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

/**
 * Event for the execution state of a {@link RouteSequence} in the {@link Scenario} execution.
 *
 * @param scenarioId      ID of the {@link Scenario}.
 * @param routeSequenceId ID of the {@link RouteSequence}.
 * @param state           Actual state.
 * @param message         Optional message.
 * @author Daniel Tuerk
 */
@Schema(description = "scenario status update sent via WebSocket")
@Tag(ref = "websocket")
public record RouteStateEvent(Long scenarioId, Long routeSequenceId, ROUTE_RUN_STATE state,
                              String message) implements StateEvent {

    public RouteStateEvent(Long scenarioId, Long routeSequenceId, ROUTE_RUN_STATE state) {
        this(scenarioId, routeSequenceId, state, null);
    }

    @Override
    public String getCacheKey() {
        return getClass().getName() + ":" + scenarioId + "-" + routeSequenceId;
    }

}
