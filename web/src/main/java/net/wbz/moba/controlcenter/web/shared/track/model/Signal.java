package net.wbz.moba.controlcenter.web.shared.track.model;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * Widget to show and control a signal.
 * <p/>
 * s@author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
public class Signal extends Straight {

    /**
     * Available lights of the different signal types.
     */
    public enum LIGHT {
        RED1, RED2, GREEN1, GREEN2, YELLOW1, YELLOW2, WHITE
    }

    /**
     * Available functions for the signal types.
     */
    public enum FUNCTION {
        HP0, HP1, HP2, HP0_SH1
    }

    /**
     * Types of signal with corresponding mapping of the lights.
     */
    public enum TYPE {
        BLOCK(new LIGHT[]{LIGHT.RED1, LIGHT.GREEN1}),
        ENTER(new LIGHT[]{LIGHT.RED1, LIGHT.GREEN1, LIGHT.YELLOW1}),
        EXIT(new LIGHT[]{LIGHT.RED1, LIGHT.RED2, LIGHT.GREEN1, LIGHT.YELLOW1, LIGHT.WHITE}),
        BEFORE(new LIGHT[]{LIGHT.GREEN1, LIGHT.GREEN2, LIGHT.YELLOW1, LIGHT.YELLOW2});

        private LIGHT[] lights;

        TYPE(LIGHT[] lights) {
            this.lights = lights;
        }

        public LIGHT[] getLights() {
            return lights;
        }
    }

    private TYPE type;

    public TYPE getType() {
        return type;
    }

    public void setType(TYPE type) {
        this.type = type;
    }

    /**
     * Wrapper to access the function configuration by the
     * {@link net.wbz.moba.controlcenter.web.shared.track.model.Signal.LIGHT} name.
     *
     * @param light {@link net.wbz.moba.controlcenter.web.shared.track.model.Signal.LIGHT}
     * @return {@link net.wbz.moba.controlcenter.web.shared.track.model.Configuration} or <code>null</code>
     */
    public Configuration getLightFunction(LIGHT light) {
        return getFunctionConfigs().get(light.name());
    }

    /**
     * Create or update the function configuration by the
     * {@link net.wbz.moba.controlcenter.web.shared.track.model.Signal.LIGHT} name.
     *
     * @param light         {@link net.wbz.moba.controlcenter.web.shared.track.model.Signal.LIGHT}
     * @param configuration {@link net.wbz.moba.controlcenter.web.shared.track.model.Configuration} or <code>null</code>
     */
    public void setLightFunctionConfig(LIGHT light, Configuration configuration) {
        getFunctionConfigs().put(light.name(), configuration);
    }

    public Map<LIGHT, Configuration> getSignalConfiguration() {
        Map<LIGHT, Configuration> lightConfig = Maps.newHashMap();
        for (Map.Entry<String, Configuration> functionConfig : getFunctionConfigs().entrySet()) {
            if(!DEFAULT_TOGGLE_FUNCTION.equals(functionConfig.getKey())) {
                lightConfig.put(LIGHT.valueOf(functionConfig.getKey()), functionConfig.getValue());
            }
        }
        return lightConfig;
    }

    @Override
    public String getConfigurationInfo() {
        return String.valueOf(getSignalConfiguration());
    }
}
