package net.wbz.moba.controlcenter.web.shared.scenario;

import de.novanic.eventservice.client.event.Event;

/**
 * @author Daniel Tuerk
 */
public class ScenariosChangedEvent implements Event {

    public ScenariosChangedEvent() {
    }

    @Override
    public String toString() {
        return "ScenariosChangedEvent{}";
    }
}
