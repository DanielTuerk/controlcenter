package net.wbz.moba.controlcenter.web.shared.scenario;

import java.util.List;

import com.google.common.base.Optional;
import com.googlecode.jmapper.annotations.JMap;

import net.wbz.moba.controlcenter.web.shared.track.model.AbstractDto;
import net.wbz.moba.controlcenter.web.shared.track.model.TrackBlock;

/**
 * @author Daniel Tuerk
 */
public class Route extends AbstractDto {

    @JMap
    private String name;

    @JMap
    private TrackBlock start;
    @JMap
    private TrackBlock end;

    @JMap
    private Boolean oneway;

    @JMap
    private List<RouteBlockPart> routeBlockParts;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TrackBlock getStart() {
        return start;
    }

    public void setStart(TrackBlock start) {
        this.start = start;
    }

    public TrackBlock getEnd() {
        return end;
    }

    public void setEnd(TrackBlock end) {
        this.end = end;
    }

    public Boolean getOneway() {
        return oneway;
    }

    public void setOneway(Boolean oneway) {
        this.oneway = oneway;
    }

    public List<RouteBlockPart> getRouteBlockParts() {
        return routeBlockParts;
    }

    public void setRouteBlockParts(List<RouteBlockPart> routeBlockParts) {
        this.routeBlockParts = routeBlockParts;
    }

    // public Optional<RouteBlockPart> getFirstRouteBlock() {
    // if (routeBlockParts.isEmpty()) {
    // return Optional.absent();
    // }
    // return Optional.of(routeBlockParts.get(0));
    // }
    //
    // public Optional<RouteBlockPart> getLastRouteBlock() {
    // if (routeBlockParts.isEmpty()) {
    // return Optional.absent();
    // }
    // return Optional.of(routeBlockParts.get(routeBlockParts.size() - 1));
    // }

}
