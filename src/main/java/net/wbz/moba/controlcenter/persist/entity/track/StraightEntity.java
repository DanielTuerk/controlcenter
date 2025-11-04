package net.wbz.moba.controlcenter.persist.entity.track;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import net.wbz.moba.controlcenter.shared.track.model.Straight;

/**
 * @author Daniel Tuerk
 */
@Entity
@Table(name = "TRACKPART_STRAIGHT")
public class StraightEntity extends AbstractTrackPartEntity {

    @Enumerated(EnumType.ORDINAL)
    public Straight.DIRECTION direction;

}
