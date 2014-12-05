package net.wbz.moba.controlcenter.web.shared.viewer;

import de.novanic.eventservice.client.event.Event;
import net.wbz.moba.controlcenter.web.shared.track.model.Configuration;

/**
 * Event for the state change of the rail voltage.
 *
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
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
}
