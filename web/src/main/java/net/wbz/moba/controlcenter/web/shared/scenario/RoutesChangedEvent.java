package net.wbz.moba.controlcenter.web.shared.scenario;

import com.google.common.base.Objects;

import de.novanic.eventservice.client.event.Event;

/**
 * @author Daniel Tuerk
 */
public class RoutesChangedEvent implements Event {

    public RoutesChangedEvent() {
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .toString();
    }
}
