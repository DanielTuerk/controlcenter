package io.github.danieltuerk.controlcenter.persist.entity.track;


import io.github.danieltuerk.controlcenter.persist.entity.AbstractEntity;
import io.github.danieltuerk.controlcenter.shared.track.model.TrackBlock.DRIVING_LEVEL_ADJUST_TYPE;
import jakarta.persistence.*;

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

    @Column(name = "construction_id", nullable = false)
    public Long constructionId;

    /**
     * Display-name for the block.
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
