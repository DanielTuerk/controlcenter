package net.wbz.moba.controlcenter.shared.scenario;


import lombok.Getter;
import lombok.Setter;
import net.wbz.moba.controlcenter.shared.track.model.AbstractDto;
import net.wbz.moba.controlcenter.shared.track.model.BlockStraight;
import net.wbz.moba.controlcenter.shared.track.model.GridPosition;
import net.wbz.moba.controlcenter.shared.track.model.TrackBlock;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Daniel Tuerk
 */
@Setter
@Getter
public class Route extends AbstractDto {

    private String name;
    private BlockStraight start;
    private TrackBlock end;
    private Boolean oneway;
    private List<GridPosition> waypoints;
    /**
     * {@link Track} for this route.
     */
    private Track track;

    public boolean hasTrack() {
        return track != null;
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
            trackBlocks.addAll(track.trackBlocks());
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
        PREPARED,
        /**
         * Reserved for start after successful preparation.
         */
        RESERVED,
        /**
         * Currently running.
         */
        RUNNING,
        /**
         * Execution finished.
         */
        FINISHED,
        /**
         * Execution failed
         */
        FAILED,
        /**
         * Execution skipped
         */
        SKIPPED,
        /**
         * Execution canceled
         */
        CANCELED
    }
}
