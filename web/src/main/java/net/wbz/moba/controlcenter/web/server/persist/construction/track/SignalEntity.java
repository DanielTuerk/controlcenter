package net.wbz.moba.controlcenter.web.server.persist.construction.track;

import com.google.common.collect.Maps;
import com.googlecode.jmapper.annotations.JMap;
import net.wbz.moba.controlcenter.web.shared.track.model.AbstractTrackPart;
import net.wbz.moba.controlcenter.web.shared.track.model.Signal;
import net.wbz.moba.controlcenter.web.shared.track.model.TrackModelConstants;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Map;
import net.wbz.moba.controlcenter.web.shared.track.model.Signal.LIGHT;
import net.wbz.moba.controlcenter.web.shared.track.model.Signal.TYPE;

/**
 * Widget to show and control a signal.
 * <p/>
 * s@author Daniel Tuerk
 */
@Entity
@Table(name = "trackpart_signal")
public class SignalEntity extends StraightEntity {

//    /**
//     * Available lights of the different signal types.
//     */
//    public enum LIGHT {
//        RED1, RED2, GREEN1, GREEN2, YELLOW1, YELLOW2, WHITE
//    }

//    /**
//     * Available functions for the signal types.
//     */
//    public enum FUNCTION {
//        HP0, HP1, HP2, HP0_SH1
//    }
//
//    /**
//     * Types of signal with corresponding mapping of the lights.
//     */
//    public enum TYPE implements IsSerializable{
//        BLOCK(new LIGHT[]{LIGHT.RED1, LIGHT.GREEN1}),
//        ENTER(new LIGHT[]{LIGHT.RED1, LIGHT.GREEN1, LIGHT.YELLOW1}),
//        EXIT(new LIGHT[]{LIGHT.RED1, LIGHT.RED2, LIGHT.GREEN1, LIGHT.YELLOW1, LIGHT.WHITE}),
//        BEFORE(new LIGHT[]{LIGHT.GREEN1, LIGHT.GREEN2, LIGHT.YELLOW1, LIGHT.YELLOW2});
//
//        private LIGHT[] lights;
//
//        TYPE(LIGHT[] lights) {
//            this.lights = lights;
//        }
//
//        public LIGHT[] getLights() {
//            return lights;
//        }
//    }

    @JMap
    private TYPE type;

    public SignalEntity() {
    }

    public TYPE getType() {
        return type;
    }

    public void setType(TYPE type) {
        this.type = type;
    }

    /**
     * Wrapper to access the function configuration by the
     * {@link Signal.LIGHT} name.
     *
     * @param light {@link Signal.LIGHT}
     * @return {@link BusDataConfigurationEntity} or <code>null</code>
     */
    public BusDataConfigurationEntity getLightFunction(LIGHT light) {
        for (TrackPartFunctionEntity trackPartFunction : getFunctionConfigs()) {
            if (trackPartFunction.getFunctionKey().equalsIgnoreCase(light.name())) {
                return trackPartFunction.getConfiguration();
            }
        }
        return null;
    }

    /**
     * Create or update the function configuration by the
     * {@link Signal.LIGHT} name.
     *
     * @param light         {@link Signal.LIGHT}
     * @param configuration {@link BusDataConfigurationEntity} or <code>null</code>
     */
    public void setLightFunctionConfig(LIGHT light, BusDataConfigurationEntity configuration) {
        getFunctionConfigs().add(new TrackPartFunctionEntity(light.name(), configuration));
    }

    public Map<LIGHT, BusDataConfigurationEntity> getSignalConfiguration() {
        Map<LIGHT, BusDataConfigurationEntity> lightConfig = Maps.newHashMap();
        for (TrackPartFunctionEntity functionConfig : getFunctionConfigs()) {
            // TODO: refactor to signal function prefix
            if (!TrackModelConstants.DEFAULT_TOGGLE_FUNCTION.equals(functionConfig.getFunctionKey())
                    && !TrackModelConstants.DEFAULT_BLOCK_FUNCTION.equals(functionConfig.getFunctionKey())) {
                lightConfig.put(LIGHT.valueOf(functionConfig.getFunctionKey()), functionConfig.getConfiguration());
            }
        }
        return lightConfig;
    }

    @Override
    public Class<? extends AbstractTrackPart> getDefaultDtoClass() {
        return Signal.class;
    }
}
