package net.wbz.moba.controlcenter.web.shared.viewer;

import de.novanic.eventservice.client.event.Event;
import net.wbz.moba.controlcenter.web.shared.track.model.BusDataConfiguration;

/**
 * @author Daniel Tuerk
 */
public class TrackPartStateEvent implements Event {

    private BusDataConfiguration configuration;
    private boolean state;

    public TrackPartStateEvent() {
    }

    public TrackPartStateEvent(BusDataConfiguration configuration, boolean state) {
        this.configuration = configuration;
        this.state = state;
    }

    public boolean isOn() {
        return state;
    }

    public BusDataConfiguration getConfiguration() {
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
