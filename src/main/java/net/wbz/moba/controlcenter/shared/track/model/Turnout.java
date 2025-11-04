package net.wbz.moba.controlcenter.shared.track.model;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

/**
 * @author Daniel Tuerk
 */
@Schema(description = "type for a track part")
@Tag(ref = "track")
public class Turnout extends AbstractTrackPart implements HasToggleFunction {

    private Turnout.DIRECTION currentDirection;
    private Turnout.PRESENTATION currentPresentation;
    private BusDataConfiguration toggleFunction;
    private EventConfiguration eventConfiguration;

    public DIRECTION getCurrentDirection() {
        return currentDirection;
    }

    public void setCurrentDirection(DIRECTION currentDirection) {
        this.currentDirection = currentDirection;
    }

    public PRESENTATION getCurrentPresentation() {
        return currentPresentation;
    }

    public void setCurrentPresentation(PRESENTATION currentPresentation) {
        this.currentPresentation = currentPresentation;
    }

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

    public GridPosition getNextGridPositionForStateBranch() {
        switch (currentDirection) {
            case RIGHT:
                return getNextGridPositionForStateBranchToRight();
            case LEFT:
                return getNextGridPositionForStateBranchToLeft();
        }
        throw new RuntimeException("unknown direction: " + currentDirection.name());
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

    @Override
    public Collection<GridPosition> getLastGridPositions() {
        return Lists.newArrayList(getLastGridPosition());
    }

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
    @Override
    public double getRotationAngle() {
        switch (getCurrentPresentation()) {
            case LEFT_TO_RIGHT:
                return 0d;
            case RIGHT_TO_LEFT:
                return 180d;
            case BOTTOM_TO_TOP:
                return 270d;
            case TOP_TO_BOTTOM:
                return 90d;
            default:
                return 0d;
        }
    }

    public enum DIRECTION {
        RIGHT, LEFT
    }

    public enum PRESENTATION {
        LEFT_TO_RIGHT, RIGHT_TO_LEFT, BOTTOM_TO_TOP, TOP_TO_BOTTOM
    }

    public enum STATE {
        STRAIGHT, BRANCH
    }
}
