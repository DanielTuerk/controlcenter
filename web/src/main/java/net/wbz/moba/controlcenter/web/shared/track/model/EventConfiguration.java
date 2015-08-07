package net.wbz.moba.controlcenter.web.shared.track.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.io.Serializable;

/**
 * @author Daniel Tuerk
 */
@Entity
public class EventConfiguration implements Serializable {

    @Id
    @GeneratedValue
    private long id;

//    @OneToOne(fetch = FetchType.LAZY)
//    private TrackPart trackPart;

    @OneToOne
    private Configuration stateOnConfig;
    @OneToOne
    private Configuration stateOffConfig;

    public void setStateOnConfig(Configuration config) {
        addStateConfig(true, config);
    }

    public void setStateOffConfig(Configuration config) {
        addStateConfig(false, config);
    }

    private void addStateConfig(boolean state, Configuration config) {
        if (state) {
            stateOnConfig = config;
        } else {
            stateOffConfig = config;
        }
    }

    public Configuration getStateOnConfig() {
        return stateOnConfig;
    }

    public Configuration getStateOffConfig() {
        return stateOffConfig;
    }

    public boolean isActive() {
        return stateOnConfig != null && stateOffConfig != null;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
