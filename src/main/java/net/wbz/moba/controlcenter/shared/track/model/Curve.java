package net.wbz.moba.controlcenter.shared.track.model;


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
public class Curve extends AbstractTrackPart {
    private DIRECTION direction;

    @Override
    public Collection<GridPosition> getNextGridPositions(GridPosition previousPosition) {
        return List.of(getNextGridPosition());
    }

    @Override
    public Collection<GridPosition> getLastGridPositions() {
        return List.of(getLastGridPosition());
    }

    private GridPosition getNextGridPosition() {
        int x = getGridPosition().getX();
        int y = getGridPosition().getY();

        switch (getDirection()) {
            case BOTTOM_LEFT:
                x--;
                break;
            case BOTTOM_RIGHT:
                x++;
                break;
            case TOP_LEFT:
                x--;
                break;
            case TOP_RIGHT:
                x++;
                break;
        }
        return new GridPosition(x, y);
    }

    private GridPosition getLastGridPosition() {
        int x = getGridPosition().getX();
        int y = getGridPosition().getY();
        switch (getDirection()) {
            case BOTTOM_LEFT:
                y++;
                break;
            case BOTTOM_RIGHT:
                y++;
                break;
            case TOP_LEFT:
                y--;
                break;
            case TOP_RIGHT:
                y--;
                break;
        }
        return new GridPosition(x, y);
    }

    @Override
    public double getRotationAngle() {
        return switch (getDirection()) {
            case BOTTOM_LEFT -> 90d;
            case TOP_LEFT -> 180d;
            case TOP_RIGHT -> 270d;
            default -> 0d;
        };
    }

    @Override
    public String toString() {
        return "Curve{" + "direction=" + direction+ "} "  +super.toString();
    }

    public enum DIRECTION {
        BOTTOM_RIGHT, BOTTOM_LEFT, TOP_LEFT, TOP_RIGHT
    }
}
