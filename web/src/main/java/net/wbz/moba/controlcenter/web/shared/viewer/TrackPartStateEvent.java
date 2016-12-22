package net.wbz.moba.controlcenter.web.shared.viewer;

import de.novanic.eventservice.client.event.Event;
import net.wbz.moba.controlcenter.web.shared.track.model.TrackPartConfiguration;

/**
 * @author Daniel Tuerk
 */
public class TrackPartStateEvent implements Event {

    private TrackPartConfiguration configuration;
    private boolean state;

    public TrackPartStateEvent() {
    }

    public TrackPartStateEvent(TrackPartConfiguration configuration, boolean state) {
        this.configuration = configuration;
        this.state = state;
    }

    public boolean isOn() {
        return state;
    }

    public TrackPartConfiguration getConfiguration() {
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
