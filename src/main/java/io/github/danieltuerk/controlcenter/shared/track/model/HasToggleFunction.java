package io.github.danieltuerk.controlcenter.shared.track.model;

/**
 * @author Daniel Tuerk
 */
public interface HasToggleFunction {
    BusDataConfiguration getToggleFunction();

    void setToggleFunction(BusDataConfiguration toggleFunction);

    EventConfiguration getEventConfiguration();

    void setEventConfiguration(EventConfiguration eventConfiguration);
}
