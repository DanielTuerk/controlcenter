package net.wbz.moba.controlcenter.web.shared.train;

import net.wbz.moba.controlcenter.web.shared.AbstractItemStateEvent;

/**
 * Created by Daniel on 08.03.14.
 */
public class TrainStateEvent extends AbstractItemStateEvent {

    public TrainStateEvent(long itemId) {
        super(itemId);
    }

    public TrainStateEvent() {
    }

    @Override
    public String toString() {
        return "TrainStateEvent{} " + super.toString();
    }
}
