package net.wbz.moba.controlcenter.shared.track.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Collection;

/**
 * @author Daniel Tuerk
 */
public abstract class AbstractTrackPart extends AbstractDto {

    private final String trackPartType = this.getClass().getSimpleName();
    private GridPosition gridPosition;

    public GridPosition getGridPosition() {
        return gridPosition;
    }

    public void setGridPosition(GridPosition gridPosition) {
        this.gridPosition = gridPosition;
    }

    /**
     * Get the next {@link GridPosition} which is connected to this track part.
     *
     * @param previousPosition previous {@link GridPosition}
     * @return next {@link GridPosition}
     */
    @JsonIgnore
    public abstract Collection<GridPosition> getNextGridPositions(GridPosition previousPosition);

    /**
     * Get the last {@link GridPosition} which is connected to this track part.
     *
     * @return last {@link GridPosition}
     */
    @JsonIgnore
    public abstract Collection<GridPosition> getLastGridPositions();

    /**
     * Rotation angle in degree of the track part.
     *
     * @return angle in degree
     */
    public double getRotationAngle() {
        return 0;
    }

    public String getTrackPartType() {
        return trackPartType;
    }
}
