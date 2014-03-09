package net.wbz.moba.controlcenter.web.shared.scenario;

import de.novanic.eventservice.client.event.Event;
import net.wbz.moba.controlcenter.web.shared.AbstractStateEvent;

/**
 * @author Daniel Tuerk (daniel.tuerk@jambit.com)
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
