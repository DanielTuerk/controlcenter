package net.wbz.moba.controlcenter.web.shared.train;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.googlecode.jmapper.annotations.JMap;
import net.wbz.moba.controlcenter.web.server.persist.construction.track.BusDataConfigurationEntity;
import net.wbz.moba.controlcenter.web.shared.track.model.AbstractDto;
import net.wbz.moba.controlcenter.web.shared.track.model.BusDataConfiguration;

import java.io.Serializable;

/**
 * @author Daniel Tuerk
 */
public class TrainFunction extends AbstractDto {
    /**
     * Available functions for the train.
     */
    public enum FUNCTION implements Serializable, IsSerializable {LIGHT, HORN, FUNCTION}


    @JMap
    private String alias;
    @JMap
    private BusDataConfiguration configuration;

    private boolean active;

    public BusDataConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(BusDataConfiguration configuration) {
        this.configuration = configuration;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
}
