package net.wbz.moba.controlcenter.web.shared.track.model;

import net.wbz.moba.controlcenter.web.shared.Identity;


import javax.persistence.*;

/**
 * @author Daniel Tuerk
 */
@Entity
public class EventConfiguration implements Identity {

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

    @Override
    public Integer getVersion() {
        return 0;
    }

    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
