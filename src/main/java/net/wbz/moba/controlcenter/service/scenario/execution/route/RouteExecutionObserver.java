package net.wbz.moba.controlcenter.service.scenario.execution.route;

import io.smallrye.mutiny.tuples.Tuple2;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.wbz.moba.controlcenter.BusAddressIdentifier;
import net.wbz.moba.controlcenter.SelectrixHelper;
import net.wbz.moba.controlcenter.service.scenario.execution.ScenarioExecutionInterruptException;
import net.wbz.moba.controlcenter.service.track.TrackProvider;
import net.wbz.moba.controlcenter.shared.scenario.Route;
import net.wbz.moba.controlcenter.shared.scenario.Route.ROUTE_RUN_STATE;
import net.wbz.moba.controlcenter.shared.scenario.RouteSequence;
import net.wbz.moba.controlcenter.shared.track.model.BlockStraight;
import net.wbz.moba.controlcenter.shared.track.model.BusDataConfiguration;
import net.wbz.moba.controlcenter.shared.track.model.TrackBlock;
import net.wbz.selectrix4java.block.BlockListener;
import net.wbz.selectrix4java.device.Device;
import net.wbz.selectrix4java.device.DeviceAccessException;
import net.wbz.selectrix4java.device.DeviceManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * Observer for the running routes in a scenario execution. The observer holds all running routes and must be
 * used to check for a free track and to reserve a free track. Route dependencies on the track are detected by the
 * observer and will be checked for the reservations of routes.
 *
 * @author Daniel Tuerk
 */
@Slf4j
@ApplicationScoped
public class RouteExecutionObserver {

    /**
     * Actual running {@link RouteSequence}s which need to be thread safe. Each execution will access the collection in
     * parallel.
     */
    private final Collection<RouteSequence> runningRouteSequences = Collections.synchronizedSet(new HashSet<>());

    private final Map<Long, List<Tuple2<BusAddressIdentifier, BlockListener>>> blockListenersOfRouteSequence = new ConcurrentHashMap<>();

    private final TrackProvider trackProvider;
    private final DeviceManager deviceManager;
    private final RouteStateChangedObserver routeStateChangedObserver;
    private final RouteReservationCoordinator routeReservationCoordinator;
    private final SwitchStartSignalOfRouteToDrive switchStartSignalOfRouteToDrive;

    @Inject
    public RouteExecutionObserver(TrackProvider trackProvider, DeviceManager deviceManager,
                                  RouteStateChangedObserver routeStateChangedObserver,
                                  RouteReservationCoordinator routeReservationCoordinator,
                                  SwitchStartSignalOfRouteToDrive switchStartSignalOfRouteToDrive) {
        this.trackProvider = trackProvider;
        this.deviceManager = deviceManager;
        this.routeStateChangedObserver = routeStateChangedObserver;
        this.routeReservationCoordinator = routeReservationCoordinator;
        this.switchStartSignalOfRouteToDrive = switchStartSignalOfRouteToDrive;
    }

    /**
     * Synchronized to guarantee a single reserve for a route execution.
     *
     * @param routeSequence {@link RouteSequence} to check and to reserve run
     * @param previousRouteSequence the previous executed {@link RouteSequence}
     * @return {@code true} if the track was free and the given route is reserved.
     */
    synchronized boolean checkAndReserveNextRunningRoute(long scenarioId, RouteSequence routeSequence,
                                                         Optional<RouteSequence> previousRouteSequence) {
        final var route = routeSequence.getRoute();
        final var dependingRunningRoutes = getDependingRunningRoutes(route);

        // previous given one is running, which will be now removed to start next route in the sequence
        previousRouteSequence.ifPresent(dependingRunningRoutes::remove);
        // all are prepared, then start with one by setting state RESERVED to avoid deadlock of routes
        boolean allDependingRoutesHaveStatePrepared = dependingRunningRoutes.stream()
            .allMatch(x -> routeStateChangedObserver.getRouteState(x.getId()) == ROUTE_RUN_STATE.PREPARED);

        // Check that no depending on route is running
        if (dependingRunningRoutes.isEmpty() || allDependingRoutesHaveStatePrepared) {

            // only set to reserve if the next route blocks are free, also without dependent route
            if (allBlocksAreFree(route.getAllTrackBlocksToDrive())) {
                if (routeReservationCoordinator.reserve(scenarioId, routeSequence)) {
                    switchStartSignalOfRouteToDrive.call(route).subscribe().with(
                        unused -> log.debug("reserved next route: {} ({})", route.getName(), route.getId()),
                        failure -> log.error("failed to reserve next route: {} ({})", route.getName(), route.getId()));
                }
                return true;
            } else {
                /*
                 * Still blocks occupied to drive without a route depending on.
                 * From manual drive or not started route.
                 */
                return false;
            }
        }
        return false;
    }

    void registerBlocksToReserveNextRoute(long scenarioId, RouteSequence current, RouteSequence next) {
        // register block listener for each block on the track to try to reserve the next route immediately
        log.debug("register block listeners for route: {} ({})", current.getRoute().getName(),
            current.getRoute().getId());

        // try to reserve next route from start block
        for (TrackBlock trackBlock : current.getRoute().getStart().getAllTrackBlocks()) {
            try {
                addBlockListener(new BlockListener() {
                    @Override
                    public void blockOccupied(int blockNr) {
                        //ignore, that's the start of the route
                    }

                    @Override
                    public void blockFreed(int blockNr) {
                        reserveNextRouteForCurrentRoute(scenarioId, current, next);
                    }
                }, trackBlock.getBlockFunction(), connectedDevice());
            } catch (DeviceAccessException e) {
                final var msg = "device error during reserve next route from start block: " + current.getRoute().getName();
                log.error(msg, e);
                throw new ScenarioExecutionInterruptException(msg, e);
            }
        }

        // try to reserve next route from each block running through
        for (TrackBlock trackBlock : current.getRoute().getTrack().trackBlocks()) {
            try {
                addBlockListener(new BlockListener() {
                    @Override
                    public void blockOccupied(int blockNr) {
                        extracted(blockNr);
                    }

                    @Override
                    public void blockFreed(int blockNr) {
                        extracted(blockNr);
                    }

                    private void extracted(int ignored) {
                        reserveNextRouteForCurrentRoute(scenarioId, current, next);
                    }
                }, trackBlock.getBlockFunction(), connectedDevice());
            } catch (DeviceAccessException e) {
                final var msg = "device error during reserve next route: " + current.getRoute().getName();
                log.error(msg, e);
                throw new ScenarioExecutionInterruptException(msg, e);
            }
        }
    }

    public synchronized void reserveNextRouteForCurrentRoute(long scenarioId, RouteSequence current, RouteSequence next) {
        if (routeReservationCoordinator.isAlreadyReserved(next)) {
            log.debug("route sequence {} already reserved", next.getId());
            return;
        }
        checkAndReserveNextRunningRoute(scenarioId, next, Optional.of(current));
    }

    public void cleanup(RouteSequence routeSequence) throws DeviceAccessException {
        log.debug("removeRunningRouteSequence: {}", routeSequence);
        synchronized (runningRouteSequences) {
            runningRouteSequences.remove(routeSequence);
        }

        if (blockListenersOfRouteSequence.containsKey(routeSequence.getId())) {
            for (Tuple2<BusAddressIdentifier, BlockListener> entry : blockListenersOfRouteSequence.get(routeSequence.getId())) {
                SelectrixHelper.getBlockModule(connectedDevice(), entry.getItem1())
                    .removeBlockListener(entry.getItem2());
            }
        }
    }

    private Device connectedDevice() throws DeviceAccessException {
        return deviceManager.getConnectedDevice()
            .orElseThrow(() -> new DeviceAccessException("no connected device"));
    }

    private void addBlockListener(BlockListener listener, BusDataConfiguration busDataConfiguration, Device device) throws
        DeviceAccessException {
        final var busAddressIdentifier = new BusAddressIdentifier(busDataConfiguration);
        SelectrixHelper.getBlockModule(device, busAddressIdentifier).addBlockListener(listener);
        if (!blockListenersOfRouteSequence.containsKey(busDataConfiguration.getId())) {
            blockListenersOfRouteSequence.put(busDataConfiguration.getId(), new CopyOnWriteArrayList<>());
        }
        blockListenersOfRouteSequence.get(busDataConfiguration.getId()).add(Tuple2.of(busAddressIdentifier, listener));
    }

    synchronized void addRunningRouteSequence(RouteSequence routeSequence) {
        log.debug("addRunningRouteSequence: {}", routeSequence);
        runningRouteSequences.add(routeSequence);
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

    /**
     * Return the actual running {@link RouteSequence}s which depends by the {@link TrackBlock}s and {@link
     * BusDataConfiguration}s of track parts on the given {@link Route}
     *
     * @param route {@link Route} to check
     * @return depending on running {@link RouteSequence}s
     */
    private Collection<RouteSequence> getDependingRunningRoutes(final Route route) {
        synchronized (runningRouteSequences) {
            return runningRouteSequences.stream().filter(input -> !route.equals(input.getRoute())
                // add as dependent if routes have the same track block or function
                && (containsSameTrackBlock(route, input.getRoute()) || containsSameTrackFunctions(input.getRoute(),
                route))).collect(Collectors.toSet());
        }
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
//        return Uni.createFrom().item(() ->
        return trackProvider.getTrack().stream()
            .filter(x -> x instanceof BlockStraight)
            .map(BlockStraight.class::cast)
            .map(BlockStraight::getAllTrackBlocks)
            .filter(allTrackBlocks -> allTrackBlocks.contains(left))
            .flatMap(Collection::stream)
            .collect(Collectors.toSet());
        // TODO nicht gut hier
//            .runSubscriptionOn(Infrastructure.getDefaultWorkerPool());
    }

    /**
     * Check for the same track functions (like switch) on the track of given routes.
     * The track functions are only checked for bus, address, and bit.
     * The bit state doesn't depend on detecting a single switch which is used in both routes, also
     * for different states.
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
