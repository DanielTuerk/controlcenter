package net.wbz.moba.controlcenter.web.shared.track.model;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Model for a part of the track.
 * - has position in grid
 * - provide functions
 *
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
public class TrackPart implements IsSerializable, Serializable {


    /**
     * Position of the track part in the grid system of the construction.
     */
    private GridPosition gridPosition;

    /**
     * Function mapping for function name and configuration of the function.
     */
    private Map<String, Configuration> functionConfigs;

    /**
     * Configuration to toggle the {@link net.wbz.moba.controlcenter.web.shared.track.model.TrackPart} by an event.
     */
    private EventConfiguration eventConfiguration;

    public Map<String, Configuration> getFunctionConfigs() {
        if (functionConfigs == null) {
            // create dummy for saved instance without configuration; will be changed during first call of
            // {@link TrackPart#setFunctionConfigs}
            functionConfigs = new HashMap<>();
            functionConfigs.put(TrackModelConstants.DEFAULT_TOGGLE_FUNCTION, new Configuration());
            functionConfigs.put(TrackModelConstants.DEFAULT_BLOCK_FUNCTION, new Configuration());
        }
        return functionConfigs;
    }

    /**
     * Return the {@link net.wbz.moba.controlcenter.web.shared.track.model.Configuration} of the default toggle
     * function. {@link net.wbz.moba.controlcenter.web.shared.track.model.TrackModelConstants#DEFAULT_TOGGLE_FUNCTION}
     *
     * @return {@link net.wbz.moba.controlcenter.web.shared.track.model.Configuration}
     */
    public Configuration getDefaultToggleFunctionConfig() {
        if (!getFunctionConfigs().containsKey(TrackModelConstants.DEFAULT_TOGGLE_FUNCTION)) {
            getFunctionConfigs().put(TrackModelConstants.DEFAULT_TOGGLE_FUNCTION, new Configuration());
        }
        return getFunctionConfigs().get(TrackModelConstants.DEFAULT_TOGGLE_FUNCTION);
    }

    public Configuration getDefaultBlockFunctionConfig() {
        if (!getFunctionConfigs().containsKey(TrackModelConstants.DEFAULT_BLOCK_FUNCTION)) {
            getFunctionConfigs().put(TrackModelConstants.DEFAULT_BLOCK_FUNCTION, new Configuration());
        }
        return getFunctionConfigs().get(TrackModelConstants.DEFAULT_BLOCK_FUNCTION);
    }

    public String getConfigurationInfo() {
        // title with function configurations
        StringBuilder functionsStringBuilder = new StringBuilder();
        for(Map.Entry<String,Configuration> functionEntry : getFunctionConfigs().entrySet()) {
            functionsStringBuilder.append(functionEntry.getKey()).append(": ").append(functionEntry.getValue()).append("; ");
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
