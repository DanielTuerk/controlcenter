package net.wbz.moba.controlcenter.web.server.persist.construction.track;

import com.googlecode.jmapper.annotations.JMap;
import net.wbz.moba.controlcenter.web.server.persist.AbstractEntity;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * @author Daniel Tuerk
 */
@Entity(name = "event_config")
public class EventConfigurationEntity extends AbstractEntity {

    @JMap
    @ManyToOne
    private BusDataConfigurationEntity stateOnConfig;

    @JMap
    @ManyToOne
    private BusDataConfigurationEntity stateOffConfig;

    public void setStateOnConfig(BusDataConfigurationEntity config) {
        stateOnConfig = config;
    }

    public void setStateOffConfig(BusDataConfigurationEntity config) {
        stateOffConfig = config;
    }

    public BusDataConfigurationEntity getStateOnConfig() {
        return stateOnConfig;
    }

    public BusDataConfigurationEntity getStateOffConfig() {
        return stateOffConfig;
    }

    public boolean isActive() {
        return stateOnConfig != null && stateOffConfig != null;
    }

}
