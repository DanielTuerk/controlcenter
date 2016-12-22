package net.wbz.moba.controlcenter.web.server.persist.construction.track;

import net.wbz.moba.controlcenter.web.shared.Identity;
import net.wbz.moba.controlcenter.web.shared.track.model.Signal;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Daniel Tuerk
 */
public class SignalConfiguration implements Identity {
    Map<Signal.LIGHT, TrackPartConfigurationEntity> lightsConfiguration;

    public Map<Signal.LIGHT, TrackPartConfigurationEntity> getLightsConfiguration() {
        if (lightsConfiguration == null) {
            lightsConfiguration = new HashMap<>();
        }
        return lightsConfiguration;
    }

    public void setLightsConfiguration(Map<Signal.LIGHT, TrackPartConfigurationEntity> lightsConfiguration) {
        this.lightsConfiguration = lightsConfiguration;
    }

    public TrackPartConfigurationEntity get(Signal.LIGHT light) {
        return getLightsConfiguration().get(light);
    }


    @Override
    public Long getId() {
        return 0L;
    }
}
