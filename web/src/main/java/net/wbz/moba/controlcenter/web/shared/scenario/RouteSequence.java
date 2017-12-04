package net.wbz.moba.controlcenter.web.shared.scenario;

import com.google.common.base.Objects;
import com.googlecode.jmapper.annotations.JMap;
import net.wbz.moba.controlcenter.web.shared.track.model.AbstractDto;

/**
 * @author Daniel Tuerk
 */
public class RouteSequence extends AbstractDto {

    @JMap
    private int position;
    @JMap
    private Route route;
    @JMap
    private int endDelayInSeconds;

    public RouteSequence() {
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public int getEndDelayInSeconds() {
        return endDelayInSeconds;
    }

    public void setEndDelayInSeconds(int endDelayInSeconds) {
        this.endDelayInSeconds = endDelayInSeconds;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("position", position)
                .add("route", route)
                .toString();
    }
}
