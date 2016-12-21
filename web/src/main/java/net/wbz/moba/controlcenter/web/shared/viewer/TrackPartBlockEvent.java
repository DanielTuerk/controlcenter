package net.wbz.moba.controlcenter.web.shared.viewer;

import de.novanic.eventservice.client.event.Event;
import net.wbz.moba.controlcenter.web.server.persist.construction.track.TrackPartConfigurationEntity;

/**
 * Created by Daniel on 18.04.2014.
 */
public class TrackPartBlockEvent implements Event {

    public TrackPartBlockEvent() {
    }

    public enum STATE { UNKNOWN, FREE, USED}

    private TrackPartConfigurationEntity config;
    private STATE state;

    public TrackPartBlockEvent(TrackPartConfigurationEntity config, STATE state) {
        this.config = config;
        this.state = state;
    }

    public TrackPartConfigurationEntity getConfig() {
        return config;
    }

    public void setConfig(TrackPartConfigurationEntity config) {
        this.config = config;
    }

    public STATE getState() {
        return state;
    }

    public void setState(STATE state) {
        this.state = state;
    }
}
