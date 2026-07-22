package io.github.danieltuerk.controlcenter.persist.entity.track;


import io.github.danieltuerk.controlcenter.persist.entity.AbstractEntity;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * Model for a part of the track.
 * - has position in grid
 * - provide functions
 *
 * @author Daniel Tuerk
 */
@Entity
@Table(name = "TRACK_PART")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class AbstractTrackPartEntity extends AbstractEntity {

    /**
     * The corresponding construction.
     */
    @Column(name = "construction_id", nullable = false)
    public Long constructionId;

    /**
     * Position of the track part in the grid system of the construction.
     */
    @OneToOne(fetch = FetchType.EAGER, cascade = { CascadeType.ALL })
    @OnDelete(action = OnDeleteAction.CASCADE)
    public GridPositionEntity gridPosition;

}
