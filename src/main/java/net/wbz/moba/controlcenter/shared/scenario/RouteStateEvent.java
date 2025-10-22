package net.wbz.moba.controlcenter.shared.scenario;

import net.wbz.moba.controlcenter.shared.StateEvent;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

/**
 * Event for the execution state of a {@link RouteSequence} in the {@link Scenario} execution.
 * 
 * @author Daniel Tuerk
 */
@Schema(description = "scenario status update sent via WebSocket")
@Tag(ref = "websocket")
public class RouteStateEvent implements StateEvent {

    /**
     * Id of the {@link Scenario}.
     */
    private Long scenarioId;
    /**
     * Id of the {@link RouteSequence}.
     */
    private Long routeSequenceId;
    /**
     * Actual state.
     */
    private STATE state;
    /**
     * Optional message.
     */
    private String message;

    public RouteStateEvent() {
    }

    public RouteStateEvent(Long scenarioId, Long routeSequenceId, STATE state) {
        this(scenarioId, routeSequenceId, state, null);
    }

    public RouteStateEvent(Long scenarioId, Long routeSequenceId, STATE state, String msg) {
        this.scenarioId = scenarioId;
        this.routeSequenceId = routeSequenceId;
        this.state = state;
        this.message = msg;
    }

    public Long getScenarioId() {
        return scenarioId;
    }

    public Long getRouteSequenceId() {
        return routeSequenceId;
    }

    public STATE getState() {
        return state;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String getCacheKey() {
        return getClass().getName() + ":" + scenarioId + "-" + routeSequenceId;
    }

    @Override
    public String toString() {
        return "RouteStateEvent{" +
                "scenarioId=" + scenarioId +
                ", routeSequenceId=" + routeSequenceId +
                ", state=" + state +
                '}';
    }

    public enum STATE {
        WAITING, RUNNING, FINISHED, FAILED
    }
}
