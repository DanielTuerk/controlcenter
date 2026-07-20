package io.github.danieltuerk.controlcenter.shared.train;

import io.github.danieltuerk.controlcenter.shared.AbstractItemStateEvent;

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
