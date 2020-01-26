package net.wbz.moba.controlcenter.web.server.persist.construction.track;

import com.googlecode.jmapper.annotations.JMap;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import net.wbz.moba.controlcenter.web.server.persist.AbstractEntity;
import net.wbz.moba.controlcenter.web.server.persist.construction.ConstructionEntity;

/**
 * @author Daniel Tuerk
 */
@Entity(name = "GRID_POSITION")
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"x", "y", "construction_id"})})
public class GridPositionEntity extends AbstractEntity {

    @JMap
    @Column
    private int x;

    @JMap
    @Column
    private int y;

    /**
     * The corresponding construction.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private ConstructionEntity construction;

    public GridPositionEntity() {

    }

    public GridPositionEntity(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public ConstructionEntity getConstruction() {
        return construction;
    }

    public void setConstruction(ConstructionEntity construction) {
        this.construction = construction;
    }
}
