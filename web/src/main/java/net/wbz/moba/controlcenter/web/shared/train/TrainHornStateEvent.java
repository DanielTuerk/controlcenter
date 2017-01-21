package net.wbz.moba.controlcenter.web.shared.train;

/**
 * @author Daniel Tuerk
 */
public class TrainHornStateEvent extends TrainStateEvent {

    private boolean state;

    public TrainHornStateEvent(long itemId, boolean state) {
        super(itemId);
        this.state = state;
    }

    public TrainHornStateEvent() {
    }

    public boolean isState() {
        return state;
    }

    @Override
    public String toString() {
        return "TrainHornStateEvent{" +
                "state=" + state +
                "} " + super.toString();
    }

}
