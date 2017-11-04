package net.wbz.moba.controlcenter.web.shared.scenario;

import java.util.List;

import com.googlecode.jmapper.annotations.JMap;

import net.wbz.moba.controlcenter.web.shared.track.model.AbstractDto;
import net.wbz.moba.controlcenter.web.shared.track.model.GridPosition;
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
    private List<GridPosition> waypoints;

    /**
     * {@link Track} for this route.
     */
    private Track track;

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

    public List<GridPosition> getWaypoints() {
        return waypoints;
    }

    public void setWaypoints(List<GridPosition> waypoints) {
        this.waypoints = waypoints;
    }

    public void addWaypoint(GridPosition gridPosition) {
        waypoints.add(gridPosition);
    }

    public void setTrack(Track track) {
        this.track = track;
    }

    public Track getTrack() {
        return track;
    }
}
