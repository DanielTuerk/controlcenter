package net.wbz.moba.controlcenter.web.shared.track.model;

import com.google.common.collect.Sets;
import net.wbz.moba.controlcenter.web.server.persist.construction.track.TrackPartConfigurationEntity;
import net.wbz.moba.controlcenter.web.server.persist.construction.track.TrackPartFunctionEntity;
import net.wbz.moba.controlcenter.web.shared.bus.BusAddressBit;

import java.util.Set;

/**
 * @author Daniel Tuerk
 */
public class TrackPart extends AbstractDto {
    private GridPosition gridPosition;

    public void setGridPosition(GridPosition gridPosition) {
        this.gridPosition = gridPosition;
    }

    public GridPosition getGridPosition() {
        return gridPosition;
    }

    public BusAddressBit getDefaultToggleFunctionConfig() {
        return null;
    }

    public EventConfiguration getEventConfiguration() {
        return null;
    }

    public TrackPartConfiguration getDefaultBlockFunctionConfig() {
        return null;
    }

    public Set<TrackPartConfiguration> getConfigurationsOfFunctions() {
        Set<TrackPartConfiguration> configurations = Sets.newHashSet();
//        for (TrackPartFunctionEntity function : getFunctionConfigs()) {
//            configurations.add(function.getConfiguration());
//        }
        return configurations;
    }

    public String getConfigurationInfo() {
        // title with function configurations
        StringBuilder functionsStringBuilder = new StringBuilder();
//        for (TrackPartFunctionEntity functionEntry : getFunctionConfigs()) {
//            functionsStringBuilder.append(functionEntry.getFunctionKey()).append(": ").append(functionEntry
//                    .getConfiguration()).append("; ");
//        }
        return functionsStringBuilder.toString();
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
