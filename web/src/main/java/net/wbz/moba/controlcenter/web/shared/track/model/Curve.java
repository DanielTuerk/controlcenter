package net.wbz.moba.controlcenter.web.shared.track.model;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
@Entity
@Table(name = "trackpart_curve")
public class Curve extends TrackPart {

    public enum DIRECTION {BOTTOM_RIGHT, BOTTOM_LEFT, TOP_LEFT, TOP_RIGHT}

    public DIRECTION direction;

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
