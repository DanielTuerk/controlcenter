package net.wbz.moba.controlcenter.web.shared.track.model;

/**
 * @author Daniel Tuerk
 */
public class GridPosition extends AbstractDto {

    private int x;
    private int y;

    public GridPosition(int x, int y) {
        this.x=x;
        this.y=y;
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
}
