package net.wbz.moba.controlcenter.web.server.persist.construction.track;

import net.wbz.moba.controlcenter.web.shared.track.model.Curve;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author Daniel Tuerk
 */
@Entity
@Table(name = "trackpart_curve")
public class CurveEntity extends TrackPartEntity {

//    public enum DIRECTION {BOTTOM_RIGHT, BOTTOM_LEFT, TOP_LEFT, TOP_RIGHT}

    public Curve.DIRECTION direction;

    public Curve.DIRECTION getDirection() {
        return direction;
    }

    public void setDirection(Curve.DIRECTION direction) {
        this.direction = direction;
    }


}
