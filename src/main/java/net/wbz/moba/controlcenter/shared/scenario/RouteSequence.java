package net.wbz.moba.controlcenter.shared.scenario;


import net.wbz.moba.controlcenter.shared.track.model.AbstractDto;

/**
 * @author Daniel Tuerk
 */
public class RouteSequence extends AbstractDto {

    private int position;
    private Route route;
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
        final StringBuffer sb = new StringBuffer("RouteSequence{");
        sb.append("position=").append(position);
        sb.append(", route=").append(route);
        sb.append(", endDelayInSeconds=").append(endDelayInSeconds);
        sb.append('}');
        return sb.toString();
    }
}
