package net.wbz.moba.controlcenter.persist.entity.track;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import net.wbz.moba.controlcenter.shared.track.model.Curve;

/**
 * @author Daniel Tuerk
 */
@Entity
@Table(name = "TRACKPART_CURVE")
public class CurveEntity extends AbstractTrackPartEntity {

    public Curve.DIRECTION direction;

}