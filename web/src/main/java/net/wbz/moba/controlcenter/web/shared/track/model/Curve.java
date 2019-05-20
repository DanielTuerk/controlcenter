package net.wbz.moba.controlcenter.web.shared.track.model;

import com.google.common.collect.Lists;
import com.google.gwt.user.client.rpc.IsSerializable;
import java.util.Collection;

import com.googlecode.jmapper.annotations.JMap;

/**
 * @author Daniel Tuerk
 */
public class Curve extends AbstractTrackPart {
    @JMap
    private DIRECTION direction;

    public DIRECTION getDirection() {
        return direction;
    }

    public void setDirection(DIRECTION direction) {
        this.direction = direction;
    }

    @Override
    public Collection<GridPosition> getNextGridPositions(GridPosition previousPosition) {
        return Lists.newArrayList(getNextGridPosition());
    }

    @Override
    public Collection<GridPosition> getLastGridPositions() {
        return Lists.newArrayList(getLastGridPosition());
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
        switch (getDirection()) {
            case BOTTOM_LEFT:
                return 90d;
            case BOTTOM_RIGHT:
                return 0d;
            case TOP_LEFT:
                return 180d;
            case TOP_RIGHT:
                return 270d;
            default:
                return 0d;
        }
    }

    @Override
    public String toString() {
        return "Curve{" + "direction=" + direction+ "} "  +super.toString();
    }

    public enum DIRECTION implements IsSerializable {
        BOTTOM_RIGHT, BOTTOM_LEFT, TOP_LEFT, TOP_RIGHT
    }
}
