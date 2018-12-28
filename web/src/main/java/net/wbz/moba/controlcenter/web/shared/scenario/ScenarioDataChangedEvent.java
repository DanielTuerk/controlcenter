package net.wbz.moba.controlcenter.web.shared.scenario;

import de.novanic.eventservice.client.event.Event;

/**
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
