package net.wbz.moba.controlcenter.web.server.persist.construction.track;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.googlecode.jmapper.annotations.JMap;

import net.wbz.moba.controlcenter.web.shared.track.model.AbstractTrackPart;
import net.wbz.moba.controlcenter.web.shared.track.model.Straight;

/**
 * @author Daniel Tuerk
 */
@Entity
@Table(name = "TRACKPART_STRAIGHT")
public class StraightEntity extends AbstractTrackPartEntity {

    @JMap
    @Column
    private Straight.DIRECTION direction;

    public StraightEntity() {
    }

    @Override
    public Class<? extends AbstractTrackPart> getDefaultDtoClass() {
        return Straight.class;
    }

    public Straight.DIRECTION getDirection() {
        return direction;
    }

    public void setDirection(Straight.DIRECTION direction) {
        this.direction = direction;
    }

}
