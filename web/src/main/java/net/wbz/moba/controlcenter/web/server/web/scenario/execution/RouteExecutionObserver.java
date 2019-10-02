package net.wbz.moba.controlcenter.web.server.web.scenario.execution;

import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import net.wbz.moba.controlcenter.web.server.SelectrixHelper;
import net.wbz.moba.controlcenter.web.server.web.editor.TrackManager;
import net.wbz.moba.controlcenter.web.server.web.editor.block.BusAddressIdentifier;
import net.wbz.moba.controlcenter.web.shared.scenario.Route;
import net.wbz.moba.controlcenter.web.shared.scenario.Route.ROUTE_RUN_STATE;
import net.wbz.moba.controlcenter.web.shared.scenario.RouteSequence;
import net.wbz.moba.controlcenter.web.shared.track.model.BlockStraight;
import net.wbz.moba.controlcenter.web.shared.track.model.BusDataConfiguration;
import net.wbz.moba.controlcenter.web.shared.track.model.TrackBlock;
import net.wbz.selectrix4java.device.Device;
import net.wbz.selectrix4java.device.DeviceAccessException;
import net.wbz.selectrix4java.device.DeviceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Observer for the running routes in a {@link ScenarioExecution}. The observer hold all running routes and must be used
 * to check for a free track and to reserve a free track. Route dependencies on the track are detected by the observer
 * and will be checked for the reservations of routes.
 *
 * @author Daniel Tuerk
 */
@Singleton
public class RouteExecutionObserver {

    private static final Logger LOG = LoggerFactory.getLogger(RouteExecutionObserver.class);

    private final TrackManager trackManager;
    /**
     * Actual running {@link RouteSequence}s which need to be thread safe. Each execution will access the collection in
     * parallel.
     */
    private final Collection<RouteSequence> runningRouteSequences = Collections.synchronizedSet(new HashSet<>());
    private final DeviceManager deviceManager;

    @Inject
    public RouteExecutionObserver(TrackManager trackManager, DeviceManager deviceManager) {
        this.trackManager = trackManager;
        this.deviceManager = deviceManager;
    }

    void removeRunningRouteSequence(RouteSequence routeSequence) {
        LOG.debug("removeRunningRouteSequence: {}", routeSequence);
        runningRouteSequences.remove(routeSequence);
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
        /*
         * Check that no depending route is running, or only the previous given one is running, which will be now
         * removed to start next route in the sequence.
         */
        if (dependingRunningRoutes.isEmpty() || (dependingRunningRoutes.size() == 1 && dependingRunningRoutes
            .contains(previousRouteSequence))) {

            // only set to reserve if the next route blocks are free, also without dependent route
            if (allBlocksAreFree(route.getAllTrackBlocksToDrive())) {
                route.setRunState(ROUTE_RUN_STATE.RESERVED);
                synchronized (this) {
                    addRunningRouteSequence(routeSequence);
                    removeRunningRouteSequence(previousRouteSequence);
                }
            }
            return true;
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
            Device device = deviceManager.getConnectedDevice();
            for (TrackBlock trackBlock : trackBlocks) {
                BusDataConfiguration blockFunction = trackBlock.getBlockFunction();
                BusAddressIdentifier entry = new BusAddressIdentifier(blockFunction);
                if (SelectrixHelper.getFeedbackBlockModule(device, entry)
                    .getLastReceivedBlockState(blockFunction.getBit())) {
                    return false;
                }
            }
        } catch (DeviceAccessException e) {
            LOG.error("can't check blocks to reserve track", e);
            return false;
        }
        return true;
    }
    private void addRunningRouteSequence(RouteSequence routeSequence) {
        LOG.debug("addRunningRouteSequence: {}", routeSequence);
        runningRouteSequences.add(routeSequence);
    }

    /**
     * Return the actual running {@link RouteSequence}s which depends by the {@link TrackBlock}s and {@link
     * BusDataConfiguration}s of track parts on the given {@link Route}
     *
     * @param route {@link Route} to check
     * @return depending running {@link RouteSequence}s
     */
    private Collection<RouteSequence> getDependingRunningRoutes(final Route route) {
        List<RouteSequence> unfiltered = Lists.newArrayList(runningRouteSequences);
        return Collections2.filter(unfiltered, input -> !route.equals(input.getRoute())
            // add as dependent if routes have the same track block or function
            && (containsSameTrackBlock(input.getRoute(), route) || containsSameTrackFunctions(input.getRoute(),
            route)));
    }

    /**
     * Check for same {@link TrackBlock}s of given routes.
     *
     * @param left {@link Route}
     * @param right {@link Route}
     * @return {@code true} if a single {@link TrackBlock} is the same in both routes
     */
    private boolean containsSameTrackBlock(Route left, Route right) {
        // collect all track blocks of left route
        List<TrackBlock> runningTrackBlocks = new ArrayList<>(left.getStart().getAllTrackBlocks());
        if (left.getTrack() != null) {
            runningTrackBlocks.addAll(left.getTrack().getTrackBlocks());
        }
        runningTrackBlocks.addAll(getTrackBlocksForBlockStraightsOfTrackBlock(left.getEnd()));

        // check for same track blocks from left route in right route
        if (runningTrackBlocks.stream().anyMatch(x -> right.getStart().getAllTrackBlocks().contains(x))
            || runningTrackBlocks.stream()
            .anyMatch(x -> getTrackBlocksForBlockStraightsOfTrackBlock(right.getEnd()).contains(x))) {
            return true;
        }
        for (TrackBlock blockToCheck : right.getTrack().getTrackBlocks()) {
            if (runningTrackBlocks.contains(blockToCheck)) {
                return true;
            }
        }
        return false;
    }

    private Set<TrackBlock> getTrackBlocksForBlockStraightsOfTrackBlock(TrackBlock left) {
        return trackManager.getBlockStraightsFromTrackBlock(left)
            .stream()
            .map(BlockStraight::getAllTrackBlocks)
            .flatMap(Collection::stream)
            .collect(Collectors.toSet());
    }

    /**
     * Check for same track functions (like switch) on track of given routes. The track functions is only checked for
     * bus, address and bit. The bit state doesn't depend to detect a signle switch which is used in both routes, also
     * for different state.
     *
     * @param left {@link Route}
     * @param right {@link Route}
     * @return {@code true} if a single track function {@link BusDataConfiguration} is the same in both routes
     */
    private boolean containsSameTrackFunctions(Route left, Route right) {
        for (BusDataConfiguration trackFunction : right.getTrack().getTrackFunctions()) {
            if (trackFunction != null) {
                for (BusDataConfiguration busDataConfiguration : left.getTrack().getTrackFunctions()) {
                    if (busDataConfiguration != null && busDataConfiguration.isSameConfig(trackFunction)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
