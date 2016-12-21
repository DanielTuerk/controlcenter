package net.wbz.moba.controlcenter.web.server.persist.construction.track;

import com.google.common.collect.Sets;
import net.wbz.moba.controlcenter.web.server.persist.AbstractEntity;
import net.wbz.moba.controlcenter.web.server.persist.construction.ConstructionEntity;
import net.wbz.moba.controlcenter.web.shared.track.model.TrackModelConstants;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Model for a part of the track.
 * - has position in grid
 * - provide functions
 *
 * @author Daniel Tuerk
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class TrackPartEntity extends AbstractEntity {

//    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
//    @Column(name = "TRACKPART_ID")
//    private long id;

    /**
     * The corresponding construction.
     * TODO remove - only access by construction?
     */
    @ManyToOne
    private ConstructionEntity construction;

    /**
     * Position of the track part in the grid system of the construction.
     */
    @ManyToOne
    private GridPositionEntity gridPositionEntity;

    /**
     * Function mapping for function name and configuration of the function.
     */
    @ManyToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    @JoinTable(joinColumns = {@JoinColumn(name = "TRACKPART_ID")}, inverseJoinColumns = {
            @JoinColumn(name = "TRACKPARTFUNCTION_ID")})
    private List<TrackPartFunctionEntity> functions;

    /**
     * TrackPartConfigurationEntity to toggle the {@link TrackPartEntity} by an event.
     */
    @OneToOne
    private EventConfigurationEntity eventStateConfig;

    public TrackPartEntity() {
    }

    public List<TrackPartFunctionEntity> getFunctions() {
        if (functions == null) {
            functions = new ArrayList<>();
        }
        return functions;
    }

    public void setFunctions(List<TrackPartFunctionEntity> functions) {
        this.functions = functions;
    }

    public void setEventStateConfig(EventConfigurationEntity eventStateConfig) {
        this.eventStateConfig = eventStateConfig;
    }

    public ConstructionEntity getConstruction() {
        return construction;
    }

    public void setConstruction(ConstructionEntity construction) {
        this.construction = construction;
    }

    public EventConfigurationEntity getEventStateConfig() {
        return eventStateConfig;
    }

    public List<TrackPartFunctionEntity> getFunctionConfigs() {
        List<TrackPartFunctionEntity> functions = getFunctions();
        if (functions.isEmpty()) {
            // create dummy for saved instance without configuration; will be changed during first call of
            functions.add(new TrackPartFunctionEntity(TrackModelConstants.DEFAULT_TOGGLE_FUNCTION, new TrackPartConfigurationEntity()));
            functions.add(new TrackPartFunctionEntity(TrackModelConstants.DEFAULT_BLOCK_FUNCTION, new TrackPartConfigurationEntity()));
        }
        return functions;
    }

    public Set<TrackPartConfigurationEntity> getConfigurationsOfFunctions() {
        Set<TrackPartConfigurationEntity> configurations = Sets.newHashSet();
        for (TrackPartFunctionEntity function : getFunctionConfigs()) {
            configurations.add(function.getConfiguration());
        }
        return configurations;
    }

    /**
     * Return the {@link TrackPartConfigurationEntity} of the default toggle
     * function. {@link net.wbz.moba.controlcenter.web.shared.track.model.TrackModelConstants#DEFAULT_TOGGLE_FUNCTION}
     *
     * @return {@link TrackPartConfigurationEntity}
     */
    public TrackPartConfigurationEntity getDefaultToggleFunctionConfig() {
        return getFunctionConfig(TrackModelConstants.DEFAULT_TOGGLE_FUNCTION);
    }

    public TrackPartConfigurationEntity getDefaultBlockFunctionConfig() {
        return getFunctionConfig(TrackModelConstants.DEFAULT_BLOCK_FUNCTION);
    }

    private TrackPartConfigurationEntity getFunctionConfig(String function) {
        for (TrackPartFunctionEntity trackPartFunction : getFunctionConfigs()) {
            if (trackPartFunction.getFunctionKey().equalsIgnoreCase(function)) {
                return trackPartFunction.getConfiguration();
            }
        }
        throw new RuntimeException("no function for key " + function + " found!");
    }

    public String getConfigurationInfo() {
        // title with function configurations
        StringBuilder functionsStringBuilder = new StringBuilder();
        for (TrackPartFunctionEntity functionEntry : getFunctionConfigs()) {
            functionsStringBuilder.append(functionEntry.getFunctionKey()).append(": ").append(functionEntry
                    .getConfiguration()).append("; ");
        }
        return functionsStringBuilder.toString();
    }

    public GridPositionEntity getGridPositionEntity() {
        return gridPositionEntity;
    }

    public void setGridPositionEntity(GridPositionEntity gridPositionEntity) {
        this.gridPositionEntity = gridPositionEntity;
    }

    public boolean hasActiveEventConfiguration() {
        return eventStateConfig != null && eventStateConfig.isActive();
    }

    public EventConfigurationEntity getEventConfiguration() {
        return eventStateConfig;
    }

    public void setEventConfiguration(EventConfigurationEntity eventConfigurationEntity) {
        eventStateConfig = eventConfigurationEntity;
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
