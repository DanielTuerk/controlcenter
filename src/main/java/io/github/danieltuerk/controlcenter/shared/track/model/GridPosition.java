package io.github.danieltuerk.controlcenter.shared.track.model;


import lombok.Getter;
import lombok.Setter;

/**
 * @author Daniel Tuerk
 */
@Setter
@Getter
public class GridPosition extends AbstractDto {

    private int x;
    private int y;

    public GridPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public GridPosition() {
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
        return x == that.x && y == that.y;
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "GridPosition{" + "x=" + x + ", y=" + y + '}';
    }

}
