package net.wbz.moba.controlcenter.web.shared.track.model;

import com.googlecode.jmapper.annotations.JMap;

/**
 * @author Daniel Tuerk
 */
public class TrackPartFunction extends AbstractDto {

    /**
     * Default function to toggle a single bit. Used for the block track parts.
     */
    public static final String DEFAULT_TOGGLE_FUNCTION = "toggle";

    /**
     * Default function to receive the block state of the track part.
     */
    public static final String DEFAULT_BLOCK_FUNCTION = "block";

    @JMap
    private String functionKey;

    @JMap
    private BusDataConfiguration configuration;

    public String getFunctionKey() {
        return functionKey;
    }

    public void setFunctionKey(String functionKey) {
        this.functionKey = functionKey;
    }

    public BusDataConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(BusDataConfiguration configuration) {
        this.configuration = configuration;
    }
}
