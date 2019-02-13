package net.wbz.moba.controlcenter.web.shared.track.model;

/**
 * Mark {@link AbstractTrackPart} that have more than one single {@link GridPosition}. Multiple positions have a start
 * {@link GridPosition} a length and a end {@link GridPosition}.
 *
 * @author Daniel Tuerk
 */
public interface MultipleGridPosition {

    /**
     * The end grid position from start.
     *
     * @return {@link GridPosition}
     */
    GridPosition getEndGridPosition();

    /**
     * Length for the {@link AbstractTrackPart}. The value {@code 1} displays only as a single {@link GridPosition}.
     *
     * @return number of {@link GridPosition}s as length
     */
    int getGridLength();
}
