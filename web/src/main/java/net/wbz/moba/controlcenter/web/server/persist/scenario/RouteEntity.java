package net.wbz.moba.controlcenter.web.server.persist.scenario;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.googlecode.jmapper.annotations.JMap;

import net.wbz.moba.controlcenter.web.server.persist.AbstractEntity;

/**
 * TODO station und stopover sind sehr ähnlich, hier wird auch was abstraktes für start und ziel gebraucht
 *
 * @author Daniel Tuerk
 */
@Entity(name = "SCENARIO_ROUTE")
public class RouteEntity extends AbstractEntity {

    /**
     * Number of the route which must be unique in the {@link ScenarioEntity}. Represents the order of the routes to
     * drive.
     * TODO validations
     */
    @JMap
    @Column
    private int number;

    @JMap
    @ManyToOne
    private StationRailEntity startStationRail;

    @JMap
    @ManyToOne
    private StationRailEntity endStationRail;

    /**
     * Indicate one-way track or multi-ways. On oneline only one train can drive from start to end. No other train can
     * go
     * on a route with the same start and end and have to wait until the track is clear.
     * TODO how to handle different ways on different station rails in one station?
     */
    @JMap
    @Column(columnDefinition = "int default 1", nullable = false)
    private Boolean oneway;

    /**
     * Blocks of the route to set to drive from start to end. Each block don't depend on another. Apply only changes for
     * the route for the blocks one after another. The block could have a block signal as end and start point. Which
     * make it possible to run different route blocks on the same time.
     */
    @JMap
    @OneToMany
    private List<RouteBlockEntity> routeBlocks;

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public StationRailEntity getStartStationRail() {
        return startStationRail;
    }

    public void setStartStationRail(
            StationRailEntity startStationRail) {
        this.startStationRail = startStationRail;
    }

    public StationRailEntity getEndStationRail() {
        return endStationRail;
    }

    public void setEndStationRail(StationRailEntity endStationRail) {
        this.endStationRail = endStationRail;
    }

    public boolean isOneway() {
        return oneway;
    }

    public void setOneway(boolean oneway) {
        this.oneway = oneway;
    }

    public List<RouteBlockEntity> getRouteBlocks() {
        return routeBlocks;
    }

    public void setRouteBlocks(
            List<RouteBlockEntity> routeBlocks) {
        this.routeBlocks = routeBlocks;
    }
}
