package net.wbz.moba.controlcenter.web.shared.scenario;

import de.novanic.eventservice.client.event.Event;

/**
 * @author Daniel Tuerk
 */
public class StationDataChangedEvent implements Event {

    public StationDataChangedEvent() {
    }

    @Override
    public String toString() {
        return "StationDataChangedEvent{}";
    }
}
