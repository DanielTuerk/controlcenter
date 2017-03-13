package net.wbz.moba.controlcenter.web.server.persist.scenario;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import com.googlecode.jmapper.annotations.JMap;

import net.wbz.moba.controlcenter.web.server.persist.AbstractEntity;
import net.wbz.moba.controlcenter.web.server.persist.construction.track.SwitchEntity;

/**
 * @author Daniel Tuerk
 */
@Entity(name = "SCENARIO_ROUTE_BLOCK_PART")
public class RouteBlockPartEntity extends AbstractEntity {

    /**
     * Switch for the route.
     * TODO refactor to abstract parts which have toggle functions
     */
    @JMap
    @ManyToOne
    private SwitchEntity switchTrackPart;

    /**
     * State of the switch to set for the route.
     */
    @JMap
    @Column(columnDefinition = "int default 1", nullable = false)
    private boolean state;

    @ManyToOne
    private RouteBlockEntity routeBlock;

    public RouteBlockEntity getRouteBlock() {
        return routeBlock;
    }

    public void setRouteBlock(RouteBlockEntity routeBlock) {
        this.routeBlock = routeBlock;
    }

    public SwitchEntity getSwitchTrackPart() {
        return switchTrackPart;
    }

    public void setSwitchTrackPart(
            SwitchEntity switchTrackPart) {
        this.switchTrackPart = switchTrackPart;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }
}
