package net.wbz.moba.controlcenter.web.shared.scenario;

import java.util.List;

import com.googlecode.jmapper.annotations.JMap;

import net.wbz.moba.controlcenter.web.shared.track.model.AbstractDto;
import net.wbz.moba.controlcenter.web.shared.track.model.Signal;
import net.wbz.moba.controlcenter.web.shared.track.model.TrackBlock;

/**
 * @author Daniel Tuerk
 */
public class RouteBlock extends AbstractDto {

    /**
     * Position of the route block which must be unique in the {@link Route}. Represents the order of the blocks to
     * drive.
     * TODO validations
     */
    @JMap
    private int position;

    /**
     * Start of the block.
     */
    @JMap
    private Signal startPoint;
    /**
     * End of the block.
     */
    @JMap
    private TrackBlock endPoint;

    /**
     * Parts between the start and end point to toggle to crate the route to drive.
     */
    @JMap
    private List<RouteBlockPart> routeBlockParts;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public Signal getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(Signal startPoint) {
        this.startPoint = startPoint;
    }

    public TrackBlock getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(TrackBlock endPoint) {
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
