package net.wbz.moba.controlcenter.persist.entity.track;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import net.wbz.moba.controlcenter.persist.entity.AbstractEntity;
import net.wbz.moba.controlcenter.persist.entity.ConstructionEntity;
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
    @ManyToOne(fetch = FetchType.LAZY)
    public ConstructionEntity construction;

    /**
     * Position of the track part in the grid system of the construction.
     */
    @OneToOne(fetch = FetchType.EAGER, cascade = { CascadeType.ALL })
    @OnDelete(action = OnDeleteAction.CASCADE)
    public GridPositionEntity gridPosition;

//    public abstract Class<? extends AbstractTrackPart> getDefaultDtoClass();
}
