package net.wbz.moba.controlcenter.web.shared.scenario;

import com.google.common.base.Objects;

import net.wbz.moba.controlcenter.web.shared.AbstractStateEvent;
import net.wbz.moba.controlcenter.web.shared.scenario.Scenario.RUN_STATE;

/**
 * @author Daniel Tuerk
 */
public class ScenarioStateEvent extends AbstractStateEvent {

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
        return Objects.toStringHelper(this)
                .add("state", state)
                .toString();
    }
}
