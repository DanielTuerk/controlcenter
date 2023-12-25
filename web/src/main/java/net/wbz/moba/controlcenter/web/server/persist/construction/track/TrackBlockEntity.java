package net.wbz.moba.controlcenter.web.server.persist.construction.track;

import com.googlecode.jmapper.annotations.JMap;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import net.wbz.moba.controlcenter.web.server.persist.AbstractEntity;
import net.wbz.moba.controlcenter.web.server.persist.construction.ConstructionEntity;
import net.wbz.moba.controlcenter.web.shared.track.model.TrackBlock.DRIVING_LEVEL_ADJUST_TYPE;
import org.hibernate.annotations.ColumnDefault;

/**
 * Block on track as state receiver for a track detector.
 * Detect trains on the {@link BusDataConfigurationEntity}.
 *
 * @author Daniel Tuerk
 */
@Entity(name = "TRACK_BLOCK")
public class TrackBlockEntity extends AbstractEntity {

    /**
     * Configuration of the detector.
     */
    @JMap
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private BusDataConfigurationEntity blockFunction;

    @JMap
    @ManyToOne(fetch = FetchType.LAZY)
    private ConstructionEntity construction;

    /**
     * Display name for the block.
     */
    @JMap
    @Column
    private String name;

    /**
     * Target driving level to set to train which is entering with driving direction 'forward'.
     * Could be {@code null} to don't change driving level of train.
     */
    @JMap
    @Column
    private Integer forwardTargetDrivingLevel;

    /**
     * @see #forwardTargetDrivingLevel for 'backward'.
     */
    @JMap
    @Column
    private Integer backwardTargetDrivingLevel;

    /**
     * Type of the driving level adjustment.
     */
    @JMap
    @Column(nullable = false, columnDefinition = "int default 0")
    @Enumerated(EnumType.ORDINAL)
    private DRIVING_LEVEL_ADJUST_TYPE drivingLevelAdjustType;

    /**
     * Block has feedback function.
     */
    @JMap
    @Column(nullable = false, columnDefinition = "int default 1")
    private Boolean feedback;

    public TrackBlockEntity() {
    }

    public BusDataConfigurationEntity getBlockFunction() {
        return blockFunction;
    }

    public void setBlockFunction(BusDataConfigurationEntity blockFunction) {
        this.blockFunction = blockFunction;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ConstructionEntity getConstruction() {
        return construction;
    }

    public void setConstruction(ConstructionEntity construction) {
        this.construction = construction;
    }

    public Integer getForwardTargetDrivingLevel() {
        return forwardTargetDrivingLevel;
    }

    public void setForwardTargetDrivingLevel(Integer forwardTargetDrivingLevel) {
        this.forwardTargetDrivingLevel = forwardTargetDrivingLevel;
    }

    public Integer getBackwardTargetDrivingLevel() {
        return backwardTargetDrivingLevel;
    }

    public void setBackwardTargetDrivingLevel(Integer backwardTargetDrivingLevel) {
        this.backwardTargetDrivingLevel = backwardTargetDrivingLevel;
    }

    public DRIVING_LEVEL_ADJUST_TYPE getDrivingLevelAdjustType() {
        return drivingLevelAdjustType;
    }

    public void setDrivingLevelAdjustType(
        DRIVING_LEVEL_ADJUST_TYPE drivingLevelAdjustType) {
        this.drivingLevelAdjustType = drivingLevelAdjustType;
    }

    public Boolean getFeedback() {
        return feedback;
    }

    public void setFeedback(Boolean feedback) {
        this.feedback = feedback;
    }
}
