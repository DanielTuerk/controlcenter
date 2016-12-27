package net.wbz.moba.controlcenter.web.shared.track.model;

import com.google.common.collect.Sets;
import com.googlecode.jmapper.annotations.JMap;

import java.util.Set;

/**
 * @author Daniel Tuerk
 */
public abstract class AbstractTrackPart extends AbstractDto {

    @JMap
    private GridPosition gridPosition;

    @JMap
    private BusDataConfiguration blockFunction;

    public void setGridPosition(GridPosition gridPosition) {
        this.gridPosition = gridPosition;
    }

    public GridPosition getGridPosition() {
        return gridPosition;
    }


    public Set<BusDataConfiguration> getConfigurationsOfFunctions() {
        //TODO
        Set<BusDataConfiguration> configurations = Sets.newHashSet();
//        for (TrackPartFunctionEntity function : getFunctionConfigs()) {
//            configurations.add(function.getConfiguration());
//        }
        return configurations;
    }

    public BusDataConfiguration getBlockFunction() {
        return blockFunction;
    }

    public void setBlockFunction(BusDataConfiguration blockFunction) {
        this.blockFunction = blockFunction;
    }

    /**
     * Rotation angle in degree of the track part.
     *
     * @return angle in degree
     */
    public double getRotationAngle() {
        return 0;
    }
}
