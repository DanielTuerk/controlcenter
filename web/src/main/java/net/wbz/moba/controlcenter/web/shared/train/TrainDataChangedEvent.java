package net.wbz.moba.controlcenter.web.shared.train;

import de.novanic.eventservice.client.event.Event;

/**
 * Event for modified data of {@link net.wbz.moba.controlcenter.web.shared.train.Train} or created/deleted entity.
 *
 * @author Daniel Tuerk
 */
public class TrainDataChangedEvent implements Event {

    private long trainId;

    public TrainDataChangedEvent(long trainId) {
        this.trainId = trainId;
    }

    public TrainDataChangedEvent() {
    }

    public long getTrainId() {
        return trainId;
    }
}
