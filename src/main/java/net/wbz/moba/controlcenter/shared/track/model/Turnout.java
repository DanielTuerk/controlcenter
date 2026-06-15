package net.wbz.moba.controlcenter.shared.track.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Daniel Tuerk
 */
@Schema(description = "type for a track part")
@Tag(ref = "track")
public class Turnout extends AbstractTrackPart implements HasToggleFunction {

    @Setter
    @Getter
    private Turnout.DIRECTION currentDirection;
    @Setter
    @Getter
    private Turnout.PRESENTATION currentPresentation;
    private BusDataConfiguration toggleFunction;
    private EventConfiguration eventConfiguration;

    @Override
    public BusDataConfiguration getToggleFunction() {
        return toggleFunction;
    }

    @Override
    public void setToggleFunction(BusDataConfiguration toggleFunction) {
        this.toggleFunction = toggleFunction;
    }

    @Override
    public EventConfiguration getEventConfiguration() {
        return eventConfiguration;
    }

    @Override
    public void setEventConfiguration(EventConfiguration eventConfiguration) {
        this.eventConfiguration = eventConfiguration;
    }

    @JsonIgnore
    @Override
    public Collection<GridPosition> getNextGridPositions(GridPosition previousPosition) {
        Collection<GridPosition> gridPositions = new ArrayList<>();

        GridPosition nextGridPositionStraight = getNextGridPositionForStateStraight();
        GridPosition nextGridPositionBranch = getNextGridPositionForStateBranch();

        if (previousPosition.equals(getLastGridPosition())) {
            // straight
            gridPositions.add(nextGridPositionStraight);
            // branch
            gridPositions.add(nextGridPositionBranch);
        } else if (previousPosition.equals(nextGridPositionStraight)) {
            gridPositions.add(nextGridPositionStraight);
        } else if (previousPosition.equals(nextGridPositionBranch)) {
            gridPositions.add(nextGridPositionBranch);
        }
        return gridPositions;
    }

    @JsonIgnore
    public GridPosition getNextGridPositionForStateBranch() {
        return switch (currentDirection) {
            case RIGHT -> getNextGridPositionForStateBranchToRight();
            case LEFT -> getNextGridPositionForStateBranchToLeft();
        };
    }

    private GridPosition getNextGridPositionForStateBranchToLeft() {
        int x = getGridPosition().getX();
        int y = getGridPosition().getY();
        switch (getCurrentPresentation()) {
            case LEFT_TO_RIGHT:
                y--;
                break;
            case RIGHT_TO_LEFT:
                y++;
                break;
            case BOTTOM_TO_TOP:
                x--;
                break;
            case TOP_TO_BOTTOM:
                x++;
                break;
        }
        return new GridPosition(x, y);
    }

    private GridPosition getNextGridPositionForStateBranchToRight() {
        int x = getGridPosition().getX();
        int y = getGridPosition().getY();
        switch (getCurrentPresentation()) {
            case LEFT_TO_RIGHT:
                y++;
                break;
            case RIGHT_TO_LEFT:
                y--;
                break;
            case BOTTOM_TO_TOP:
                x++;
                break;
            case TOP_TO_BOTTOM:
                x--;
                break;
        }
        return new GridPosition(x, y);
    }

    @JsonIgnore
    public GridPosition getNextGridPositionForStateStraight() {
        int x = getGridPosition().getX();
        int y = getGridPosition().getY();
        switch (getCurrentPresentation()) {
            case LEFT_TO_RIGHT:
                x++;
                break;
            case RIGHT_TO_LEFT:
                x--;
                break;
            case BOTTOM_TO_TOP:
                y--;
                break;
            case TOP_TO_BOTTOM:
                y++;
                break;
        }
        return new GridPosition(x, y);
    }

    @JsonIgnore
    @Override
    public Collection<GridPosition> getLastGridPositions() {
        return List.of(getLastGridPosition());
    }

    @JsonIgnore
    public GridPosition getLastGridPosition() {
        int x = getGridPosition().getX();
        int y = getGridPosition().getY();
        switch (getCurrentPresentation()) {
            case LEFT_TO_RIGHT:
                x--;
                break;
            case RIGHT_TO_LEFT:
                x++;
                break;
            case BOTTOM_TO_TOP:
                y++;
                break;
            case TOP_TO_BOTTOM:
                y--;
                break;
        }
        return new GridPosition(x, y);
    }

    /**
     * Rotation angle in degree of the current
     * {@link Turnout.PRESENTATION} of the switch.
     *
     * @return angle in degree
     */
    @JsonIgnore
    @Override
    public double getRotationAngle() {
        return switch (getCurrentPresentation()) {
            case RIGHT_TO_LEFT -> 180d;
            case BOTTOM_TO_TOP -> 270d;
            case TOP_TO_BOTTOM -> 90d;
            default -> 0d;
        };
    }

    public enum DIRECTION {
        RIGHT, LEFT
    }

    public enum PRESENTATION {
        LEFT_TO_RIGHT, RIGHT_TO_LEFT, BOTTOM_TO_TOP, TOP_TO_BOTTOM
    }

}
