package net.wbz.moba.controlcenter.web.shared.scenario;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.gwt.user.client.rpc.IsSerializable;

import net.wbz.moba.controlcenter.web.shared.track.model.BusDataConfiguration;
import net.wbz.moba.controlcenter.web.shared.track.model.GridPosition;
import net.wbz.moba.controlcenter.web.shared.track.model.TrackBlock;

/**
 * Model for the track of a {@link Route}.
 * The track have a dedicated start and end position. It stores the track from start to end with needed toggle function
 * settings of the switches and the track blocks to drive trough.
 * 
 * @author Daniel Tuerk
 */
public class Track implements IsSerializable {

    /**
     * Blocks which need to be freed to drive.
     */
    private List<TrackBlock> trackBlocks;
    /**
     * Function with needed state.
     */
    private List<BusDataConfiguration> trackFunctions;
    /**
     * Way by positions to go.
     */
    private List<GridPosition> gridPositions;

    public Track() {
        this(new ArrayList<TrackBlock>(), new ArrayList<BusDataConfiguration>(), new ArrayList<GridPosition>());
    }

    public Track(Track track) {
        this(Lists.newArrayList(track.getTrackBlocks()), Lists.newArrayList(track.getTrackFunctions()),
                Lists.newArrayList(track.getGridPositions()));
    }

    public Track(List<TrackBlock> trackBlocks, List<BusDataConfiguration> trackFunctions,
            List<GridPosition> gridPositions) {
        this.trackBlocks = trackBlocks;
        this.trackFunctions = trackFunctions;
        this.gridPositions = gridPositions;
    }

    public List<TrackBlock> getTrackBlocks() {
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

}
