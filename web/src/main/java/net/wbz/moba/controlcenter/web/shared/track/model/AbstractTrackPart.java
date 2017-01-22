package net.wbz.moba.controlcenter.web.shared.track.model;

import java.util.Set;

import com.google.common.collect.Sets;
import com.googlecode.jmapper.annotations.JMap;

/**
 * @author Daniel Tuerk
 */
public abstract class AbstractTrackPart extends AbstractDto {

    @JMap
    private GridPosition gridPosition;

    @JMap
    private TrackBlock trackBlock;

    public GridPosition getGridPosition() {
        return gridPosition;
    }

    public void setGridPosition(GridPosition gridPosition) {
        this.gridPosition = gridPosition;
    }

    /**
     * TODO maybe refactor
     * 
     * @return
     */
    public Set<BusDataConfiguration> getConfigurationsOfFunctions() {
        // TODO
        if (trackBlock != null && trackBlock.getBlockFunction() != null) {
            return Sets.newHashSet(trackBlock.getBlockFunction());
        } else {
            return Sets.newHashSet();
        }
    }

    /**
     * TODO drop?
     * 
     * @return
     */
    public BusDataConfiguration getBlockFunction() {
        return trackBlock != null ? trackBlock.getBlockFunction() : null;
    }

    public TrackBlock getTrackBlock() {
        return trackBlock;
    }

    public void setTrackBlock(TrackBlock trackBlock) {
        this.trackBlock = trackBlock;
    }

    /**
     * Rotation angle in degree of the track part.
     *
     * @return angle in degree
     */
    public double getRotationAngle() {
        return 0;
    }
}
