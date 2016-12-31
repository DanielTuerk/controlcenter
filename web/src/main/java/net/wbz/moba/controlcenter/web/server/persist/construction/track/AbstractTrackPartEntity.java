package net.wbz.moba.controlcenter.web.server.persist.construction.track;

import com.google.common.collect.Sets;
import com.googlecode.jmapper.annotations.JMap;
import net.wbz.moba.controlcenter.web.server.persist.AbstractEntity;
import net.wbz.moba.controlcenter.web.server.persist.construction.ConstructionEntity;
import net.wbz.moba.controlcenter.web.shared.track.model.AbstractTrackPart;
import net.wbz.moba.controlcenter.web.shared.track.model.TrackModelConstants;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Model for a part of the track.
 * - has position in grid
 * - provide functions
 *
 * @author Daniel Tuerk
 */
@Entity(name = "TRACK_PART")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class AbstractTrackPartEntity extends AbstractEntity {

    /**
     * The corresponding construction.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private ConstructionEntity construction;

    /**
     * Position of the track part in the grid system of the construction.
     */
    @JMap
    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private GridPositionEntity gridPosition;

    @JMap
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private BusDataConfigurationEntity blockFunction;

    public AbstractTrackPartEntity() {
    }

    public ConstructionEntity getConstruction() {
        return construction;
    }

    public void setConstruction(ConstructionEntity construction) {
        this.construction = construction;
    }

    public List<TrackPartFunctionEntity> getFunctionConfigs() {
        //TODO drop
        return Collections.emptyList();
//        List<TrackPartFunctionEntity> functions = getFunctions();
//        if (functions.isEmpty()) {
//             //create dummy for saved instance without configuration; will be changed during first call of
//            functions.add(new TrackPartFunctionEntity(TrackModelConstants.DEFAULT_TOGGLE_FUNCTION, new BusDataConfigurationEntity()));
//            functions.add(new TrackPartFunctionEntity(TrackModelConstants.DEFAULT_BLOCK_FUNCTION, new BusDataConfigurationEntity()));
//        }
//        return functions;
    }

    public Set<BusDataConfigurationEntity> getConfigurationsOfFunctions() {
        Set<BusDataConfigurationEntity> configurations = Sets.newHashSet();
        for (TrackPartFunctionEntity function : getFunctionConfigs()) {
            configurations.add(function.getConfiguration());
        }
        return configurations;
    }

    /**
     * Return the {@link BusDataConfigurationEntity} of the default toggle
     * function. {@link net.wbz.moba.controlcenter.web.shared.track.model.TrackModelConstants#DEFAULT_TOGGLE_FUNCTION}
     *
     * @return {@link BusDataConfigurationEntity}
     */
    public BusDataConfigurationEntity getDefaultToggleFunctionConfig() {
        return getFunctionConfig(TrackModelConstants.DEFAULT_TOGGLE_FUNCTION);
    }

    public BusDataConfigurationEntity getDefaultBlockFunctionConfig() {
        return getFunctionConfig(TrackModelConstants.DEFAULT_BLOCK_FUNCTION);
    }

    private BusDataConfigurationEntity getFunctionConfig(String function) {
        for (TrackPartFunctionEntity trackPartFunction : getFunctionConfigs()) {
            if (trackPartFunction.getFunctionKey().equalsIgnoreCase(function)) {
                return trackPartFunction.getConfiguration();
            }
        }
        throw new RuntimeException("no function for key " + function + " found!");
    }

    public GridPositionEntity getGridPosition() {
        return gridPosition;
    }

    public void setGridPosition(GridPositionEntity gridPositionEntity) {
        this.gridPosition = gridPositionEntity;
    }

    public BusDataConfigurationEntity getBlockFunction() {
        return blockFunction;
    }

    public void setBlockFunction(BusDataConfigurationEntity blockFunction) {
        this.blockFunction = blockFunction;
    }

    public abstract Class<? extends AbstractTrackPart> getDefaultDtoClass();
}
