package net.wbz.moba.controlcenter.web.shared.train;

import de.novanic.eventservice.client.event.Event;

/**
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
public class TrainDrivingLevelEvent extends TrainStateEvent{
    private int speed;

    public TrainDrivingLevelEvent(int speed) {
        this.speed = speed;
    }

    public TrainDrivingLevelEvent() {
    }

    public int getSpeed() {
        return speed;
    }
}