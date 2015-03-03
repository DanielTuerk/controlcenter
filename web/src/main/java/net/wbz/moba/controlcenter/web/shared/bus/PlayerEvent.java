package net.wbz.moba.controlcenter.web.shared.bus;

import de.novanic.eventservice.client.event.Event;

/**
 * Event for the state of the {@link net.wbz.selectrix4java.data.recording.BusDataPlayer}.
 *
 * @author Daniel Tuerk
 */
public class PlayerEvent implements Event {

    public enum STATE {START, STOP}

    private STATE state;

    public PlayerEvent() {
    }

    public PlayerEvent(STATE state) {
        this.state = state;
    }

    public STATE getState() {
        return state;
    }

}
