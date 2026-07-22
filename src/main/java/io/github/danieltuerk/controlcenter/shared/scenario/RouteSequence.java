package io.github.danieltuerk.controlcenter.shared.scenario;


import io.github.danieltuerk.controlcenter.shared.track.model.AbstractDto;
import lombok.Getter;
import lombok.Setter;

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
