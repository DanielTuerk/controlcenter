package net.wbz.moba.controlcenter.shared.scenario;

import net.wbz.moba.controlcenter.shared.track.model.BusDataConfiguration;
import net.wbz.moba.controlcenter.shared.track.model.GridPosition;
import net.wbz.moba.controlcenter.shared.track.model.TrackBlock;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Model for the track of a {@link Route}. The track has a dedicated start and end position. It stores the track from
 * start to end with necessary toggle function settings of the switches and the track blocks to drive through.
 *
 * @param trackBlocks    Blocks that need to be freed to drive.
 * @param trackFunctions Function with necessary state.
 * @param gridPositions  Way by positions to go.
 * @author Daniel Tuerk
 */
public record Track(Set<TrackBlock> trackBlocks, List<BusDataConfiguration> trackFunctions,
                    List<GridPosition> gridPositions) {

    public Track() {
        this(new HashSet<>(), new ArrayList<>(), new ArrayList<>());
    }

    public Track(Track track) {
        this(new HashSet<>(track.trackBlocks()), new ArrayList<>(track.trackFunctions()),
                new ArrayList<>(track.gridPositions()));
    }

    public int getLength() {
        return gridPositions.size();
    }

}
