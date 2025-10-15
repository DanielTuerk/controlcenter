package net.wbz.moba.controlcenter.shared.bus;

import net.wbz.moba.controlcenter.shared.StateEvent;

/**
 * Event for the state of the {@link net.wbz.selectrix4java.data.recording.BusDataPlayer}.
 *
 * @author Daniel Tuerk
 */
public class PlayerEvent implements StateEvent {

    private STATE state;

    public PlayerEvent() {
    }

    public PlayerEvent(STATE state) {
        this.state = state;
    }

    public STATE getState() {
        return state;
    }

    public enum STATE {
        START, STOP
    }

}
