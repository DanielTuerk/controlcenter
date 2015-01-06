package net.wbz.moba.controlcenter.web.shared.track.model;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Daniel Tuerk
 */
public class EventConfiguration implements IsSerializable, Serializable {

    private Map<Boolean, Configuration> stateConfigurations = new HashMap<>();

    public void setStateOnConfig(Configuration config) {
        addStateConfig(true, config);
    }

    public void setStateOffConfig(Configuration config) {
        addStateConfig(false, config);
    }

    private void addStateConfig(boolean state, Configuration config) {
        stateConfigurations.put(state, config);
    }

    public Configuration getStateOnConfig() {
        return getStateConfiguration(true);
    }

    private Configuration getStateConfiguration(boolean state) {
        if (!stateConfigurations.containsKey(state)) {
            stateConfigurations.put(state, new Configuration());
        }
        return stateConfigurations.get(state);
    }

    public Configuration getStateOffConfig() {
        return getStateConfiguration(false);
    }

    public boolean isActive() {
        return !stateConfigurations.isEmpty();
    }
}
