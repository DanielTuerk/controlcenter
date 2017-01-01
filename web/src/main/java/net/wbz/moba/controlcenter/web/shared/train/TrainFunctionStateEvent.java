package net.wbz.moba.controlcenter.web.shared.train;

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
    public String toString() {
        return "TrainFunctionStateEvent{" +
                "function=" + function +
                ", active=" + active +
                "} " + super.toString();
    }
}
