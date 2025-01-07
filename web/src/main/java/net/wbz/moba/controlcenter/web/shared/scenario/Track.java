package net.wbz.moba.controlcenter.web.shared.scenario;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.wbz.moba.controlcenter.web.shared.track.model.BusDataConfiguration;
import net.wbz.moba.controlcenter.web.shared.track.model.GridPosition;
import net.wbz.moba.controlcenter.web.shared.track.model.TrackBlock;

/**
 * Model for the track of a {@link Route}. The track have a dedicated start and end position. It stores the track from
 * start to end with needed toggle function settings of the switches and the track blocks to drive trough.
 *
 * @author Daniel Tuerk
 */
public class Track {

    /**
     * Blocks which need to be freed to drive.
     */
    private Set<TrackBlock> trackBlocks;
    /**
     * Function with needed state.
     */
    private List<BusDataConfiguration> trackFunctions;
    /**
     * Way by positions to go.
     */
    private List<GridPosition> gridPositions;

    public Track() {
        this(new HashSet<TrackBlock>(), new ArrayList<BusDataConfiguration>(), new ArrayList<GridPosition>());
    }

    public Track(Track track) {
        this(Sets.newHashSet(track.getTrackBlocks()), Lists.newArrayList(track.getTrackFunctions()),
            Lists.newArrayList(track.getGridPositions()));
    }

    public Track(Set<TrackBlock> trackBlocks, List<BusDataConfiguration> trackFunctions,
        List<GridPosition> gridPositions) {
        this.trackBlocks = trackBlocks;
        this.trackFunctions = trackFunctions;
        this.gridPositions = gridPositions;
    }

    public Set<TrackBlock> getTrackBlocks() {
        return trackBlocks;
    }

    public List<BusDataConfiguration> getTrackFunctions() {
        return trackFunctions;
    }

    public List<GridPosition> getGridPositions() {
        return gridPositions;
    }

    public int getLength() {
        return gridPositions.size();
    }

    public Optional<Boolean> getTrackFunctionState(BusDataConfiguration busDataConfiguration) {
        for (BusDataConfiguration trackFunction : trackFunctions) {
            if (trackFunction.isSameConfig(busDataConfiguration)) {
                return Optional.of(trackFunction.getBitState());
            }
        }
        return Optional.empty();
    }
}
