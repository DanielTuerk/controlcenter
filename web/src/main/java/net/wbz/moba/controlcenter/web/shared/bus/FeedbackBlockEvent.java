package net.wbz.moba.controlcenter.web.shared.bus;

import com.google.common.base.Objects;

import de.novanic.eventservice.client.event.Event;

/**
 * Event for train state at {@link net.wbz.selectrix4java.block.FeedbackBlockModule}.
 *
 * @author Daniel Tuerk
 */
public class FeedbackBlockEvent implements Event {

    private int bus;
    private int address;
    private int block;
    private int train;
    private boolean direction;
    private STATE state;

    public FeedbackBlockEvent() {
    }

    public FeedbackBlockEvent(STATE state, int bus, int address, int block, int train, boolean direction) {
        this.state = state;
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

    @Override
    public boolean equals(Object o) {
        // TODO check that not all states are in cache forever, becasue they will
        // only state is not checked
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FeedbackBlockEvent that = (FeedbackBlockEvent) o;
        return super.equals(o) && getBus() == that.getBus() &&
                getAddress() == that.getAddress() &&
                getBlock() == that.getBlock() &&
                getTrain() == that.getTrain() &&
                isDirection() == that.isDirection();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), getBus(), getAddress(), getBlock(), getTrain(), isDirection());
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("bus", bus)
                .add("address", address)
                .add("block", block)
                .add("train", train)
                .add("direction", direction)
                .add("state", state)
                .toString();
    }

    public enum STATE {
        ENTER, EXIT
    }
}
