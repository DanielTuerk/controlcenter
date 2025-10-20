package net.wbz.moba.controlcenter.service.scenario;

import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import net.wbz.moba.controlcenter.service.track.TrackManager;
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
import org.jboss.logging.Logger;

/**
 * Builder to create a track from a start to an endpoint.
 *
 * @author Daniel Tuerk
 */
@ApplicationScoped
public class TrackBuilder {

    private static final Logger LOG = Logger.getLogger(TrackBuilder.class);

    /**
     * Timeout to build the track.
     */
    private static final long TIMEOUT_IN_MILLIS = 5000L;

    /**
     * Start times for building the track of the route.
     */
    private final Map<Long, Long> routeStartTimeMillis = Maps.newConcurrentMap();

    /**
     * Flag to enable the timeout by {@link #TIMEOUT_IN_MILLIS}.
     */
    private boolean timeoutEnabled = true;

    @Inject
    TrackManager trackManager;

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

        LOG.debugf("start build track: %s (%d)", route.getName(), route.getId());
        routeStartTimeMillis.put(trackBuildId, System.currentTimeMillis());
        Map<GridPosition, AbstractTrackPart> positions = new HashMap<>();
        GridPosition startPosition = null;
        GridPosition endPosition = null;

        final Collection<AbstractTrackPart> loadedTrack = trackManager.getTrack();

        // prepare start, end and all grid positions of the construction
        for (AbstractTrackPart trackPart : loadedTrack) {

            // collect all available grid positions to create the route from point to point
            positions.put(trackPart.getGridPosition(), trackPart);

            if (trackPart instanceof MultipleGridPosition) {
                positions.put(((MultipleGridPosition) trackPart).getEndGridPosition(), trackPart);
            }

            if (trackPart instanceof BlockStraight) {
                BlockStraight blockStraight = (BlockStraight) trackPart;
                if (isOneTrackBlockTheSame(route.getStart(), blockStraight)) {
                    startPosition = trackPart.getGridPosition();
                } else if (isBlockPresent(route.getEnd(), blockStraight)) {
                    endPosition = trackPart.getGridPosition();
                }
            }
        }

        // build the track from start to end
        Track track = buildTrack(positions, startPosition, endPosition, route.getWaypoints(), trackBuildId);

        for (int i = 0; i < track.getGridPositions().size(); i++) {
            GridPosition gridPosition = track.getGridPositions().get(i);
            AbstractTrackPart abstractTrackPart = positions.get(gridPosition);

            if ((i > 0 && i < loadedTrack.size() - 1)
                && abstractTrackPart instanceof Turnout) {

                Turnout turnoutTrackPart = (Turnout) abstractTrackPart;

                GridPosition nextPosition = track.getGridPositions().get(i + 1);
                GridPosition previousPosition = track.getGridPositions().get(i - 1);
                boolean state =
                    turnoutTrackPart.getNextGridPositionForStateBranch().equals(nextPosition)
                        || turnoutTrackPart.getNextGridPositionForStateBranch().equals(previousPosition);

                BusDataConfiguration toggleFunction = turnoutTrackPart.getToggleFunction();
                if (toggleFunction != null) {
                    track.getTrackFunctions()
                        .add(new BusDataConfiguration(toggleFunction.getBus(), toggleFunction.getAddress(),
                            toggleFunction.getBit(), state));
                }
            }
        }
        LOG.debugf("finished build track: %s (%d) in %d ms", route.getName(), route.getId(),
            currentExecutionTime(trackBuildId));
        return track;
    }

    /**
     * State for the active timout to build a {@link Track}.
     *
     * @return {@code false} if disabled
     */
    public boolean isTimeoutEnabled() {
        return timeoutEnabled;
    }

    /**
     * Enable or disable the timeout to build a {@link Track}
     *
     * @param timeoutEnabled {@code false} to disable
     */
    public void setTimeoutEnabled(boolean timeoutEnabled) {
        this.timeoutEnabled = timeoutEnabled;
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
        if (startPosition != null && endPosition != null) {
            AbstractTrackPart abstractTrackPart = positions.get(startPosition);
            LOG.trace("search forward routed tracks");
            Set<Track> forwardRoutedTrack = searchPath(new Track(), Maps.newHashMap(positions), startPosition,
                endPosition, abstractTrackPart.getNextGridPositions(null), trackBuildId);
            LOG.tracef("finished forward routed tracks (%d)", forwardRoutedTrack.size());
            LOG.trace("search backward routed tracks");
            Set<Track> backwardRoutedTrack = searchPath(new Track(), Maps.newHashMap(positions), startPosition,
                endPosition, abstractTrackPart.getLastGridPositions(), trackBuildId);
            LOG.tracef("finished backward routed tracks (%d)", backwardRoutedTrack.size());

            LOG.trace("search shortest track");
            Track track = shortestTrack(forwardRoutedTrack, backwardRoutedTrack, waypoints);
            LOG.trace("finished shortest track");

            List<GridPosition> gridPositions = Lists.newArrayList(startPosition);
            gridPositions.addAll(track.getGridPositions());
            gridPositions.add(endPosition);
            track.getGridPositions().clear();
            track.getGridPositions().addAll(gridPositions);
            return track;
        }
        throw new TrackNotFoundException("no start or end");
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
        return Sets.newHashSet(Collections2.filter(tracks,
            input -> waypoints == null || waypoints.isEmpty() || Objects.requireNonNull(input).getGridPositions()
                .containsAll(waypoints)));
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
        Set<Track> tracks = Sets.newHashSet();
        if (positions.containsKey(startPosition)) {
            for (GridPosition nextPosition : nextGridPositions) {
                Map<GridPosition, AbstractTrackPart> positionsCopy = Maps.newHashMap(positions);
                if (positionsCopy.containsKey(nextPosition)) {
                    AbstractTrackPart trackPartOfNextPos = positionsCopy.get(nextPosition);
                    Collection<GridPosition> lastGridPositions = trackPartOfNextPos.getLastGridPositions();
                    lastGridPositions.addAll(trackPartOfNextPos.getNextGridPositions(startPosition));
                    for (GridPosition lastGridPosition : lastGridPositions) {
                        if (checkLastPositionIsStartPosition(positions, startPosition, lastGridPosition)) {
                            LOG.trace("apply pos: " + startPosition);
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
        Collection<GridPosition> nextGridPositions = abstractTrackPart.getNextGridPositions(previousPosition);
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
            return Sets.newHashSet(track);
        } else if (!positions.containsKey(nextPosition)) {
            return null;
        } else {
            AbstractTrackPart abstractTrackPart = positions.get(nextPosition);
            // add blocks
            if (abstractTrackPart instanceof BlockStraight) {
                BlockStraight blockStraight = (BlockStraight) abstractTrackPart;
                getValidTrackBlock(blockStraight.getLeftTrackBlock()).ifPresent(x -> track.getTrackBlocks().add(x));
                getValidTrackBlock(blockStraight.getMiddleTrackBlock()).ifPresent(x -> track.getTrackBlocks().add(x));
                getValidTrackBlock(blockStraight.getRightTrackBlock()).ifPresent(x -> track.getTrackBlocks().add(x));
            }

            // add grid positions
            track.getGridPositions().add(nextPosition);

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
