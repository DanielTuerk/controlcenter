package net.wbz.moba.controlcenter.shared.track.model;

import io.quarkus.runtime.util.StringUtil;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Daniel Tuerk
 */
@Getter
public class TrackBlock extends AbstractDto {

    private BusDataConfiguration blockFunction;
    @Setter
    private String name;
    @Setter
    private Integer forwardTargetDrivingLevel;
    /**
     * @see #forwardTargetDrivingLevel for 'backward'.
     */
    @Setter
    private Integer backwardTargetDrivingLevel;
    @Setter
    private DRIVING_LEVEL_ADJUST_TYPE drivingLevelAdjustType;

    @Setter
    private Boolean feedback;

    public void setBlockFunction(BusDataConfiguration blockFunction) {
        if (blockFunction != null) {
            // TODO
            blockFunction.setBus(1);
        }
        this.blockFunction = blockFunction;
    }

    @Override
    public String toString() {
        return "TrackBlock{" + "blockFunction=" + blockFunction
            + ", name='" + name + '\''
            + ", forwardTargetDrivingLevel=" + forwardTargetDrivingLevel
            + ", backwardTargetDrivingLevel=" + backwardTargetDrivingLevel
            + ", drivingLevelAdjustType=" + drivingLevelAdjustType
            + ", feedback=" + feedback
            + '}';
    }

    public String getDisplayValue() {
        return (StringUtil.isNullOrEmpty(name) ? "-" : name) + " (" + (blockFunction != null ? String
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
