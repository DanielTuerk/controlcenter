package net.wbz.moba.controlcenter.shared.track.model;


import lombok.Getter;
import lombok.Setter;

/**
 * @author Daniel Tuerk
 */
@Setter
@Getter
public class EventConfiguration extends AbstractDto {

    private BusDataConfiguration stateOnConfig;
    private BusDataConfiguration stateOffConfig;

    @Override
    public String toString() {
        return "EventConfiguration{" +
                "stateOnConfig=" + stateOnConfig +
                ", stateOffConfig=" + stateOffConfig +
                '}';
    }

}
