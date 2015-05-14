package net.wbz.moba.controlcenter.web.shared.track.model;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
@Entity
@Table(name = "trackpart_straight")
public class Straight extends TrackPart {

    public enum DIRECTION {HORIZONTAL, VERTICAL};

    public DIRECTION direction;

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
