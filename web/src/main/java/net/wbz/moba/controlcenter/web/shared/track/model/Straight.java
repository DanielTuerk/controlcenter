package net.wbz.moba.controlcenter.web.shared.track.model;

import com.google.common.collect.Lists;
import com.googlecode.jmapper.annotations.JMap;
import java.util.Collection;

/**
 * @author Daniel Tuerk
 */
public class Straight extends AbstractTrackPart {

    @JMap
    private Straight.DIRECTION direction;

    public DIRECTION getDirection() {
        return direction;
    }

    public void setDirection(DIRECTION direction) {
        this.direction = direction;
    }

    @Override
    public Collection<GridPosition> getNextGridPositions(GridPosition previousPosition) {
        int x = getGridPosition().getX();
        int y = getGridPosition().getY();
        if (isVertical()) {
            y++;
        } else {
            x++;
        }
        return Lists.newArrayList(new GridPosition(x, y));
    }

    @Override
    public Collection<GridPosition> getLastGridPositions() {
        int x = getGridPosition().getX();
        int y = getGridPosition().getY();
        if (isVertical()) {
            y--;
        } else {
            x--;
        }
        return Lists.newArrayList(new GridPosition(x, y));
    }

    @Override
    public double getRotationAngle() {
        if (isVertical()) {
            return 90d;
        }
        return 0d;
    }

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
