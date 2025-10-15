package net.wbz.moba.controlcenter.shared.train;


import java.io.Serializable;
import net.wbz.moba.controlcenter.shared.track.model.AbstractDto;
import net.wbz.moba.controlcenter.shared.track.model.BusDataConfiguration;

/**
 * @author Daniel Tuerk
 */
public class TrainFunction extends AbstractDto {

    private String alias;
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

    /**
     * Available functions for the train.
     */
    public enum FUNCTION implements Serializable {
        LIGHT, HORN, FUNCTION
    }
}
