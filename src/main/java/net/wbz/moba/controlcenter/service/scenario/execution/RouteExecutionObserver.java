package net.wbz.moba.controlcenter.service.scenario.execution;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.wbz.moba.controlcenter.BusAddressIdentifier;
import net.wbz.moba.controlcenter.SelectrixHelper;
import net.wbz.moba.controlcenter.service.track.TrackProvider;
import net.wbz.moba.controlcenter.shared.scenario.Route;
import net.wbz.moba.controlcenter.shared.scenario.Route.ROUTE_RUN_STATE;
import net.wbz.moba.controlcenter.shared.scenario.RouteSequence;
import net.wbz.moba.controlcenter.shared.track.model.BlockStraight;
import net.wbz.moba.controlcenter.shared.track.model.BusDataConfiguration;
import net.wbz.moba.controlcenter.shared.track.model.TrackBlock;
import net.wbz.selectrix4java.device.DeviceAccessException;
import net.wbz.selectrix4java.device.DeviceManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Observer for the running routes in a {@link ScenarioExecution}. The observer hold all running routes and must be used
 * to check for a free track and to reserve a free track. Route dependencies on the track are detected by the observer
 * and will be checked for the reservations of routes.
 *
 * @author Daniel Tuerk
 */
@Slf4j
@ApplicationScoped
public class RouteExecutionObserver {


    private final TrackProvider trackProvider;
    /**
     * Actual running {@link RouteSequence}s which need to be thread safe. Each execution will access the collection in
     * parallel.
     */
    private final Collection<RouteSequence> runningRouteSequences = Collections.synchronizedSet(new HashSet<>());
    private final DeviceManager deviceManager;

    @Inject
    public RouteExecutionObserver(TrackProvider trackProvider, DeviceManager deviceManager) {
        this.trackProvider = trackProvider;
        this.deviceManager = deviceManager;
    }

    /**
     * Synchronized to guarantee a single reserve for a route execution.
     *
     * @param routeSequence {@link RouteSequence} to check and to reserve run
     * @param previousRouteSequence the previous executed {@link RouteSequence}
     * @return {@code true} if the track was free and the given route is reserved.
     */
    synchronized boolean checkAndReserveNextRunningRoute(RouteSequence routeSequence,
        RouteSequence previousRouteSequence) {
        Route route = routeSequence.getRoute();
        Collection<RouteSequence> dependingRunningRoutes = getDependingRunningRoutes(route);

        // previous given one is running, which will be now removed to start next route in the sequence
        dependingRunningRoutes.remove(previousRouteSequence);

        // all are prepared, than start with one by setting state RESERVED to avoid dead lock of routes
        boolean allDependingRoutesHaveStatePrepared = dependingRunningRoutes.stream()
            .allMatch(x -> x.getRoute().getRunState() == ROUTE_RUN_STATE.PREPARED);

        // Check that no depending route is running
        if (dependingRunningRoutes.isEmpty() || allDependingRoutesHaveStatePrepared) {

            // only set to reserve if the next route blocks are free, also without dependent route
            if (allBlocksAreFree(route.getAllTrackBlocksToDrive())) {
                route.setRunState(ROUTE_RUN_STATE.RESERVED);
                return true;
            } else {
                /*
                 * No depending route is running but still blocks occupied to drive.
                 * From manual drive or non started route.
                 */
                return false;
            }
        }
        return false;
    }

    /**
     * Check that all given {@link TrackBlock}s are free.
     *
     * @param trackBlocks {@link TrackBlock}s to check
     * @return {@code true} if all given blocks are free
     */
    private boolean allBlocksAreFree(Set<TrackBlock> trackBlocks) {
        try {
            final var device$ = deviceManager.getConnectedDevice();
            if (device$.isEmpty()) {
                log.error("can't check blocks to reserve track, no connected device");
                return false;
            }
            for (TrackBlock trackBlock : trackBlocks) {
                BusDataConfiguration blockFunction = trackBlock.getBlockFunction();
                BusAddressIdentifier entry = new BusAddressIdentifier(blockFunction);
                if (SelectrixHelper.getFeedbackBlockModule(device$.get(), entry)
                    .getLastReceivedBlockState(blockFunction.getBit())) {
                    return false;
                }
            }
        } catch (DeviceAccessException e) {
            log.error("can't check blocks to reserve track", e);
            return false;
        }
        return true;
    }

    void addRunningRouteSequence(RouteSequence routeSequence) {
        log.debug("addRunningRouteSequence: {}", routeSequence);
        runningRouteSequences.add(routeSequence);
    }

    void removeRunningRouteSequence(RouteSequence routeSequence) {
        log.debug("removeRunningRouteSequence: {}", routeSequence);
        runningRouteSequences.remove(routeSequence);
    }

    /**
     * Return the actual running {@link RouteSequence}s which depends by the {@link TrackBlock}s and {@link
     * BusDataConfiguration}s of track parts on the given {@link Route}
     *
     * @param route {@link Route} to check
     * @return depending running {@link RouteSequence}s
     */
    private Collection<RouteSequence> getDependingRunningRoutes(final Route route) {
        return runningRouteSequences.stream().filter(input -> !route.equals(input.getRoute())
            // add as dependent if routes have the same track block or function
            && (containsSameTrackBlock(route, input.getRoute()) || containsSameTrackFunctions(input.getRoute(),
                route))).collect(Collectors.toSet());
    }

    /**
     * Check for same {@link TrackBlock}s of given routes.
     *
     * @param routeToStart {@link Route}
     * @param other {@link Route}
     * @return {@code true} if a single {@link TrackBlock} is the same in both routes
     */
    private boolean containsSameTrackBlock(Route routeToStart, Route other) {
        // collect all track blocks of left route
        List<TrackBlock> runningTrackBlocks = new ArrayList<>();
        if (routeToStart.getTrack() != null) {
            runningTrackBlocks.addAll(routeToStart.getTrack().trackBlocks());
        }
        runningTrackBlocks.addAll(getTrackBlocksForBlockStraightsOfTrackBlock(routeToStart.getEnd()));

        // check for same track blocks from left route in right route
        if (runningTrackBlocks.stream().anyMatch(x -> other.getStart().getAllTrackBlocks().contains(x))
            || runningTrackBlocks.stream()
            .anyMatch(x -> getTrackBlocksForBlockStraightsOfTrackBlock(other.getEnd()).contains(x))) {
            return true;
        }
        for (TrackBlock blockToCheck : other.getTrack().trackBlocks()) {
            if (runningTrackBlocks.contains(blockToCheck)) {
                return true;
            }
        }
        return false;
    }

    private Set<TrackBlock> getTrackBlocksForBlockStraightsOfTrackBlock(TrackBlock left) {
        return trackProvider.getTrack().stream()
            .filter(x -> x instanceof BlockStraight)
            .map(BlockStraight.class::cast)
            .map(BlockStraight::getAllTrackBlocks)
            .filter(allTrackBlocks -> allTrackBlocks.contains(left))
            .flatMap(Collection::stream)
            .collect(Collectors.toSet());
    }

    /**
     * Check for same track functions (like switch) on track of given routes. The track functions is only checked for
     * bus, address and bit. The bit state doesn't depend on detect a single switch which is used in both routes, also
     * for different state.
     *
     * @param left {@link Route}
     * @param right {@link Route}
     * @return {@code true} if a single track function {@link BusDataConfiguration} is the same in both routes
     */
    private boolean containsSameTrackFunctions(Route left, Route right) {
        for (BusDataConfiguration trackFunction : right.getTrack().trackFunctions()) {
            if (trackFunction != null) {
                for (BusDataConfiguration busDataConfiguration : left.getTrack().trackFunctions()) {
                    if (busDataConfiguration != null && busDataConfiguration.isSameConfig(trackFunction)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
