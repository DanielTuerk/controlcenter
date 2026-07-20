package io.github.danieltuerk.controlcenter.shared.track.model;


import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

/**
 * @author Daniel Tuerk
 */
@Schema(
    description = "type for a track part",
    allOf = {Straight.class}
)
@Tag(ref = "track")
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

    @Override
    public String toString() {
        return "Uncoupler{toggleFunction=%s, eventConfiguration=%s}".formatted(toggleFunction, eventConfiguration);
    }
}
