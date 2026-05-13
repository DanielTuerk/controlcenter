package net.wbz.moba.controlcenter.shared.track.model;

import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Daniel Tuerk
 */
@Schema(
    description = "type for a track part",
    allOf = {Straight.class}
)
@Tag(ref = "track")
public class Signal extends Straight {

    private SIGNAL_TYPE type;
    private BusDataConfiguration signalConfigRed1;
    private BusDataConfiguration signalConfigRed2;
    private BusDataConfiguration signalConfigGreen1;
    private BusDataConfiguration signalConfigGreen2;
    private BusDataConfiguration signalConfigYellow1;
    private BusDataConfiguration signalConfigYellow2;
    private BusDataConfiguration signalConfigWhite;

    private TrackBlock stopBlock;

    public SIGNAL_TYPE getType() {
        return type;
    }

    public void setType(SIGNAL_TYPE type) {
        this.type = type;
    }

    /**
     * Return {@link BusDataConfiguration} for the given {@link LIGHT} or {@code null}.
     *
     * @param light {@link LIGHT}
     * @return {@link BusDataConfiguration} of given light
     */
    public BusDataConfiguration getSignalConfiguration(LIGHT light) {
        return switch (light) {
            case RED1 -> signalConfigRed1;
            case RED2 -> signalConfigRed2;
            case GREEN1 -> signalConfigGreen1;
            case GREEN2 -> signalConfigGreen2;
            case YELLOW1 -> signalConfigYellow1;
            case YELLOW2 -> signalConfigYellow2;
            case WHITE -> signalConfigWhite;
        };
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

    public Map<LIGHT, BusDataConfiguration> getSignalLightsConfigurations(SIGNAL_TYPE type) {
        Map<LIGHT, BusDataConfiguration> lightConfigs = new HashMap<>();
        for (LIGHT light : type.getLights()) {
            lightConfigs.put(light, getSignalConfiguration(light));
        }
        return lightConfigs;
    }

    public TrackBlock getStopBlock() {
        return stopBlock;
    }

    public void setStopBlock(TrackBlock stopBlock) {
        this.stopBlock = stopBlock;
    }

    /**
     * Types of signal with corresponding mapping of the lights.
     */
    public enum SIGNAL_TYPE {
        BLOCK(new LIGHT[]{LIGHT.RED1, LIGHT.GREEN1}),
        ENTER(new LIGHT[]{LIGHT.RED1, LIGHT.GREEN1, LIGHT.YELLOW1}),
        EXIT(new LIGHT[]{LIGHT.RED1, LIGHT.RED2, LIGHT.GREEN1, LIGHT.YELLOW1, LIGHT.WHITE}),
        BEFORE(new LIGHT[]{LIGHT.GREEN1, LIGHT.GREEN2, LIGHT.YELLOW1, LIGHT.YELLOW2});

        private final LIGHT[] lights;

        SIGNAL_TYPE(LIGHT[] lights) {
            this.lights = lights;
        }

        public LIGHT[] getLights() {
            return lights;
        }
    }

    /**
     * Available functions for the signal types.
     */
    public enum FUNCTION {
        HP0, HP1, HP2, HP0_SH1
    }

    @Override
    public String toString() {
        return "Signal{type=%s, id=%d, stopBlock=%s}".formatted(type, getId(), stopBlock);
    }

    /**
     * Available lights of the different signal types.
     */
    public enum LIGHT {
        RED1, RED2, GREEN1, GREEN2, YELLOW1, YELLOW2, WHITE
    }
}
