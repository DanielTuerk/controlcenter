package net.wbz.moba.controlcenter.shared.scenario;

import net.wbz.moba.controlcenter.shared.Event;

/**
 * Event that indicates changes to the {@link Scenario}s data.
 *
 * @author Daniel Tuerk
 */
public class ScenarioDataChangedEvent implements Event {

    public ScenarioDataChangedEvent() {
    }

    @Override
    public String toString() {
        return "ScenarioDataChangedEvent{}";
    }
}
