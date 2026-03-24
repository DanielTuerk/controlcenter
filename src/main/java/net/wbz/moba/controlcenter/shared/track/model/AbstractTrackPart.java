package net.wbz.moba.controlcenter.shared.track.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.util.Collection;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * @author Daniel Tuerk
 */
@Schema(
    discriminatorProperty = "trackPartType",
    oneOf = {
        BlockStraight.class,
        Straight.class,
        Curve.class,
        Signal.class,
        Turnout.class,
        Uncoupler.class
    }
)
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "trackPartType"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = BlockStraight.class, name = "BlockStraight"),
    @JsonSubTypes.Type(value = Straight.class, name = "Straight"),
    @JsonSubTypes.Type(value = Curve.class, name = "Curve"),
    @JsonSubTypes.Type(value = Signal.class, name = "Signal"),
    @JsonSubTypes.Type(value = Turnout.class, name = "Turnout"),
    @JsonSubTypes.Type(value = Uncoupler.class, name = "Uncoupler")
})
public abstract class AbstractTrackPart extends AbstractDto {

    @Schema(required = true)
    public String getTrackPartType() {
        return this.getClass().getSimpleName();
    }

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

}
