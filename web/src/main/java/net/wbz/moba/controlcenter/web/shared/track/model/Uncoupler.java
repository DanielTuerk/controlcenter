package net.wbz.moba.controlcenter.web.shared.track.model;

import com.googlecode.jmapper.annotations.JMap;

import java.util.Set;

/**
 * @author Daniel Tuerk
 */
public class Uncoupler extends Straight implements HasToggleFunction {
    @JMap
    private BusDataConfiguration toggleFunction;

    @JMap
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

    @Override
    public Set<BusDataConfiguration> getConfigurationsOfFunctions() {
        Set<BusDataConfiguration> functions = super.getConfigurationsOfFunctions();
        functions.add(toggleFunction);
        return functions;
    }

}
