package net.wbz.moba.controlcenter.web.shared.track.model;

import com.google.common.collect.Sets;
import com.google.gwt.user.client.rpc.IsSerializable;
import net.sf.gilead.pojo.gwt.LightEntity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Model for a part of the track.
 * - has position in grid
 * - provide functions
 *
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@MappedSuperclass
public class TrackPart extends LightEntity implements IsSerializable, Serializable {

    @Id
    @GeneratedValue
    @Column(name = "TRACKPART_ID")
    private long id;

    /**
     * Id of the corresponding construction.
     */
    private long constructionId;

    /**
     * Position of the track part in the grid system of the construction.
     */
    @ManyToOne
    private GridPosition gridPosition;

    /**
     * Function mapping for function name and configuration of the function.
     */
    @ManyToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    @JoinTable(
            joinColumns = {@JoinColumn(name = "TRACKPART_ID")},
            inverseJoinColumns = {@JoinColumn(name = "TRACKPARTFUNCTION_ID")})
    private List<TrackPartFunction> functions = new ArrayList<>();

    /**
     * Configuration to toggle the {@link net.wbz.moba.controlcenter.web.shared.track.model.TrackPart} by an event.
     */
    @OneToOne
    private EventConfiguration eventStateConfig;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<TrackPartFunction> getFunctions() {
        if (functions == null) {
            functions = new ArrayList<>();
        }
        return functions;
    }

    public void setFunctions(List<TrackPartFunction> functions) {
        this.functions = functions;
    }

    public void setEventStateConfig(EventConfiguration eventStateConfig) {
        this.eventStateConfig = eventStateConfig;
    }

    public long getConstructionId() {
        return constructionId;
    }

    public void setConstructionId(long constructionId) {
        this.constructionId = constructionId;
    }

    public List<TrackPartFunction> getFunctionConfigs() {
        List<TrackPartFunction> functions = getFunctions();
        if (functions.isEmpty()) {
            // create dummy for saved instance without configuration; will be changed during first call of
            functions.add(new TrackPartFunction(TrackModelConstants.DEFAULT_TOGGLE_FUNCTION, new Configuration()));
            functions.add(new TrackPartFunction(TrackModelConstants.DEFAULT_BLOCK_FUNCTION, new Configuration()));
        }
        return functions;
    }

    public Set<Configuration> getConfigurationsOfFunctions() {
        Set<Configuration> configurations = Sets.newHashSet();
        for (TrackPartFunction function : getFunctionConfigs()) {
            configurations.add(function.getConfiguration());
        }
        return configurations;
    }

    /**
     * Return the {@link net.wbz.moba.controlcenter.web.shared.track.model.Configuration} of the default toggle
     * function. {@link net.wbz.moba.controlcenter.web.shared.track.model.TrackModelConstants#DEFAULT_TOGGLE_FUNCTION}
     *
     * @return {@link net.wbz.moba.controlcenter.web.shared.track.model.Configuration}
     */
    public Configuration getDefaultToggleFunctionConfig() {
        return getFunctionConfig(TrackModelConstants.DEFAULT_TOGGLE_FUNCTION);
    }

    public Configuration getDefaultBlockFunctionConfig() {
        return getFunctionConfig(TrackModelConstants.DEFAULT_BLOCK_FUNCTION);
    }

    private Configuration getFunctionConfig(String function) {
        for (TrackPartFunction trackPartFunction : getFunctionConfigs()) {
            if (trackPartFunction.getFunctionKey().equalsIgnoreCase(function)) {
                return trackPartFunction.getConfiguration();
            }
        }
        throw new RuntimeException("no function for key " + function + " found!");
    }

    public String getConfigurationInfo() {
        // title with function configurations
        StringBuilder functionsStringBuilder = new StringBuilder();
        for (TrackPartFunction functionEntry : getFunctionConfigs()) {
            functionsStringBuilder.append(functionEntry.getFunctionKey()).append(": ").append(functionEntry.getConfiguration()).append("; ");
        }
        return functionsStringBuilder.toString();
    }

    public GridPosition getGridPosition() {
        return gridPosition;
    }

    public void setGridPosition(GridPosition gridPosition) {
        this.gridPosition = gridPosition;
    }

    public boolean hasActiveEventConfiguration() {
        return eventStateConfig != null && eventStateConfig.isActive();
    }

    public EventConfiguration getEventConfiguration() {
        return eventStateConfig;
    }

    public void setEventConfiguration(EventConfiguration eventConfiguration) {
        eventStateConfig = eventConfiguration;
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
