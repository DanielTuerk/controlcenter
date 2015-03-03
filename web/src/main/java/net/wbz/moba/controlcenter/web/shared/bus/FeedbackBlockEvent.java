package net.wbz.moba.controlcenter.web.shared.bus;

import de.novanic.eventservice.client.event.Event;

/**
 * Event for train state at {@link net.wbz.selectrix4java.block.FeedbackBlockModule}.
 *
 * @author Daniel Tuerk
 */
public class FeedbackBlockEvent implements Event {

    public enum STATE {ENTER, EXIT}

    private int bus;
    private int address;
    private int block;
    private int train;
    private boolean direction;
    private STATE state;

    public FeedbackBlockEvent() {
    }

    public FeedbackBlockEvent(STATE state,int bus, int address, int block, int train, boolean direction) {
        this.state=state;
        this.bus = bus;
        this.address = address;
        this.block = block;
        this.train = train;
        this.direction = direction;
    }

    public STATE getState() {
        return state;
    }

    public int getBus() {
        return bus;
    }

    public int getAddress() {
        return address;
    }

    public int getBlock() {
        return block;
    }

    public int getTrain() {
        return train;
    }

    public boolean isDirection() {
        return direction;
    }
}
