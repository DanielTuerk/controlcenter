package net.wbz.moba.controlcenter.web.shared.track.model;

import com.google.gwt.user.client.rpc.IsSerializable;
import net.sf.gilead.pojo.gwt.LightEntity;

import javax.annotation.Generated;
import javax.persistence.*;
import java.io.Serializable;

/**
 * @author Daniel Tuerk
 */
@Entity
public class EventConfiguration extends LightEntity implements IsSerializable, Serializable {

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
