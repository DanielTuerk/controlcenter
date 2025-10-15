package net.wbz.moba.controlcenter.persist.entity.track;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import net.wbz.moba.controlcenter.persist.entity.AbstractEntity;
import net.wbz.moba.controlcenter.persist.entity.ConstructionEntity;
import net.wbz.moba.controlcenter.shared.track.model.TrackBlock.DRIVING_LEVEL_ADJUST_TYPE;

/**
 * Block on track as state receiver for a track detector.
 * Detect trains on the {@link BusDataConfigurationEntity}.
 *
 * @author Daniel Tuerk
 */
@Entity
@Table(name = "TRACK_BLOCK")
public class TrackBlockEntity extends AbstractEntity {

    /**
     * Configuration of the detector.
     */
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    public BusDataConfigurationEntity blockFunction;

    @ManyToOne(fetch = FetchType.LAZY)
    public ConstructionEntity construction;

    /**
     * Display name for the block.
     */
    public String name;

    /**
     * Target driving level to set to train which is entering with driving direction 'forward'.
     * Could be {@code null} to don't change driving level of train.
     */
    public Integer forwardTargetDrivingLevel;

    /**
     * @see #forwardTargetDrivingLevel for 'backward'.
     */
    public Integer backwardTargetDrivingLevel;

    /**
     * Type of the driving level adjustment.
     */
    @Column(nullable = false, columnDefinition = "int default 0")
    @Enumerated(EnumType.ORDINAL)
    public DRIVING_LEVEL_ADJUST_TYPE drivingLevelAdjustType;

    /**
     * Block has feedback function.
     */
    @Column(nullable = false)
    public Boolean feedback;

}
