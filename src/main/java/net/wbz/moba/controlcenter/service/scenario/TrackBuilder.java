package net.wbz.moba.controlcenter.service.scenario;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.wbz.moba.controlcenter.service.track.TrackProvider;
import net.wbz.moba.controlcenter.shared.scenario.Route;
import net.wbz.moba.controlcenter.shared.scenario.Track;
import net.wbz.moba.controlcenter.shared.scenario.TrackNotFoundException;
import net.wbz.moba.controlcenter.shared.track.model.AbstractTrackPart;
import net.wbz.moba.controlcenter.shared.track.model.BlockStraight;
import net.wbz.moba.controlcenter.shared.track.model.BusDataConfiguration;
import net.wbz.moba.controlcenter.shared.track.model.GridPosition;
import net.wbz.moba.controlcenter.shared.track.model.MultipleGridPosition;
import net.wbz.moba.controlcenter.shared.track.model.TrackBlock;
import net.wbz.moba.controlcenter.shared.track.model.Turnout;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Builder to create a track from a start to an endpoint.
 *
 * @author Daniel Tuerk
 */
@Slf4j
@ApplicationScoped
public class TrackBuilder {


    /**
     * Timeout to build the track.
     */
    private static final long TIMEOUT_IN_MILLIS = 5000L;

    /**
     * Start times for building the track of the route.
     */
    private final Map<Long, Long> routeStartTimeMillis = new ConcurrentHashMap<>();

    @Setter
    @Getter
    private boolean timeoutEnabled = true;

    @Inject
    TrackProvider trackProvider;

    private static Optional<TrackBlock> getValidTrackBlock(TrackBlock trackBlock) {
        if (trackBlock != null && trackBlock.getBlockFunction().isValid()) {
            return Optional.of(trackBlock);
        } else {
            return Optional.empty();
        }
    }

    /**
     * Build the {@link Track} for the given {@link Route}.
     *
     * @param route {@link Route}
     * @return {@link Track}
     * @throws TrackNotFoundException no track
     */
    public Track build(final Route route) throws TrackNotFoundException {
        if (route == null) {
            throw new TrackNotFoundException("no route given");
        }
        if (route.getStart() == null) {
            throw new TrackNotFoundException("no route start defined");
        } else {
            for (TrackBlock trackBlock : route.getStart().getAllTrackBlocks()) {
                if (trackBlock.getBlockFunction() == null || !trackBlock.getBlockFunction().isValid()) {
                    throw new TrackNotFoundException(
                        "invalid start: invalid block function: " + trackBlock.getBlockFunction());
                }
            }
        }
        if (route.getEnd() == null) {
            throw new TrackNotFoundException("no route end defined");
        } else if (route.getEnd().getBlockFunction() == null || !route.getEnd().getBlockFunction().isValid()) {
            throw new TrackNotFoundException(
                "invalid end: invalid block function: " + route.getEnd().getBlockFunction());
        }

        final long trackBuildId = System.nanoTime();

        log.debug("start build track: {} ({})", route.getName(), route.getId());
        routeStartTimeMillis.put(trackBuildId, System.currentTimeMillis());
        Map<GridPosition, AbstractTrackPart> positions = new HashMap<>();
        GridPosition startPosition = null;
        GridPosition endPosition = null;

        final Collection<AbstractTrackPart> loadedTrack = trackProvider.getTrack();

        // prepare start, end and all grid positions of the construction
        for (AbstractTrackPart trackPart : loadedTrack) {

            // collect all available grid positions to create the route from point to point
            positions.put(trackPart.getGridPosition(), trackPart);

            if (trackPart instanceof MultipleGridPosition) {
                positions.put(((MultipleGridPosition) trackPart).getEndGridPosition(), trackPart);
            }

            if (trackPart instanceof final BlockStraight blockStraight) {
                if (isOneTrackBlockTheSame(route.getStart(), blockStraight)) {
                    startPosition = trackPart.getGridPosition();
                } else if (isBlockPresent(route.getEnd(), blockStraight)) {
                    endPosition = trackPart.getGridPosition();
                }
            }
        }

        // build the track from start to end
        Track track = buildTrack(positions, startPosition, endPosition, route.getWaypoints(), trackBuildId);

        for (int i = 0; i < track.gridPositions().size(); i++) {
            GridPosition gridPosition = track.gridPositions().get(i);
            AbstractTrackPart abstractTrackPart = positions.get(gridPosition);

            if ((i > 0 && i < loadedTrack.size() - 1)
                && abstractTrackPart instanceof Turnout turnoutTrackPart) {

                var nextPosition = track.gridPositions().get(i + 1);
                var previousPosition = track.gridPositions().get(i - 1);
                boolean state =
                    turnoutTrackPart.getNextGridPositionForStateBranch().equals(nextPosition)
                        || turnoutTrackPart.getNextGridPositionForStateBranch().equals(previousPosition);

                var toggleFunction = turnoutTrackPart.getToggleFunction();
                if (toggleFunction != null) {
                    track.trackFunctions()
                        .add(new BusDataConfiguration(toggleFunction.getBus(), toggleFunction.getAddress(),
                            toggleFunction.getBit(), state));
                }
            }
        }
        log.debug("finished build track: {} ({}) in {} ms", route.getName(), route.getId(),
            currentExecutionTime(trackBuildId));
        return track;
    }

    /**
     * Check that at least one {@link TrackBlock} for both {@link BlockStraight}s is the same one.
     *
     * @param left {@link BlockStraight}
     * @param right {@link BlockStraight}
     * @return {@code true} if one {@link TrackBlock} is present in both {@link BlockStraight}s
     */
    private boolean isOneTrackBlockTheSame(BlockStraight left, BlockStraight right) {
        return left.getAllTrackBlocks().stream().anyMatch(x -> isBlockPresent(x, right));
    }

    private Track buildTrack(Map<GridPosition, AbstractTrackPart> positions, GridPosition startPosition,
        GridPosition endPosition, List<GridPosition> waypoints, Long trackBuildId) throws TrackNotFoundException {
        if (startPosition == null) {
            throw new TrackNotFoundException("no start grid pos");
        }
        if (endPosition == null) {
            throw new TrackNotFoundException("no end grid pos");
        }
        var abstractTrackPart = positions.get(startPosition);
        log.trace("search forward routed tracks");
        Set<Track> forwardRoutedTrack = searchPath(new Track(), new HashMap<>(positions), startPosition,
            endPosition, abstractTrackPart.getNextGridPositions(null), trackBuildId);
        log.trace("finished forward routed tracks ({})", forwardRoutedTrack.size());
        log.trace("search backward routed tracks");
        Set<Track> backwardRoutedTrack = searchPath(new Track(), new HashMap<>(positions), startPosition,
            endPosition, abstractTrackPart.getLastGridPositions(), trackBuildId);
        log.trace("finished backward routed tracks ({})", backwardRoutedTrack.size());

        log.trace("search shortest track");
        Track track = shortestTrack(forwardRoutedTrack, backwardRoutedTrack, waypoints);
        log.trace("finished shortest track");

        List<GridPosition> gridPositions = new ArrayList<>(List.of(startPosition));
        gridPositions.addAll(track.gridPositions());
        gridPositions.add(endPosition);
        track.gridPositions().clear();
        track.gridPositions().addAll(gridPositions);
        return track;
    }

    /**
     * Check for the shortest track of the given ones. All tracks will first filter by the waypoints.
     *
     * @param forwardRoutedTracks tracks for the forward direction
     * @param backwardRoutedTracks tracks for the backward direction
     * @param waypoints {@link List} of {@link GridPosition}s for the waypoint on the {@link Route}.
     * @return the shortest {@link Track} of the given ones
     */
    private Track shortestTrack(Set<Track> forwardRoutedTracks, Set<Track> backwardRoutedTracks,
        final List<GridPosition> waypoints) throws TrackNotFoundException {
        // waypoints
        forwardRoutedTracks = filterByWaypoints(forwardRoutedTracks, waypoints);
        backwardRoutedTracks = filterByWaypoints(backwardRoutedTracks, waypoints);

        // shortest
        Track forwardRoutedTrack = findShortestTrack(forwardRoutedTracks);
        Track backwardRoutedTrack = findShortestTrack(backwardRoutedTracks);

        // shortest of forward and backward
        if (forwardRoutedTrack != null) {
            if (backwardRoutedTrack != null) {
                if (forwardRoutedTrack.getLength() < backwardRoutedTrack.getLength()) {
                    return forwardRoutedTrack;
                } else {
                    return backwardRoutedTrack;
                }
            } else {
                return forwardRoutedTrack;
            }

        } else if (backwardRoutedTrack != null) {
            return backwardRoutedTrack;
        }
        throw new TrackNotFoundException();
    }

    private Set<Track> filterByWaypoints(Collection<Track> tracks, final Collection<GridPosition> waypoints) {
        return tracks.stream()
                .filter(input -> waypoints == null
                        || waypoints.isEmpty()
                        || new HashSet<>(Objects.requireNonNull(input).gridPositions()).containsAll(waypoints))
                .collect(Collectors.toSet());
    }

    private Track findShortestTrack(Set<Track> forwardRoutedTracks) {
        Track shortTrack = null;
        for (Track track : forwardRoutedTracks) {
            if (shortTrack == null || shortTrack.getLength() > track.getLength()) {
                shortTrack = track;
            }
        }
        return shortTrack;
    }

    private Set<Track> searchPath(Track track, Map<GridPosition, AbstractTrackPart> positions,
        GridPosition startPosition, GridPosition endPosition, Collection<GridPosition> nextGridPositions,
        long trackBuildId) throws TrackNotFoundException {
        Set<Track> tracks = new HashSet<>();
        if (positions.containsKey(startPosition)) {
            for (GridPosition nextPosition : nextGridPositions) {
                Map<GridPosition, AbstractTrackPart> positionsCopy = new HashMap<>(Map.copyOf(positions));
                if (positionsCopy.containsKey(nextPosition)) {
                    AbstractTrackPart trackPartOfNextPos = positionsCopy.get(nextPosition);
                    Collection<GridPosition> lastGridPositions = new ArrayList<>(trackPartOfNextPos.getLastGridPositions());
                    lastGridPositions.addAll(trackPartOfNextPos.getNextGridPositions(startPosition));
                    for (GridPosition lastGridPosition : lastGridPositions) {
                        if (checkLastPositionIsStartPosition(positions, startPosition, lastGridPosition)) {
                            log.trace("apply pos: {}", startPosition);
                            positionsCopy.remove(startPosition);

                            // next pos of track or replace the fake pos from the block length with the original one
                            GridPosition nextPositionCopy;
                            AbstractTrackPart abstractTrackPart = positions.get(nextPosition);
                            if (abstractTrackPart instanceof BlockStraight) {
                                nextPositionCopy = abstractTrackPart.getGridPosition();
                            } else {
                                nextPositionCopy = nextPosition;
                            }

                            // new track for possible branch
                            Set<Track> apply = apply(new Track(track), positionsCopy, startPosition, endPosition,
                                nextPositionCopy, trackBuildId);
                            if (apply != null) {
                                tracks.addAll(apply);

                                positionsCopy.remove(nextPosition);
                                positionsCopy.remove(nextPositionCopy);
                                break;
                            }
                        }
                    }
                }
            }
        }
        return tracks;
    }

    private boolean checkLastPositionIsStartPosition(
        Map<GridPosition, AbstractTrackPart> positions,
        GridPosition startPosition, GridPosition lastGridPosition) {

        AbstractTrackPart trackPartOfStartPos = positions.get(startPosition);
        if (trackPartOfStartPos instanceof MultipleGridPosition) {
            GridPosition endGridPosition = ((MultipleGridPosition) trackPartOfStartPos).getEndGridPosition();
            return endGridPosition.equals(lastGridPosition)
                || trackPartOfStartPos.getGridPosition().equals(startPosition);
        } else {
            return startPosition.equals(lastGridPosition);
        }
    }

    private Collection<GridPosition> getNextGridPositions(Map<GridPosition, AbstractTrackPart> positions,
        GridPosition startPosition, GridPosition previousPosition) {
        AbstractTrackPart abstractTrackPart = positions.get(startPosition);
        Collection<GridPosition> nextGridPositions = new ArrayList<>(abstractTrackPart.getNextGridPositions(previousPosition));
        nextGridPositions.addAll(abstractTrackPart.getLastGridPositions());
        return nextGridPositions;
    }

    private boolean isBlockPresent(TrackBlock trackBlock, BlockStraight blockStraight) {
        return trackBlock.equals(blockStraight.getLeftTrackBlock())
            || trackBlock.equals(blockStraight.getMiddleTrackBlock())
            || trackBlock.equals(blockStraight.getRightTrackBlock());
    }

    private Set<Track> apply(Track track, Map<GridPosition, AbstractTrackPart> positions, GridPosition startPosition,
        GridPosition endPosition, GridPosition nextPosition, long trackBuildId) throws TrackNotFoundException {

        if (timeoutReached(trackBuildId)) {
            throw new TrackNotFoundException("timeout");
        }

        if (nextPosition.equals(endPosition)) {
            return Set.of(track);
        } else if (!positions.containsKey(nextPosition)) {
            return null;
        } else {
            AbstractTrackPart abstractTrackPart = positions.get(nextPosition);
            // add blocks
            if (abstractTrackPart instanceof BlockStraight blockStraight) {
                getValidTrackBlock(blockStraight.getLeftTrackBlock()).ifPresent(x -> track.trackBlocks().add(x));
                getValidTrackBlock(blockStraight.getMiddleTrackBlock()).ifPresent(x -> track.trackBlocks().add(x));
                getValidTrackBlock(blockStraight.getRightTrackBlock()).ifPresent(x -> track.trackBlocks().add(x));
            }

            // add grid positions
            track.gridPositions().add(nextPosition);

            return searchPath(track, positions, nextPosition, endPosition,
                getNextGridPositions(positions, nextPosition, startPosition), trackBuildId);
        }
    }

    private boolean timeoutReached(long trackBuildId) {
        return !routeStartTimeMillis.containsKey(trackBuildId)
            || (timeoutEnabled && currentExecutionTime(trackBuildId) > TIMEOUT_IN_MILLIS);
    }

    private long currentExecutionTime(long trackBuildId) {
        return System.currentTimeMillis() - routeStartTimeMillis.get(trackBuildId);
    }

}
