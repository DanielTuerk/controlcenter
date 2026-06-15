package net.wbz.moba.controlcenter.shared.track.model;

import lombok.Getter;
import lombok.Setter;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Daniel Tuerk
 */
@Setter
@Getter
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

    public Map<LIGHT, BusDataConfiguration> getSignalLightsConfigurations(SIGNAL_TYPE type) {
        Map<LIGHT, BusDataConfiguration> lightConfigs = new HashMap<>();
        for (LIGHT light : type.getLights()) {
            lightConfigs.put(light, getSignalConfiguration(light));
        }
        return lightConfigs;
    }

    /**
     * Types of signal with corresponding mapping of the lights.
     */
    @Getter
    public enum SIGNAL_TYPE {
        BLOCK(new LIGHT[]{LIGHT.RED1, LIGHT.GREEN1}),
        ENTER(new LIGHT[]{LIGHT.RED1, LIGHT.GREEN1, LIGHT.YELLOW1}),
        EXIT(new LIGHT[]{LIGHT.RED1, LIGHT.RED2, LIGHT.GREEN1, LIGHT.YELLOW1, LIGHT.WHITE}),
        BEFORE(new LIGHT[]{LIGHT.GREEN1, LIGHT.GREEN2, LIGHT.YELLOW1, LIGHT.YELLOW2});

        private final LIGHT[] lights;

        SIGNAL_TYPE(LIGHT[] lights) {
            this.lights = lights;
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
