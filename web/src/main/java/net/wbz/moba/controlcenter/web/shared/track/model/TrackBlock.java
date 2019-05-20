package net.wbz.moba.controlcenter.web.shared.track.model;

import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.gwt.user.client.rpc.IsSerializable;
import com.googlecode.jmapper.annotations.JMap;
import net.wbz.moba.controlcenter.web.shared.constrution.Construction;

/**
 * @author Daniel Tuerk
 */
public class TrackBlock extends AbstractDto {

    @JMap
    private BusDataConfiguration blockFunction;
    @JMap
    private Construction construction;
    @JMap
    private String name;
    @JMap
    private Integer forwardTargetDrivingLevel;
    /**
     * @see #forwardTargetDrivingLevel for 'backward'.
     */
    @JMap
    private Integer backwardTargetDrivingLevel;
    @JMap
    private DRIVING_LEVEL_ADJUST_TYPE drivingLevelAdjustType;

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

    @Override
    public String toString() {
        return "TrackBlock{" + "blockFunction=" + blockFunction + ", name='" + name + '\'' + (
            drivingLevelAdjustType != DRIVING_LEVEL_ADJUST_TYPE.NONE ? (", drivingLevelAdjustType='"
                + drivingLevelAdjustType + '\'' + ", name='" + forwardTargetDrivingLevel + '\'' + ", name='"
                + backwardTargetDrivingLevel + '\'') : "") + "} " + super.toString();
    }

    public String getDisplayValue() {
        return (Strings.isNullOrEmpty(name) ? "-" : name) + " (" + (blockFunction != null ? String
            .valueOf(blockFunction.getAddress()) : "-") + ", " + (blockFunction != null ? blockFunction.getBit()
            : "-") + ")";
    }

    public enum DRIVING_LEVEL_ADJUST_TYPE implements IsSerializable {
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
