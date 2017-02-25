package net.wbz.moba.controlcenter.web.shared.scenario;

import java.util.List;

import com.google.common.base.Optional;
import com.googlecode.jmapper.annotations.JMap;

import net.wbz.moba.controlcenter.web.shared.track.model.AbstractDto;

/**
 * @author Daniel Tuerk
 */
public class Route extends AbstractDto {

    @JMap
    private int number;

    @JMap
    private StationRail startStationRail;
    @JMap
    private StationRail endStationRail;

    @JMap
    private boolean oneway;

    @JMap
    private List<RouteBlock> routeBlocks;

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public StationRail getStartStationRail() {
        return startStationRail;
    }

    public void setStartStationRail(StationRail startStationRail) {
        this.startStationRail = startStationRail;
    }

    public StationRail getEndStationRail() {
        return endStationRail;
    }

    public void setEndStationRail(StationRail endStationRail) {
        this.endStationRail = endStationRail;
    }

    public boolean isOneway() {
        return oneway;
    }

    public void setOneway(boolean oneway) {
        this.oneway = oneway;
    }

    public List<RouteBlock> getRouteBlocks() {
        return routeBlocks;
    }

    public void setRouteBlocks(List<RouteBlock> routeBlocks) {
        this.routeBlocks = routeBlocks;
    }

    public Optional<RouteBlock> getFirstRouteBlock() {
        if (routeBlocks.isEmpty()) {
            return Optional.absent();
        }
        return Optional.of(routeBlocks.get(0));
    }
}
