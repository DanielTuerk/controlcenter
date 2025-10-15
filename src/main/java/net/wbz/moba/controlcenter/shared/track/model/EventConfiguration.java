package net.wbz.moba.controlcenter.shared.track.model;



/**
 * @author Daniel Tuerk
 */
public class EventConfiguration extends AbstractDto {

    private BusDataConfiguration stateOnConfig;
    private BusDataConfiguration stateOffConfig;

    public BusDataConfiguration getStateOnConfig() {
        return stateOnConfig;
    }

    public void setStateOnConfig(BusDataConfiguration stateOnConfig) {
        this.stateOnConfig = stateOnConfig;
    }

    public BusDataConfiguration getStateOffConfig() {
        return stateOffConfig;
    }

    public void setStateOffConfig(BusDataConfiguration stateOffConfig) {
        this.stateOffConfig = stateOffConfig;
    }

    @Override
    public String toString() {
        return "EventConfiguration{" +
                "stateOnConfig=" + stateOnConfig +
                ", stateOffConfig=" + stateOffConfig +
                '}';
    }

    public boolean isActive() {
        return stateOffConfig != null && stateOnConfig != null;
    }
}
