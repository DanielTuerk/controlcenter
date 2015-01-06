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
     * Default function to toggle a single bit. Used for the block track parts.
     */
    public static final String DEFAULT_TOGGLE_FUNCTION = "toggle";

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
            functionConfigs.put(DEFAULT_TOGGLE_FUNCTION, new Configuration());
        }
        return functionConfigs;
    }

    /**
     * Return the {@link net.wbz.moba.controlcenter.web.shared.track.model.Configuration} of the default toggle
     * function. {@link net.wbz.moba.controlcenter.web.shared.track.model.TrackPart#DEFAULT_TOGGLE_FUNCTION}
     *
     * @return {@link net.wbz.moba.controlcenter.web.shared.track.model.Configuration}
     */
    public Configuration getDefaultToggleFunctionConfig() {
        return getFunctionConfigs().get(DEFAULT_TOGGLE_FUNCTION);
    }

    public String getConfigurationInfo() {
        return String.valueOf(getDefaultToggleFunctionConfig());
    }

    public GridPosition getGridPosition() {
        return gridPosition;
    }

    public void setGridPosition(GridPosition gridPosition) {
        this.gridPosition = gridPosition;
    }

    public EventConfiguration getEventConfiguration() {
        if(eventConfiguration==null) {
            eventConfiguration=new EventConfiguration();
        }
        return eventConfiguration;
    }

    public void setEventConfiguration(EventConfiguration eventConfiguration) {
        this.eventConfiguration = eventConfiguration;
    }
}
