package net.wbz.moba.controlcenter.web.shared.scenario;

import java.util.List;

import com.googlecode.jmapper.annotations.JMap;

import net.wbz.moba.controlcenter.web.shared.track.model.AbstractDto;
import net.wbz.moba.controlcenter.web.shared.track.model.Signal;

/**
 * @author Daniel Tuerk
 */
public class RouteBlock extends AbstractDto {

    /**
     * Number of the route block which must be unique in the {@link Route}. Represents the order of the blocks to
     * drive.
     * TODO validations
     */
    @JMap
    private int number;

    /**
     * Start of the block.
     */
    @JMap
    private Signal startPoint;
    /**
     * End of the block.
     */
    @JMap
    private Signal endPoint;

    /**
     * Parts between the start and end point to toggle to crate the route to drive.
     */
    @JMap
    private List<RouteBlockPart> routeBlockParts;

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public Signal getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(Signal startPoint) {
        this.startPoint = startPoint;
    }

    public Signal getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(Signal endPoint) {
        this.endPoint = endPoint;
    }

    public List<RouteBlockPart> getRouteBlockParts() {
        return routeBlockParts;
    }

    public void setRouteBlockParts(
            List<RouteBlockPart> routeBlockParts) {
        this.routeBlockParts = routeBlockParts;
    }
}
