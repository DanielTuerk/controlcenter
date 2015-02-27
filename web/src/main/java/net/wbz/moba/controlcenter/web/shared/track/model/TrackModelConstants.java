package net.wbz.moba.controlcenter.web.shared.track.model;

/**
 * Class to hold all constants to avoid null pointer by old serialized
 * {@link net.wbz.moba.controlcenter.web.shared.track.model.TrackPart}s which didn't contain newer constants.
 *
 * @author Daniel Tuerk
 */
public class TrackModelConstants {

    /**
     * Default function to toggle a single bit. Used for the block track parts.
     */
    public static final String DEFAULT_TOGGLE_FUNCTION = "toggle";

    /**
     * Default function to receive the block state of the track part.
     */
    public static final String DEFAULT_BLOCK_FUNCTION = "block";
}
