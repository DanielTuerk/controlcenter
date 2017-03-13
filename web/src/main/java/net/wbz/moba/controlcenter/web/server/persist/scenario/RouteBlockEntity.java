package net.wbz.moba.controlcenter.web.server.persist.scenario;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.googlecode.jmapper.annotations.JMap;

import net.wbz.moba.controlcenter.web.server.persist.AbstractEntity;
import net.wbz.moba.controlcenter.web.server.persist.construction.track.SignalEntity;

/**
 * @author Daniel Tuerk
 */
@Entity(name = "SCENARIO_ROUTE_BLOCK")
public class RouteBlockEntity extends AbstractEntity {

    /**
     * Position of the route block which must be unique in the {@link RouteEntity}. Represents the order of the blocks
     * to
     * drive.
     * TODO validations
     */
    @JMap
    @Column
    private int position;

    /**
     * Start of the block.
     */
    @JMap
    @ManyToOne
    private SignalEntity startPoint;

    /**
     * End of the block.
     */
    @JMap
    @ManyToOne
    private SignalEntity endPoint;

    /**
     * Parts between the start and end point to toggle to crate the route to drive.
     */
    @JMap
    @OneToMany(mappedBy = "routeBlock")
    private List<RouteBlockPartEntity> routeBlockParts;

    @ManyToOne
    private RouteEntity route;

    public RouteEntity getRoute() {
        return route;
    }

    public void setRoute(RouteEntity route) {
        this.route = route;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int number) {
        this.position = number;
    }

    public SignalEntity getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(SignalEntity startPoint) {
        this.startPoint = startPoint;
    }

    public SignalEntity getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(SignalEntity endPoint) {
        this.endPoint = endPoint;
    }

    public List<RouteBlockPartEntity> getRouteBlockParts() {
        return routeBlockParts;
    }

    public void setRouteBlockParts(
            List<RouteBlockPartEntity> routeBlockParts) {
        this.routeBlockParts = routeBlockParts;
    }
}
