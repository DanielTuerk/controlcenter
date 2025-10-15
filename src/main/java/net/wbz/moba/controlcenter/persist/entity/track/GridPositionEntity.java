package net.wbz.moba.controlcenter.persist.entity.track;


import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import net.wbz.moba.controlcenter.persist.entity.AbstractEntity;
import net.wbz.moba.controlcenter.persist.entity.ConstructionEntity;

/**
 * @author Daniel Tuerk
 */
@Entity
@Table(name = "GRID_POSITION", uniqueConstraints = {@UniqueConstraint(columnNames = {"x", "y", "construction_id"})})
public class GridPositionEntity extends AbstractEntity {

    public int x;

    public int y;

    /**
     * The corresponding construction.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    public ConstructionEntity construction;

}
