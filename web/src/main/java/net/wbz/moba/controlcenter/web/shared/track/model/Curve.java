package net.wbz.moba.controlcenter.web.shared.track.model;

import com.googlecode.jmapper.annotations.JMap;

/**
 * @author Daniel Tuerk
 */
public class Curve extends AbstractTrackPart {
    public enum DIRECTION {BOTTOM_RIGHT, BOTTOM_LEFT, TOP_LEFT, TOP_RIGHT}

    @JMap
    private DIRECTION direction;

    public DIRECTION getDirection() {
        return direction;
    }

    public void setDirection(DIRECTION direction) {
        this.direction = direction;
    }

    @Override
    public double getRotationAngle() {
        switch (getDirection()) {
            case BOTTOM_LEFT:
                return 270d;
            case BOTTOM_RIGHT:
                return 0d;
            case TOP_LEFT:
                return 180d;
            case TOP_RIGHT:
                return 90d;
            default:
                return 0d;
        }
    }
}
