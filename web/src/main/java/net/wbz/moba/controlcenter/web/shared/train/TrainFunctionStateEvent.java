package net.wbz.moba.controlcenter.web.shared.train;

import java.util.Objects;

/**
 * @author Daniel Tuerk
 */
public class TrainFunctionStateEvent extends TrainStateEvent {

    private TrainFunction function;
    private boolean active;

    public TrainFunctionStateEvent(long itemId, TrainFunction function, boolean active) {
        super(itemId);
        this.function = function;
        this.active = active;
    }

    public TrainFunctionStateEvent() {
    }

    public TrainFunction getFunction() {
        return function;
    }

    public boolean isActive() {
        return active;
    }

    @Override
    public String getCacheKey() {
        return super.getCacheKey() + "-" + function.getConfiguration().getIdentifierKey();
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        TrainFunctionStateEvent that = (TrainFunctionStateEvent) o;
        return active == that.active && Objects.equals(function, that.function);
    }

    @Override
    public int hashCode() {

        return Objects.hash(super.hashCode(), function, active);
    }

    @Override
    public String toString() {
        return "TrainFunctionStateEvent{" + "function=" + function + ", active=" + active + "} " + super.toString();
    }
}
