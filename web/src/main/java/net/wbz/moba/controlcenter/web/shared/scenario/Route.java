package net.wbz.moba.controlcenter.web.shared.scenario;

import com.googlecode.jmapper.annotations.JMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.wbz.moba.controlcenter.web.shared.track.model.AbstractDto;
import net.wbz.moba.controlcenter.web.shared.track.model.BlockStraight;
import net.wbz.moba.controlcenter.web.shared.track.model.GridPosition;
import net.wbz.moba.controlcenter.web.shared.track.model.TrackBlock;

/**
 * @author Daniel Tuerk
 */
public class Route extends AbstractDto {

    @JMap
    private String name;
    @JMap
    private BlockStraight start;
    @JMap
    private TrackBlock end;
    @JMap
    private Boolean oneway;
    @JMap
    private List<GridPosition> waypoints;
    /**
     * Current state for the execution.
     */
    private ROUTE_RUN_STATE runState;
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

    public BlockStraight getStart() {
        return start;
    }

    public void setStart(BlockStraight start) {
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

    public Track getTrack() {
        return track;
    }

    public void setTrack(Track track) {
        this.track = track;
    }

    public boolean hasTrack() {
        return track != null;
    }

    public ROUTE_RUN_STATE getRunState() {
        return runState;
    }

    public void setRunState(ROUTE_RUN_STATE runState) {
        this.runState = runState;
    }

    /**
     * Return all {@link TrackBlock}s on the {@link Track} with end block. It represents all blocks to drive from start
     * till end.
     *
     * @return {@link TrackBlock}s
     */
    public Set<TrackBlock> getAllTrackBlocksToDrive() {
        Set<TrackBlock> trackBlocks = new HashSet<>();
        Track track = getTrack();
        if (track != null) {
            trackBlocks.addAll(track.getTrackBlocks());
        }
        trackBlocks.add(getEnd());
        return trackBlocks;
    }

    @Override
    public String toString() {
        return "Route{" + "name='" + name + '\''
            + ", start=" + start
            + ", end=" + end
            + ", oneway=" + oneway
            + ", waypoints=" + waypoints
            + ", runState=" + runState
            + ", track=" + track
            + '}';
    }

    /**
     * Run state of a route.
     */
    public enum ROUTE_RUN_STATE {
        /**
         * Prepared to start.
         */
        PREPARED, /**
         * Reserved for start after successful preparation.
         */
        RESERVED, /**
         * Currently running.
         */
        RUNNING, /**
         * Execution finished.
         */
        FINISHED
    }
}
