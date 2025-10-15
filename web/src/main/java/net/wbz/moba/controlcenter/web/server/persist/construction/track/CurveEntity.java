package net.wbz.moba.controlcenter.web.server.persist.construction.track;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;



import net.wbz.moba.controlcenter.shared.track.model.AbstractTrackPart;
import net.wbz.moba.controlcenter.shared.track.model.Curve;

/**
 * @author Daniel Tuerk
 */
@Entity
@Table(name = "TRACKPART_CURVE")
public class CurveEntity extends AbstractTrackPartEntity {

    private Curve.DIRECTION direction;

    public Curve.DIRECTION getDirection() {
        return direction;
    }

    public void setDirection(Curve.DIRECTION direction) {
        this.direction = direction;
    }

    @Override
    public Class<? extends AbstractTrackPart> getDefaultDtoClass() {
        return Curve.class;
    }
}
