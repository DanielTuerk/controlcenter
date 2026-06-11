package net.wbz.moba.controlcenter.service.scenario;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import net.wbz.moba.controlcenter.EventBroadcaster;
import net.wbz.moba.controlcenter.shared.scenario.Scenario;
import net.wbz.moba.controlcenter.shared.scenario.ScenarioStateEvent;

@ApplicationScoped
public class ScenarioStateEventPublisher {
    private final EventBroadcaster eventBroadcaster;
    private final Event<ScenarioStateEvent> scenarioStateEvent;

    public ScenarioStateEventPublisher(EventBroadcaster eventBroadcaster, Event<ScenarioStateEvent> scenarioStateEvent) {
        this.eventBroadcaster = eventBroadcaster;
        this.scenarioStateEvent = scenarioStateEvent;
    }

    public void fireEvent(Long scenarioId, Scenario.RUN_STATE state) {
        final var event = new ScenarioStateEvent(scenarioId, state, null);
        scenarioStateEvent.fire(event);
        eventBroadcaster.fireEvent(event);
    }
}
