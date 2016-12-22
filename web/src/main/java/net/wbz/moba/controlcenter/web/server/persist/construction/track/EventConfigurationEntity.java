package net.wbz.moba.controlcenter.web.server.persist.construction.track;

import net.wbz.moba.controlcenter.web.server.persist.AbstractEntity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

/**
 * @author Daniel Tuerk
 */
@Entity
public class EventConfigurationEntity extends AbstractEntity {


    @OneToOne
    private TrackPartConfigurationEntity stateOnConfig;
    @OneToOne
    private TrackPartConfigurationEntity stateOffConfig;

    public void setStateOnConfig(TrackPartConfigurationEntity config) {
        addStateConfig(true, config);
    }

    public void setStateOffConfig(TrackPartConfigurationEntity config) {
        addStateConfig(false, config);
    }

    private void addStateConfig(boolean state, TrackPartConfigurationEntity config) {
        if (state) {
            stateOnConfig = config;
        } else {
            stateOffConfig = config;
        }
    }

    public TrackPartConfigurationEntity getStateOnConfig() {
        return stateOnConfig;
    }

    public TrackPartConfigurationEntity getStateOffConfig() {
        return stateOffConfig;
    }

    public boolean isActive() {
        return stateOnConfig != null && stateOffConfig != null;
    }

}
