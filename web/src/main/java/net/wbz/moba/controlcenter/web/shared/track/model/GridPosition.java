package net.wbz.moba.controlcenter.web.shared.track.model;

import com.google.common.base.Objects;
import com.googlecode.jmapper.annotations.JMap;

/**
 * @author Daniel Tuerk
 */
public class GridPosition extends AbstractDto {

    @JMap
    private int x;
    @JMap
    private int y;

    public GridPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public GridPosition() {
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(x, y);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GridPosition that = (GridPosition) o;
        return x == that.x &&
                y == that.y;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("x", x)
                .add("y", y)
                .toString();
    }

    public boolean isSame(GridPosition gridPosition) {
        return gridPosition != null && gridPosition.getX() == x && gridPosition.getY() == y;
    }
}
