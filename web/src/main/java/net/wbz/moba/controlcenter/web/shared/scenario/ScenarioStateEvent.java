package net.wbz.moba.controlcenter.web.shared.scenario;

import net.wbz.moba.controlcenter.web.shared.AbstractStateEvent;

/**
 * @author Daniel Tuerk
 */
public class ScenarioStateEvent extends AbstractStateEvent {

    public Scenario.RUN_STATE state;

    public ScenarioStateEvent() {
    }

    public ScenarioStateEvent(long scenarioId, Scenario.RUN_STATE state) {
        super(scenarioId);
        this.state = state;
    }

    public Scenario.RUN_STATE getState() {
        return state;
    }
}
