package net.wbz.moba.controlcenter.web.shared.viewer;

import de.novanic.eventservice.client.event.Event;
import net.wbz.moba.controlcenter.web.shared.track.model.Configuration;

/**
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
public class TrackPartStateEvent implements Event {

    private Configuration configuration;
    private boolean state;

    public TrackPartStateEvent() {
    }

    public TrackPartStateEvent(Configuration configuration, boolean state) {
        this.configuration = configuration;
        this.state = state;
    }

    public boolean isOn() {
        return state;
    }

    public Configuration getConfiguration() {
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
