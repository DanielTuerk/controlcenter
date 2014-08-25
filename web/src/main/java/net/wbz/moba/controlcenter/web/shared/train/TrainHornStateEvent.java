package net.wbz.moba.controlcenter.web.shared.train;

import de.novanic.eventservice.client.event.Event;

/**
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
public class TrainHornStateEvent extends TrainStateEvent {

    private boolean state;

    public TrainHornStateEvent(boolean b) {
        state=b;
    }

    public TrainHornStateEvent() {
    }

    public boolean isState() {
        return state;
    }
}
