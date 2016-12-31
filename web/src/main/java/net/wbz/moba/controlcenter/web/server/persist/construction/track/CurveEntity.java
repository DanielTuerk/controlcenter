package net.wbz.moba.controlcenter.web.server.persist.construction.track;

import com.googlecode.jmapper.annotations.JMap;
import net.wbz.moba.controlcenter.web.shared.track.model.AbstractTrackPart;
import net.wbz.moba.controlcenter.web.shared.track.model.Curve;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author Daniel Tuerk
 */
@Entity
@Table(name = "TRACKPART_CURVE")
public class CurveEntity extends AbstractTrackPartEntity {

    @JMap
    @Column
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
