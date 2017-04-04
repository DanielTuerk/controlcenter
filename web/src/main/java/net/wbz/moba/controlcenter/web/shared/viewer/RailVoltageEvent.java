package net.wbz.moba.controlcenter.web.shared.viewer;

import com.google.common.base.Objects;
import de.novanic.eventservice.client.event.Event;

/**
 * Event for the state change of the rail voltage.
 *
 * @author Daniel Tuerk
 */
public class RailVoltageEvent implements Event {

    private boolean state;

    public RailVoltageEvent() {
    }

    public RailVoltageEvent(boolean state) {
        this.state = state;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("state", state)
                .toString();
    }
}
