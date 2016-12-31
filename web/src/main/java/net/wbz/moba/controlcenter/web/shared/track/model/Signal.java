package net.wbz.moba.controlcenter.web.shared.track.model;

import com.google.common.collect.Maps;
import com.google.gwt.user.client.rpc.IsSerializable;
import com.googlecode.jmapper.annotations.JMap;

import java.util.Map;
import java.util.Set;

/**
 * @author Daniel Tuerk
 */
public class Signal extends Straight implements HasToggleFunction {

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


    @JMap
    private TYPE type;

    @JMap
    private BusDataConfiguration signalConfigRed1;
    @JMap
    private BusDataConfiguration signalConfigRed2;
    @JMap
    private BusDataConfiguration signalConfigGreen1;
    @JMap
    private BusDataConfiguration signalConfigGreen2;
    @JMap
    private BusDataConfiguration signalConfigYellow1;
    @JMap
    private BusDataConfiguration signalConfigYellow2;
    @JMap
    private BusDataConfiguration signalConfigWhite;


    public TYPE getType() {
        return type;
    }

    public void setType(TYPE type) {
        this.type = type;
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

    @Override
    public Set<BusDataConfiguration> getConfigurationsOfFunctions() {
        Set<BusDataConfiguration> functions = super.getConfigurationsOfFunctions();
        //TODO needed or update to common usage?
        return functions;
    }

    /**
     * TODO doc or drop
     *
     * @return
     */
    public Map<LIGHT, BusDataConfiguration> getSignalConfiguration() {
        Map<LIGHT, BusDataConfiguration> lightConfig = Maps.newHashMap();
        lightConfig.put(LIGHT.RED1, signalConfigRed1);
        lightConfig.put(LIGHT.RED2, signalConfigRed2);
        lightConfig.put(LIGHT.GREEN1, signalConfigGreen1);
        lightConfig.put(LIGHT.GREEN2, signalConfigGreen2);
        lightConfig.put(LIGHT.YELLOW1, signalConfigYellow1);
        lightConfig.put(LIGHT.YELLOW2, signalConfigYellow2);
        lightConfig.put(LIGHT.WHITE, signalConfigWhite);
        return lightConfig;
    }

    /**
     * Available lights of the different signal types.
     */
    public enum LIGHT {
        RED1, RED2, GREEN1, GREEN2, YELLOW1, YELLOW2, WHITE
    }


    public BusDataConfiguration getSignalConfigRed1() {
        return signalConfigRed1;
    }

    public void setSignalConfigRed1(BusDataConfiguration signalConfigRed1) {
        this.signalConfigRed1 = signalConfigRed1;
    }

    public BusDataConfiguration getSignalConfigRed2() {
        return signalConfigRed2;
    }

    public void setSignalConfigRed2(BusDataConfiguration signalConfigRed2) {
        this.signalConfigRed2 = signalConfigRed2;
    }

    public BusDataConfiguration getSignalConfigGreen1() {
        return signalConfigGreen1;
    }

    public void setSignalConfigGreen1(BusDataConfiguration signalConfigGreen1) {
        this.signalConfigGreen1 = signalConfigGreen1;
    }

    public BusDataConfiguration getSignalConfigGreen2() {
        return signalConfigGreen2;
    }

    public void setSignalConfigGreen2(BusDataConfiguration signalConfigGreen2) {
        this.signalConfigGreen2 = signalConfigGreen2;
    }

    public BusDataConfiguration getSignalConfigYellow1() {
        return signalConfigYellow1;
    }

    public void setSignalConfigYellow1(BusDataConfiguration signalConfigYellow1) {
        this.signalConfigYellow1 = signalConfigYellow1;
    }

    public BusDataConfiguration getSignalConfigYellow2() {
        return signalConfigYellow2;
    }

    public void setSignalConfigYellow2(BusDataConfiguration signalConfigYellow2) {
        this.signalConfigYellow2 = signalConfigYellow2;
    }

    public BusDataConfiguration getSignalConfigWhite() {
        return signalConfigWhite;
    }

    public void setSignalConfigWhite(BusDataConfiguration signalConfigWhite) {
        this.signalConfigWhite = signalConfigWhite;
    }
}
