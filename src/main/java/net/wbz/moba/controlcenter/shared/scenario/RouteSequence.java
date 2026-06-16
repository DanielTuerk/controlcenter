package net.wbz.moba.controlcenter.shared.scenario;


import lombok.Getter;
import lombok.Setter;
import net.wbz.moba.controlcenter.shared.track.model.AbstractDto;

/**
 * @author Daniel Tuerk
 */
@Setter
@Getter
public class RouteSequence extends AbstractDto {

    private int position;
    private Route route;
    private int endDelayInSeconds;

    public RouteSequence() {
    }

    @Override
    public String toString() {
        return "RouteSequence{position=%d, route=%s, endDelayInSeconds=%d}"
            .formatted(position, route, endDelayInSeconds);
    }
}
