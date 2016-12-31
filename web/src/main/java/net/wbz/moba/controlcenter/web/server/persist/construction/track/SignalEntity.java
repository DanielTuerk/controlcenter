package net.wbz.moba.controlcenter.web.server.persist.construction.track;

import com.google.common.collect.Maps;
import com.googlecode.jmapper.annotations.JMap;
import net.wbz.moba.controlcenter.web.shared.track.model.AbstractTrackPart;
import net.wbz.moba.controlcenter.web.shared.track.model.Signal;
import net.wbz.moba.controlcenter.web.shared.track.model.TrackModelConstants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
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
@Table(name = "TRACKPART_SIGNAL")
public class SignalEntity extends StraightEntity {

    @JMap
    @Column
    private TYPE type;


    @JMap
    @ManyToOne
    private BusDataConfigurationEntity signalConfigRed1;
    @JMap
    @ManyToOne
    private BusDataConfigurationEntity signalConfigRed2;
    @JMap
    @ManyToOne
    private BusDataConfigurationEntity signalConfigGreen1;
    @JMap
    @ManyToOne
    private BusDataConfigurationEntity signalConfigGreen2;
    @JMap
    @ManyToOne
    private BusDataConfigurationEntity signalConfigYellow1;
    @JMap
    @ManyToOne
    private BusDataConfigurationEntity signalConfigYellow2;
    @JMap
    @ManyToOne
    private BusDataConfigurationEntity signalConfigWhite;

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

    public BusDataConfigurationEntity getSignalConfigRed1() {
        return signalConfigRed1;
    }

    public void setSignalConfigRed1(BusDataConfigurationEntity signalConfigRed1) {
        this.signalConfigRed1 = signalConfigRed1;
    }

    public BusDataConfigurationEntity getSignalConfigRed2() {
        return signalConfigRed2;
    }

    public void setSignalConfigRed2(BusDataConfigurationEntity signalConfigRed2) {
        this.signalConfigRed2 = signalConfigRed2;
    }

    public BusDataConfigurationEntity getSignalConfigGreen1() {
        return signalConfigGreen1;
    }

    public void setSignalConfigGreen1(BusDataConfigurationEntity signalConfigGreen1) {
        this.signalConfigGreen1 = signalConfigGreen1;
    }

    public BusDataConfigurationEntity getSignalConfigGreen2() {
        return signalConfigGreen2;
    }

    public void setSignalConfigGreen2(BusDataConfigurationEntity signalConfigGreen2) {
        this.signalConfigGreen2 = signalConfigGreen2;
    }

    public BusDataConfigurationEntity getSignalConfigYellow1() {
        return signalConfigYellow1;
    }

    public void setSignalConfigYellow1(BusDataConfigurationEntity signalConfigYellow1) {
        this.signalConfigYellow1 = signalConfigYellow1;
    }

    public BusDataConfigurationEntity getSignalConfigYellow2() {
        return signalConfigYellow2;
    }

    public void setSignalConfigYellow2(BusDataConfigurationEntity signalConfigYellow2) {
        this.signalConfigYellow2 = signalConfigYellow2;
    }

    public BusDataConfigurationEntity getSignalConfigWhite() {
        return signalConfigWhite;
    }

    public void setSignalConfigWhite(BusDataConfigurationEntity signalConfigWhite) {
        this.signalConfigWhite = signalConfigWhite;
    }

    @Override
    public Class<? extends AbstractTrackPart> getDefaultDtoClass() {
        return Signal.class;
    }
}
