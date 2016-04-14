package net.wbz.moba.controlcenter.web.shared.track.model;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * @author Daniel Tuerk
 */
@Entity
@Table(name = "trackpart_straight")
public class Straight extends TrackPart {

    public enum DIRECTION {HORIZONTAL, VERTICAL};

    private DIRECTION direction;

    @OneToOne
    private TrackPart trackPart;

    public Straight() {
    }

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
