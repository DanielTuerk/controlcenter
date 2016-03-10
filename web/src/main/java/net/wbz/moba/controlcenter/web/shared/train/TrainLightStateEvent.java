package net.wbz.moba.controlcenter.web.shared.train;

/**
 * @author Daniel Tuerk
 */
public class TrainLightStateEvent extends TrainStateEvent {

    private boolean state;

    public TrainLightStateEvent(long itemId, boolean state) {
        super(itemId);
        this.state = state;
    }

    public TrainLightStateEvent() {
    }

    public boolean isState() {
        return state;
    }
}
