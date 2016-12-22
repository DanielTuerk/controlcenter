package net.wbz.moba.controlcenter.web.shared.track.model;

import net.wbz.moba.controlcenter.web.server.persist.construction.track.TrackPartConfigurationEntity;

/**
 * @author Daniel Tuerk
 */
public class EventConfiguration extends AbstractDto {

    private TrackPartConfiguration stateOnConfig;
    private TrackPartConfiguration stateOffConfig;

    public TrackPartConfiguration getStateOnConfig() {
        return stateOnConfig;
    }

    public void setStateOnConfig(TrackPartConfiguration stateOnConfig) {
        this.stateOnConfig = stateOnConfig;
    }

    public TrackPartConfiguration getStateOffConfig() {
        return stateOffConfig;
    }

    public void setStateOffConfig(TrackPartConfiguration stateOffConfig) {
        this.stateOffConfig = stateOffConfig;
    }
}
