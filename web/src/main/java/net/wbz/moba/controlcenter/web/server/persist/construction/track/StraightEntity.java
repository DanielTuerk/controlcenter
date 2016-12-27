package net.wbz.moba.controlcenter.web.server.persist.construction.track;

import com.googlecode.jmapper.annotations.JMap;
import net.wbz.moba.controlcenter.web.shared.track.model.AbstractTrackPart;
import net.wbz.moba.controlcenter.web.shared.track.model.Straight;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author Daniel Tuerk
 */
@Entity
@Table(name = "trackpart_straight")
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
