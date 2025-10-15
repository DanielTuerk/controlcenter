package net.wbz.moba.controlcenter.shared.track.model;



/**
 * @author Daniel Tuerk
 */
public class Uncoupler extends Straight implements HasToggleFunction {
    private BusDataConfiguration toggleFunction;

    private EventConfiguration eventConfiguration;

    @Override
    public BusDataConfiguration getToggleFunction() {
        return toggleFunction;
    }

    @Override
    public void setToggleFunction(BusDataConfiguration toggleFunction) {
        this.toggleFunction = toggleFunction;
    }

    @Override
    public EventConfiguration getEventConfiguration() {
        return eventConfiguration;
    }

    @Override
    public void setEventConfiguration(EventConfiguration eventConfiguration) {
        this.eventConfiguration = eventConfiguration;
    }

}
