package io.github.danieltuerk.controlcenter.persist.entity.track;

import io.github.danieltuerk.controlcenter.shared.track.model.Curve;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * @author Daniel Tuerk
 */
@Entity
@Table(name = "TRACKPART_CURVE")
public class CurveEntity extends AbstractTrackPartEntity {

    public Curve.DIRECTION direction;

}