package net.wbz.moba.controlcenter.web.shared.viewer;

import java.util.Objects;

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
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TrackPartStateEvent that = (TrackPartStateEvent) o;
        return Objects.equals(configuration, that.configuration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(configuration);
    }

    @Override
    public String toString() {
        return "TrackPartStateEvent{" +
                "configuration=" + configuration +
                ", state=" + state +
                '}';
    }
}
