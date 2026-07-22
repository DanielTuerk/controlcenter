package io.github.danieltuerk.controlcenter.persist.entity.track;


import io.github.danieltuerk.controlcenter.persist.entity.AbstractEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

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
    @Column(name = "construction_id", nullable = false)
    public Long constructionId;

}
