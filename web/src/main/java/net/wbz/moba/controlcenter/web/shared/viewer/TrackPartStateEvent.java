package net.wbz.moba.controlcenter.web.shared.viewer;

import de.novanic.eventservice.client.event.Event;
import net.wbz.moba.controlcenter.web.server.persist.construction.track.TrackPartConfigurationEntity;

/**
 * @author Daniel Tuerk
 */
public class TrackPartStateEvent implements Event {

    private TrackPartConfigurationEntity configuration;
    private boolean state;

    public TrackPartStateEvent() {
    }

    public TrackPartStateEvent(TrackPartConfigurationEntity configuration, boolean state) {
        this.configuration = configuration;
        this.state = state;
    }

    public boolean isOn() {
        return state;
    }

    public TrackPartConfigurationEntity getConfiguration() {
        return configuration;
    }

    @Override
    public String toString() {
        return "TrackPartStateEvent{" +
                "configuration=" + configuration +
                ", state=" + state +
                '}';
    }
}
