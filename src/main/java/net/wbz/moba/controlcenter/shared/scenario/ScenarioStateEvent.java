package net.wbz.moba.controlcenter.shared.scenario;

import net.wbz.moba.controlcenter.shared.AbstractItemStateEvent;
import net.wbz.moba.controlcenter.shared.scenario.Scenario.RUN_STATE;

/**
 * @author Daniel Tuerk
 */
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

    public String getNextScheduleTimeText() {
        return nextScheduleTimeText;
    }

    public RUN_STATE getState() {
        return state;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("ScenarioStateEvent{");
        sb.append("state=").append(state);
        sb.append(", itemId=").append(itemId);
        sb.append(", nextScheduleTimeText=").append(nextScheduleTimeText);
        sb.append('}');
        return sb.toString();
    }
}
