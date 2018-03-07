package net.wbz.moba.controlcenter.web.shared.track.model;

import java.util.Collection;

import com.google.common.collect.Lists;
import com.googlecode.jmapper.annotations.JMap;

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

    private boolean isVertical() {
        return getDirection() == DIRECTION.VERTICAL;
    }

    public enum DIRECTION {
        HORIZONTAL, VERTICAL
    }
}
