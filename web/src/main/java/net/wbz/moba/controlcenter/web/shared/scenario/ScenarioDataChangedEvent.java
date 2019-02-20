package net.wbz.moba.controlcenter.web.shared.scenario;

import de.novanic.eventservice.client.event.Event;

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
