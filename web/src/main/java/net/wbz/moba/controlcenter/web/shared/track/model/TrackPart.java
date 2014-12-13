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

    private GridPosition gridPosition;

    /**
     * Function mapping for function name and configuration of the function.
     */
    private Map<String, Configuration> functionConfigs;
//    private Configuration configuration;
//    private boolean initialState;

    public Map<String, Configuration> getFunctionConfigs() {
//        if(functionConfigs==null){
//            functionConfigs=new HashMap<>();
//            functionConfigs.put(DEFAULT_TOGGLE_FUNCTION, new Configuration());
//        }
        return functionConfigs;
    }

    public Configuration getDefaultToggleFunctionConfig() {
        return getFunctionConfigs().get(DEFAULT_TOGGLE_FUNCTION);
    }

//    public Configuration getConfiguration() {
//        return configuration;
//    }

//    public void setConfiguration(Configuration configuration) {
//        this.configuration = configuration;
//    }

    public GridPosition getGridPosition() {
        return gridPosition;
    }

    public void setGridPosition(GridPosition gridPosition) {
        this.gridPosition = gridPosition;
    }

    public void setFunctionConfigs(Map<String, Configuration> functionConfigs) {
        this.functionConfigs = functionConfigs;
    }

//    public boolean isInitialState() {
//        return initialState;
//    }
//
//    public void setInitialState(boolean initialState) {
//        this.initialState = initialState;
//    }
}
