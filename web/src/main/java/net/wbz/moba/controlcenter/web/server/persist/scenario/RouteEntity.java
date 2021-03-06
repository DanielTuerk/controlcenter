package net.wbz.moba.controlcenter.web.server.persist.scenario;

import com.googlecode.jmapper.annotations.JMap;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import net.wbz.moba.controlcenter.web.server.persist.AbstractEntity;
import net.wbz.moba.controlcenter.web.server.persist.construction.track.BlockStraightEntity;
import net.wbz.moba.controlcenter.web.server.persist.construction.track.GridPositionEntity;
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
    private BlockStraightEntity start;

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
     * Optional waypoints between the start and end point.
     */
    @JMap
    @ManyToMany(fetch = FetchType.EAGER)
    private List<GridPositionEntity> waypoints;

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

    public BlockStraightEntity getStart() {
        return start;
    }

    public void setStart(BlockStraightEntity start) {
        this.start = start;
    }

    public TrackBlockEntity getEnd() {
        return end;
    }

    public void setEnd(TrackBlockEntity end) {
        this.end = end;
    }

    public List<GridPositionEntity> getWaypoints() {
        return waypoints;
    }

    public void setWaypoints(List<GridPositionEntity> waypoints) {
        this.waypoints = waypoints;
    }

}
