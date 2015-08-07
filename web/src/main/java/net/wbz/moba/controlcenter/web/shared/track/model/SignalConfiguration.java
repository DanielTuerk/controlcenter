package net.wbz.moba.controlcenter.web.shared.track.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Daniel Tuerk
 */
public class SignalConfiguration implements Serializable {
    Map<Signal.LIGHT, Configuration> lightsConfiguration;

    public Map<Signal.LIGHT, Configuration> getLightsConfiguration() {
        if (lightsConfiguration == null) {
            lightsConfiguration = new HashMap<>();
        }
        return lightsConfiguration;
    }

    public void setLightsConfiguration(Map<Signal.LIGHT, Configuration> lightsConfiguration) {
        this.lightsConfiguration = lightsConfiguration;
    }

    public Configuration get(Signal.LIGHT light) {
        return getLightsConfiguration().get(light);
    }
}
