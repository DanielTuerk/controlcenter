package net.wbz.moba.controlcenter.persist.entity.track;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;



import net.wbz.moba.controlcenter.shared.track.model.AbstractTrackPart;
import net.wbz.moba.controlcenter.shared.track.model.Straight;

/**
 * @author Daniel Tuerk
 */
@Entity
@Table(name = "TRACKPART_STRAIGHT")
public class StraightEntity extends AbstractTrackPartEntity {

    public Straight.DIRECTION direction;

}
