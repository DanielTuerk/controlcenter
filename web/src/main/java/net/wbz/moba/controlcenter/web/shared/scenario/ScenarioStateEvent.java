package net.wbz.moba.controlcenter.web.shared.scenario;

import de.novanic.eventservice.client.event.Event;

/**
 * @author Daniel Tuerk (daniel.tuerk@jambit.com)
 */
public class ScenarioStateEvent implements Event {

    public long scenarioId;
    public Scenario.RUN_STATE state;

    public ScenarioStateEvent() {
    }

    public ScenarioStateEvent(long scenarioId, Scenario.RUN_STATE state) {
        this.scenarioId = scenarioId;
        this.state = state;
    }

    public long getScenarioId() {
        return scenarioId;
    }

    public Scenario.RUN_STATE getState() {
        return state;
    }
}
