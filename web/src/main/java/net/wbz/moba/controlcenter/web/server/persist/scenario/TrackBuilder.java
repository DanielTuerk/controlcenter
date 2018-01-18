package net.wbz.moba.controlcenter.web.server.persist.scenario;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import net.wbz.moba.controlcenter.web.server.web.editor.TrackManager;
import net.wbz.moba.controlcenter.web.shared.scenario.Route;
import net.wbz.moba.controlcenter.web.shared.scenario.Track;
import net.wbz.moba.controlcenter.web.shared.track.model.AbstractTrackPart;
import net.wbz.moba.controlcenter.web.shared.track.model.BusDataConfiguration;
import net.wbz.moba.controlcenter.web.shared.track.model.GridPosition;
import net.wbz.moba.controlcenter.web.shared.track.model.Switch;
import net.wbz.moba.controlcenter.web.shared.track.model.TrackBlock;

/**
 * TODO doc
 * 
 * @author Daniel Tuerk
 */
@Singleton
public class TrackBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(TrackBuilder.class);
    private static final long TIMEOUT_IN_MILLIS = 5000L;

    private final TrackManager trackManager;
    private final Map<Long, Long> routeStartTimeMillis = Maps.newConcurrentMap();

    @Inject
    public TrackBuilder(TrackManager trackManager) {
        this.trackManager = trackManager;
    }

    /**
     * Build the {@link Track} for the given {@link Route}.
     * 
     * @param route {@link Route}
     * @return {@link Track}
     * @throws TrackNotFoundException
     */
    public Track build(final Route route) throws TrackNotFoundException {
        Preconditions.checkNotNull(route, "no route");
        Preconditions.checkNotNull(route.getStart(), "no route start");
        Preconditions.checkNotNull(route.getEnd(), "no route end");

        LOG.debug("start build track: {} ({})", route.getName(), route.getId());
        routeStartTimeMillis.put(route.getId(), System.currentTimeMillis());
        Map<GridPosition, AbstractTrackPart> positions = new HashMap<>();
        GridPosition startPosition = null;
        GridPosition endPosition = null;

        Collection<AbstractTrackPart> loadedTrack = trackManager.getTrack();
        for (AbstractTrackPart trackPart : loadedTrack) {
            positions.put(trackPart.getGridPosition(), trackPart);
            if (route.getStart().equals(trackPart.getTrackBlock())) {
                startPosition = trackPart.getGridPosition();
            } else if (route.getEnd().equals(trackPart.getTrackBlock())) {
                endPosition = trackPart.getGridPosition();
            }
        }

        Track track = buildTrack(positions, startPosition, endPosition, route.getWaypoints(), route.getId());

        for (int i = 0; i < track.getGridPositions().size(); i++) {
            GridPosition gridPosition = track.getGridPositions().get(i);
            AbstractTrackPart abstractTrackPart = positions.get(gridPosition);

            if ((i > 0 && i < loadedTrack.size() - 1) && abstractTrackPart instanceof Switch) {
                Switch switchTrackPart = (Switch) abstractTrackPart;

                GridPosition nextPosition = track.getGridPositions().get(i + 1);
                GridPosition previousPosition = track.getGridPositions().get(i - 1);
                boolean state = switchTrackPart.getNextGridPositionForStateBranch().equals(nextPosition)
                        || switchTrackPart.getNextGridPositionForStateBranch().equals(previousPosition);

                BusDataConfiguration toggleFunction = switchTrackPart.getToggleFunction();
                if (toggleFunction != null) {
                    track.getTrackFunctions().add(new BusDataConfiguration(toggleFunction.getBus(),
                            toggleFunction.getAddress(), toggleFunction.getBit(), state));
                }
            }
        }
        LOG.debug("finished build track: {} ({}) in {} ms", new Object[] { route.getName(), route.getId(),
                currentExecutionTime(route.getId()) });
        return track;
    }

    private Track buildTrack(Map<GridPosition, AbstractTrackPart> positions, GridPosition startPosition,
            GridPosition endPosition, List<GridPosition> waypoints, Long routeId) throws TrackNotFoundException {
        if (startPosition != null && endPosition != null) {
            AbstractTrackPart abstractTrackPart = positions.get(startPosition);
            LOG.trace("search forward routed tracks");
            Set<Track> forwardRoutedTrack = searchPath(new Track(), Maps.newHashMap(positions), startPosition,
                    endPosition,
                    abstractTrackPart.getNextGridPositions(null), routeId);
            LOG.trace("finished forward routed tracks ({})", forwardRoutedTrack.size());
            LOG.trace("search backward routed tracks");
            Set<Track> backwardRoutedTrack = searchPath(new Track(), Maps.newHashMap(positions), startPosition,
                    endPosition,
                    abstractTrackPart.getLastGridPositions(), routeId);
            LOG.trace("finished backward routed tracks ({})", backwardRoutedTrack.size());

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
        return Sets.newHashSet(Collections2.filter(tracks, new Predicate<Track>() {
            @Override
            public boolean apply(Track input) {
                return waypoints == null || waypoints.isEmpty() || input.getGridPositions().containsAll(waypoints);
            }
        }));
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
            long routeId) throws TrackNotFoundException {
        Set<Track> tracks = Sets.newHashSet();
        if (positions.containsKey(startPosition)) {
            for (GridPosition nextPosition : nextGridPositions) {

                Map<GridPosition, AbstractTrackPart> positionsCopy = Maps
                        .newHashMap(positions);
                // for (GridPosition gridPosition : positionsCopy.keySet()) {
                // gridPosition.setId(null);
                // }

                if (positionsCopy.containsKey(nextPosition)) {
                    AbstractTrackPart trackPartOfNextPos = positionsCopy.get(nextPosition);
                    Collection<GridPosition> lastGridPositions = trackPartOfNextPos.getLastGridPositions();
                    lastGridPositions.addAll(trackPartOfNextPos.getNextGridPositions(startPosition));
                    for (GridPosition lastGridPosition : lastGridPositions) {
                        if (startPosition.equals(lastGridPosition)) {

                            positionsCopy.remove(startPosition);

                            // new track for possible branch
                            Set<Track> apply = apply(new Track(track), positionsCopy, startPosition, endPosition,
                                    nextPosition, routeId);
                            if (apply != null) {
                                tracks.addAll(apply);
                                positionsCopy.remove(nextPosition);
                                break;
                            }
                        }
                    }
                }
            }
        }
        return tracks;
    }

    private Collection<GridPosition> getNextGridPositions(Map<GridPosition, AbstractTrackPart> positions,
            GridPosition startPosition, GridPosition previousPosition) {
        AbstractTrackPart abstractTrackPart = positions.get(startPosition);
        Collection<GridPosition> nextGridPositions = abstractTrackPart.getNextGridPositions(previousPosition);
        nextGridPositions.addAll(abstractTrackPart.getLastGridPositions());
        return nextGridPositions;
    }

    private Set<Track> apply(Track track, Map<GridPosition, AbstractTrackPart> positions, GridPosition startPosition,
            GridPosition endPosition, GridPosition nextPosition, long routeId) throws TrackNotFoundException {

        if (timeoutReached(routeId)) {
            throw new TrackNotFoundException("timeout");
        }

        if (nextPosition.equals(endPosition)) {
            return Sets.newHashSet(track);
        } else if (!positions.containsKey(nextPosition)) {
            return null;
        } else {
            // add grid positions
            track.getGridPositions().add(nextPosition);

            // add blocks
            TrackBlock trackBlock = positions.get(nextPosition).getTrackBlock();
            if (trackBlock != null && trackBlock.getBlockFunction().isValid()) {
                track.getTrackBlocks().add(trackBlock);
            }

            return searchPath(track, positions, nextPosition, endPosition,
                    getNextGridPositions(positions, nextPosition, startPosition), routeId);
        }
    }

    private boolean timeoutReached(long routeId) {
        return !routeStartTimeMillis.containsKey(routeId) || currentExecutionTime(routeId) > TIMEOUT_IN_MILLIS;
    }

    private long currentExecutionTime(long routeId) {
        return System.currentTimeMillis() - routeStartTimeMillis.get(routeId);
    }

    public class TrackNotFoundException extends Exception {

        public TrackNotFoundException() {
            this("no track found!");
        }

        public TrackNotFoundException(String message) {
            super(message);
        }

    }
}
