package net.wbz.moba.controlcenter.web.shared.viewer;

import de.novanic.eventservice.client.event.Event;
import net.wbz.moba.controlcenter.web.shared.track.model.BusDataConfiguration;

/**
 * Created by Daniel on 18.04.2014.
 */
public class TrackPartBlockEvent implements Event {

    private BusDataConfiguration config;
    private STATE state;

    public TrackPartBlockEvent() {
    }

    public TrackPartBlockEvent(BusDataConfiguration config, STATE state) {
        this.config = config;
        this.state = state;
    }

    public BusDataConfiguration getConfig() {
        return config;
    }

    public void setConfig(BusDataConfiguration config) {
        this.config = config;
    }

    public STATE getState() {
        return state;
    }

    public void setState(STATE state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "TrackPartBlockEvent{" +
                "config=" + config +
                ", state=" + state +
                '}';
    }

    public enum STATE {
        UNKNOWN, FREE, USED
    }
}
