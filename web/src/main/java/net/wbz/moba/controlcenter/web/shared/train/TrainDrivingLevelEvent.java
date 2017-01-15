package net.wbz.moba.controlcenter.web.shared.train;

import de.novanic.eventservice.client.event.Event;

/**
 * @author Daniel Tuerk
 */
public class TrainDrivingLevelEvent extends TrainStateEvent{
    private int speed;

    public TrainDrivingLevelEvent(long itemId, int speed) {
        super(itemId);
        this.speed = speed;
    }

    public TrainDrivingLevelEvent() {
    }

    public int getSpeed() {
        return speed;
    }

    @Override
    public String toString() {
        return "TrainDrivingLevelEvent{" +
                "speed=" + speed +
                "} " + super.toString();
    }
}
