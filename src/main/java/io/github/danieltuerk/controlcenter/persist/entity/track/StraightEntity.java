package io.github.danieltuerk.controlcenter.persist.entity.track;

import io.github.danieltuerk.controlcenter.shared.track.model.Straight;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

/**
 * @author Daniel Tuerk
 */
@Entity
@Table(name = "TRACKPART_STRAIGHT")
public class StraightEntity extends AbstractTrackPartEntity {

    @Enumerated(EnumType.ORDINAL)
    public Straight.DIRECTION direction;

}
