package net.wbz.moba.controlcenter.web.shared.track.model;

import com.google.common.collect.Maps;
import com.google.gwt.user.client.rpc.IsSerializable;

import java.util.Map;

/**
 * @author Daniel Tuerk
 */
public class Signal extends Straight implements HasToggleFunction {

    private TYPE type;

    public TYPE getType() {
        return type;
    }

    public void setType(TYPE type) {
        this.type = type;
    }
    public Map<LIGHT, BusDataConfiguration> getSignalConfiguration() {
        //TODO
        Map<LIGHT, BusDataConfiguration> lightConfig = Maps.newHashMap();
        return lightConfig;
    }

    @Override
    public BusDataConfiguration getToggleFunction() {
        return null;
    }

    @Override
    public void setToggleFunction(BusDataConfiguration toggleFunction) {
    }

    @Override
    public EventConfiguration getEventConfiguration() {
        return null;
    }

    @Override
    public void setEventConfiguration(EventConfiguration eventConfiguration) {

    }

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
    public enum TYPE implements IsSerializable {
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

}
