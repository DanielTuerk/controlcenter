package net.wbz.moba.controlcenter.web.server.persist.construction.track;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author Daniel Tuerk
 */
@Entity
@Table(name = "trackpart_straight")
public class StraightEntity extends TrackPartEntity {

    public enum DIRECTION {HORIZONTAL, VERTICAL};

    private DIRECTION direction;

    public StraightEntity() {
    }

    public DIRECTION getDirection() {
        return direction;
    }

    public void setDirection(DIRECTION direction) {
        this.direction = direction;
    }

    @Override
    public double getRotationAngle() {
        if (getDirection() == StraightEntity.DIRECTION.VERTICAL) {
            return 90d;
        }
        return 0d;
    }
}
