package net.wbz.moba.controlcenter.web.shared.scenario;

import de.novanic.eventservice.client.event.Event;

/**
 * @author Daniel Tuerk
 */
public class RoutesChangedEvent implements Event {

    public RoutesChangedEvent() {
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("RoutesChangedEvent{");
        sb.append('}');
        return sb.toString();
    }
}
