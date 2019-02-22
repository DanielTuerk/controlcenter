package net.wbz.moba.controlcenter.web.shared.scenario;

import net.wbz.moba.controlcenter.web.shared.AbstractItemStateEvent;
import net.wbz.moba.controlcenter.web.shared.scenario.Scenario.RUN_STATE;

/**
 * @author Daniel Tuerk
 */
public class ScenarioStateEvent extends AbstractItemStateEvent {

    public RUN_STATE state;

    public ScenarioStateEvent() {
    }

    public ScenarioStateEvent(long scenarioId, RUN_STATE state) {
        super(scenarioId);
        this.state = state;
    }

    public RUN_STATE getState() {
        return state;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("ScenarioStateEvent{");
        sb.append("state=").append(state);
        sb.append(", itemId=").append(itemId);
        sb.append('}');
        return sb.toString();
    }
}
