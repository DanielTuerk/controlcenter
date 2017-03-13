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
    private String name;

    @JMap
    private StationRail startStationRail;
    @JMap
    private StationRail endStationRail;

    @JMap
    private Boolean oneway;

    @JMap
    private List<RouteBlock> routeBlocks;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Boolean getOneway() {
        return oneway;
    }

    public void setOneway(Boolean oneway) {
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

    public String getStationRailDisplayName(StationRail stationRail) {
        return String.valueOf(stationRail != null ? stationRail.getRailNumber()
                : "");
    }
}
