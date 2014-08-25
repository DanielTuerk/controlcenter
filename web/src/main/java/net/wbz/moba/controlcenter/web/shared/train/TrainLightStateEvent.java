package net.wbz.moba.controlcenter.web.shared.train;

/**
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
public class TrainLightStateEvent extends TrainStateEvent {

    private boolean state;

    public TrainLightStateEvent(boolean b) {
        state=b;
    }

    public TrainLightStateEvent() {
    }

    public boolean isState() {
        return state;
    }
}
