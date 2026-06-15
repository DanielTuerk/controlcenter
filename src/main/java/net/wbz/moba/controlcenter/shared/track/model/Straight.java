package net.wbz.moba.controlcenter.shared.track.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.Collection;
import java.util.List;

/**
 * @author Daniel Tuerk
 */
@Setter
@Getter
@Schema(description = "type for a track part")
@Tag(ref = "track")
public class Straight extends AbstractTrackPart {

    private Straight.DIRECTION direction;

    @JsonIgnore
    @Override
    public Collection<GridPosition> getNextGridPositions(GridPosition previousPosition) {
        int x = getGridPosition().getX();
        int y = getGridPosition().getY();
        if (isVertical()) {
            y++;
        } else {
            x++;
        }
        return List.of(new GridPosition(x, y));
    }

    @JsonIgnore
    @Override
    public Collection<GridPosition> getLastGridPositions() {
        int x = getGridPosition().getX();
        int y = getGridPosition().getY();
        if (isVertical()) {
            y--;
        } else {
            x--;
        }
        return List.of(new GridPosition(x, y));
    }

    @JsonIgnore
    @Override
    public double getRotationAngle() {
        if (isVertical()) {
            return 90d;
        }
        return 0d;
    }

    @JsonIgnore
    boolean isVertical() {
        return getDirection() == DIRECTION.VERTICAL;
    }

    @Override
    public String toString() {
        return "Straight{" + "direction=" + direction + "} " + super.toString();
    }

    public enum DIRECTION {
        HORIZONTAL, VERTICAL
    }
}
