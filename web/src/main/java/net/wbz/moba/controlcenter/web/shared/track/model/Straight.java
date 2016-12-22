package net.wbz.moba.controlcenter.web.shared.track.model;

/**
 * @author Daniel Tuerk
 */
public class Straight extends TrackPart {

    public enum DIRECTION {HORIZONTAL, VERTICAL};

    private Straight.DIRECTION direction;

    public DIRECTION getDirection() {
        return direction;
    }

    public void setDirection(DIRECTION direction) {
        this.direction = direction;
    }

    @Override
    public double getRotationAngle() {
        if (getDirection() == Straight.DIRECTION.VERTICAL) {
            return 90d;
        }
        return 0d;
    }
}
