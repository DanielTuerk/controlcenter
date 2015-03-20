package net.wbz.moba.controlcenter.web.shared.track.model;

import com.google.common.collect.Sets;
import com.google.gwt.user.client.rpc.IsSerializable;
import net.sf.gilead.pojo.gwt.LightEntity;
import net.wbz.moba.controlcenter.web.shared.constrution.Construction;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

/**
 * Model for a part of the track.
 * - has position in grid
 * - provide functions
 *
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
@Entity
public class TrackPart extends LightEntity implements IsSerializable, Serializable {

    @Id
    @GeneratedValue
    private Long id;

    public Long getId() {
        return id;
    }

//    public void setId(Long id) {
//        this.id = id;
//    }

    private long constructionId;

    /**
     * Position of the track part in the grid system of the construction.
     */
    private GridPosition gridPosition;

    /**
     * Function mapping for function name and configuration of the function.
     */
    @OneToMany(mappedBy="trackPart")
    private Set<TrackPartFunction> functions;

    public long getConstructionId() {
        return constructionId;
    }

    public void setConstructionId(long constructionId) {
        this.constructionId = constructionId;
    }

    /**
     * Configuration to toggle the {@link net.wbz.moba.controlcenter.web.shared.track.model.TrackPart} by an event.
     */
    private EventConfiguration eventConfiguration;

    public Set<TrackPartFunction> getFunctionConfigs() {
        if (functions.isEmpty()) {
            // create dummy for saved instance without configuration; will be changed during first call of
            // {@link TrackPart#setFunctionConfigs}
//            functionConfigs = new HashMap<>();
//            functionConfigs.put(TrackModelConstants.DEFAULT_TOGGLE_FUNCTION, new Configuration());
//            functionConfigs.put(TrackModelConstants.DEFAULT_BLOCK_FUNCTION, new Configuration());
            functions.add(new TrackPartFunction(this, TrackModelConstants.DEFAULT_TOGGLE_FUNCTION, new Configuration()));
            functions.add(new TrackPartFunction(this, TrackModelConstants.DEFAULT_BLOCK_FUNCTION, new Configuration()));
        }
        return functions;
    }

    public Set<Configuration> getConfigurationsOfFunctions() {
        Set<Configuration> configurations = Sets.newHashSet();
        for(TrackPartFunction function:getFunctionConfigs()) {
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
//        if (!getFunctionConfigs().containsKey(TrackModelConstants.DEFAULT_TOGGLE_FUNCTION)) {
//            getFunctionConfigs().put(TrackModelConstants.DEFAULT_TOGGLE_FUNCTION, new Configuration());
//        }
//        return getFunctionConfigs().get(TrackModelConstants.DEFAULT_TOGGLE_FUNCTION);
        //TODO
        return new Configuration();
    }

    public Configuration getDefaultBlockFunctionConfig() {
//        if (!getFunctionConfigs().containsKey(TrackModelConstants.DEFAULT_BLOCK_FUNCTION)) {
//            getFunctionConfigs().put(TrackModelConstants.DEFAULT_BLOCK_FUNCTION, new Configuration());
//        }
//        return getFunctionConfigs().get(TrackModelConstants.DEFAULT_BLOCK_FUNCTION);
        //TODO
        return new Configuration();
    }

    public String getConfigurationInfo() {
        // title with function configurations
        StringBuilder functionsStringBuilder = new StringBuilder();
        for(TrackPartFunction functionEntry : getFunctionConfigs()) {
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

    public EventConfiguration getEventConfiguration() {
        if (eventConfiguration == null) {
            eventConfiguration = new EventConfiguration();
        }
        return eventConfiguration;
    }

    public void setEventConfiguration(EventConfiguration eventConfiguration) {
        this.eventConfiguration = eventConfiguration;
    }
}
