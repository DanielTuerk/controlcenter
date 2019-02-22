package net.wbz.moba.controlcenter.web.shared.train;

import net.wbz.moba.controlcenter.web.shared.AbstractItemEvent;

/**
 * Event for modified data of {@link Train} or created/deleted entity.
 *
 * @author Daniel Tuerk
 */
public class TrainDataChangedEvent extends AbstractItemEvent {

    public TrainDataChangedEvent(long trainId) {
        super(trainId);
    }

    public TrainDataChangedEvent() {
    }

    @Override
    public String toString() {
        return "TrainDataChangedEvent{} " + super.toString();
    }
}
