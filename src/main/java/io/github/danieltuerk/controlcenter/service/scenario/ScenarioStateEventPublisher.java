package io.github.danieltuerk.controlcenter.service.scenario;

import io.github.danieltuerk.controlcenter.EventBroadcaster;
import io.github.danieltuerk.controlcenter.shared.scenario.Scenario;
import io.github.danieltuerk.controlcenter.shared.scenario.ScenarioStateEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;

@ApplicationScoped
public class ScenarioStateEventPublisher {
    private final EventBroadcaster eventBroadcaster;
    private final Event<ScenarioStateEvent> scenarioStateEvent;

    public ScenarioStateEventPublisher(EventBroadcaster eventBroadcaster, Event<ScenarioStateEvent> scenarioStateEvent) {
        this.eventBroadcaster = eventBroadcaster;
        this.scenarioStateEvent = scenarioStateEvent;
    }

    public void fireEvent(Long scenarioId, Scenario.RUN_STATE state) {
        final var event = new ScenarioStateEvent(scenarioId, state);
        scenarioStateEvent.fire(event);
        eventBroadcaster.fireEvent(event);
    }
}
