package net.wbz.moba.controlcenter.web.server.persist.construction.track;

import net.wbz.moba.controlcenter.web.shared.Identity;
import net.wbz.moba.controlcenter.web.shared.track.model.Signal;

import java.util.HashMap;
import java.util.Map;

/**
 * TODO drop?
 * @author Daniel Tuerk
 */
public class SignalConfiguration implements Identity {
    Map<Signal.LIGHT, BusDataConfigurationEntity> lightsConfiguration;

    public Map<Signal.LIGHT, BusDataConfigurationEntity> getLightsConfiguration() {
        if (lightsConfiguration == null) {
            lightsConfiguration = new HashMap<>();
        }
        return lightsConfiguration;
    }

    public void setLightsConfiguration(Map<Signal.LIGHT, BusDataConfigurationEntity> lightsConfiguration) {
        this.lightsConfiguration = lightsConfiguration;
    }

    public BusDataConfigurationEntity get(Signal.LIGHT light) {
        return getLightsConfiguration().get(light);
    }


    @Override
    public Long getId() {
        return 0L;
    }
}
