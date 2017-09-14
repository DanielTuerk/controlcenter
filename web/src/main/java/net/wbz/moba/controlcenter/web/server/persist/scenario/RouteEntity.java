package net.wbz.moba.controlcenter.web.server.persist.scenario;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.googlecode.jmapper.annotations.JMap;

import net.wbz.moba.controlcenter.web.server.persist.AbstractEntity;
import net.wbz.moba.controlcenter.web.server.persist.construction.track.TrackBlockEntity;

/**
 * TODO station und stopover sind sehr ähnlich, hier wird auch was abstraktes für start und ziel gebraucht
 *
 * @author Daniel Tuerk
 */
@Entity(name = "SCENARIO_ROUTE")
public class RouteEntity extends AbstractEntity {

    /**
     * TODO validations
     */
    @JMap
    @Column
    private String name;

    @JMap
    @ManyToOne
    private TrackBlockEntity start;

    @JMap
    @ManyToOne
    private TrackBlockEntity end;

    /**
     * Indicate one-way track or multi-ways. On one-way only one train can drive from start to end. No other train can
     * go on a route with the same start and end and have to wait until the track is clear.
     * TODO how to handle different ways on different station rails in one station?
     */
    @JMap
    @Column(columnDefinition = "int default 1", nullable = false)
    private Boolean oneway;

    /**
     * Parts between the start and end point to toggle to create the route on track to drive.
     */
    @JMap
    @OneToMany(mappedBy = "route", fetch = FetchType.EAGER)
    private List<RouteBlockPartEntity> routeBlockParts;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getOneway() {
        return oneway;
    }

    public void setOneway(Boolean oneway) {
        this.oneway = oneway;
    }

    public TrackBlockEntity getStart() {
        return start;
    }

    public void setStart(TrackBlockEntity start) {
        this.start = start;
    }

    public TrackBlockEntity getEnd() {
        return end;
    }

    public void setEnd(TrackBlockEntity end) {
        this.end = end;
    }

    public List<RouteBlockPartEntity> getRouteBlockParts() {
        return routeBlockParts;
    }

    public void setRouteBlockParts(
            List<RouteBlockPartEntity> routeBlockParts) {
        this.routeBlockParts = routeBlockParts;
    }
}
