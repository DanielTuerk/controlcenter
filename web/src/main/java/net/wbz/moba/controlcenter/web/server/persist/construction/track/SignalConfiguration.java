package net.wbz.moba.controlcenter.web.server.persist.construction.track;

import net.wbz.moba.controlcenter.web.shared.Identity;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Daniel Tuerk
 */
public class SignalConfiguration implements Identity {
    Map<SignalEntity.LIGHT, TrackPartConfigurationEntity> lightsConfiguration;

    public Map<SignalEntity.LIGHT, TrackPartConfigurationEntity> getLightsConfiguration() {
        if(lightsConfiguration==null) {
            lightsConfiguration=new HashMap<>();
        }
        return lightsConfiguration;
    }

    public void setLightsConfiguration(Map<SignalEntity.LIGHT, TrackPartConfigurationEntity> lightsConfiguration) {
        this.lightsConfiguration = lightsConfiguration;
    }

    public TrackPartConfigurationEntity get(SignalEntity.LIGHT light) {
        return getLightsConfiguration().get(light);
    }


    @Override
    public Long getId() {
        return 0L;
    }
}
