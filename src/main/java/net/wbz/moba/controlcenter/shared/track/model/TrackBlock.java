package net.wbz.moba.controlcenter.shared.track.model;

import com.google.common.base.Strings;

import net.wbz.moba.controlcenter.shared.constrution.Construction;

/**
 * @author Daniel Tuerk
 */
public class TrackBlock extends AbstractDto {

    private BusDataConfiguration blockFunction;
    private Construction construction;
    private String name;
    private Integer forwardTargetDrivingLevel;
    /**
     * @see #forwardTargetDrivingLevel for 'backward'.
     */
    private Integer backwardTargetDrivingLevel;
    private DRIVING_LEVEL_ADJUST_TYPE drivingLevelAdjustType;

    private Boolean feedback;

    public BusDataConfiguration getBlockFunction() {
        return blockFunction;
    }

    public void setBlockFunction(BusDataConfiguration blockFunction) {
        if (blockFunction != null) {
            // TODO
            blockFunction.setBus(1);
        }
        this.blockFunction = blockFunction;
    }

    public Construction getConstruction() {
        return construction;
    }

    public void setConstruction(Construction construction) {
        this.construction = construction;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public void setDrivingLevelAdjustType(DRIVING_LEVEL_ADJUST_TYPE drivingLevelAdjustType) {
        this.drivingLevelAdjustType = drivingLevelAdjustType;
    }

    public Boolean getFeedback() {
        return feedback;
    }

    public void setFeedback(Boolean feedback) {
        this.feedback = feedback;
    }

    @Override
    public String toString() {
        return "TrackBlock{" + "blockFunction=" + blockFunction
            + ", construction=" + construction
            + ", name='" + name + '\''
            + ", forwardTargetDrivingLevel=" + forwardTargetDrivingLevel
            + ", backwardTargetDrivingLevel=" + backwardTargetDrivingLevel
            + ", drivingLevelAdjustType=" + drivingLevelAdjustType
            + ", feedback=" + feedback
            + '}';
    }

    public String getDisplayValue() {
        return (Strings.isNullOrEmpty(name) ? "-" : name) + " (" + (blockFunction != null ? String
            .valueOf(blockFunction.getAddress()) : "-") + ", " + (blockFunction != null ? blockFunction.getBit()
            : "-") + ")";
    }

    public enum DRIVING_LEVEL_ADJUST_TYPE {
        /**
         * No adjustment.
         */
        NONE, /**
         * Adjust on entering the block.
         */
        ENTER, /**
         * Adjust after leaving the block
         */
        EXIT
    }
}
