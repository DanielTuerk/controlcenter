package net.wbz.moba.controlcenter.web.shared.train;

/**
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
public class TrainFunctionStateEvent extends TrainStateEvent {
    private TrainFunction.FUNCTION function;
    private boolean active;

    public TrainFunctionStateEvent(long itemId, TrainFunction.FUNCTION function, boolean active) {
        super(itemId);
        this.function = function;
        this.active = active;
    }

    public TrainFunctionStateEvent() {
    }

    public TrainFunction.FUNCTION getFunction() {
        return function;
    }

    public boolean isActive() {
        return active;
    }
}
