package io.github.danieltuerk.controlcenter.service.scenario.execution.route;

import io.github.danieltuerk.controlcenter.BusAddressIdentifier;
import io.github.danieltuerk.controlcenter.SelectrixHelper;
import io.github.danieltuerk.controlcenter.service.scenario.execution.ScenarioExecutionInterruptException;
import io.github.danieltuerk.controlcenter.service.track.TrackProvider;
import io.github.danieltuerk.controlcenter.shared.scenario.Route;
import io.github.danieltuerk.controlcenter.shared.scenario.Route.ROUTE_RUN_STATE;
import io.github.danieltuerk.controlcenter.shared.scenario.RouteSequence;
import io.github.danieltuerk.controlcenter.shared.track.model.BlockStraight;
import io.github.danieltuerk.controlcenter.shared.track.model.BusDataConfiguration;
import io.github.danieltuerk.controlcenter.shared.track.model.TrackBlock;
import io.github.danieltuerk.selectrix4java.block.BlockListener;
import io.github.danieltuerk.selectrix4java.device.Device;
import io.github.danieltuerk.selectrix4java.device.DeviceAccessException;
import io.github.danieltuerk.selectrix4java.device.DeviceManager;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.tuples.Tuple2;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
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
     * @param routeSequence {@link RouteSequence} to check
     * @return {@code true} if the track was free, also without dependent route
     *         {@code false} if still blocks occupied to drive without a route depending on.
     *          (From manual drive or not started route)
     */
    synchronized boolean checkForNextRouteToRun(RouteSequence routeSequence) {
        log.debug("check for next route to run: {} ({}) [seqId: {}]", routeSequence.getRoute().getName(), routeSequence.getRoute().getId(), routeSequence.getId());
        final var route = routeSequence.getRoute();
        final var dependingRunningRoutes = getDependingRunningRoutes(route);

        // all are prepared, then start with one by setting state RESERVED to avoid deadlock of routes
        boolean allDependingRoutesHaveStatePrepared = dependingRunningRoutes.stream()
            .allMatch(x -> routeStateChangedObserver.getRouteState(x.getId()) == ROUTE_RUN_STATE.PREPARED);

        // Check that no depending on route is running
        if (dependingRunningRoutes.isEmpty() || allDependingRoutesHaveStatePrepared) {
            // only set to reserve if the next route blocks are free, also without dependent route
            return allBlocksAreFree(route.getAllTrackBlocksToDrive());
        }
        return false;
    }

    /**
     * Register {@link BlockListener} for each block running through on the track to try to reserve the next route
     * immediately.
     *
     * @param scenarioId {@link Long} id of scneario
     * @param current    the current {@link RouteSequence}
     * @param next       the next {@link RouteSequence}
     */
    void registerBlocksToReserveNextRoute(long scenarioId, RouteSequence current, RouteSequence next) {
        log.debug("register block listeners to running through for route: {} ({})",
                current.getRoute().getName(), current.getRoute().getId());

        for (TrackBlock trackBlock : current.getRoute().getTrack().trackBlocks()) {
            try {
                addBlockListener(current, connectedDevice(), trackBlock.getBlockFunction(), new BlockListener() {
                    @Override
                    public void blockOccupied(int blockNr) {
                        extracted(blockNr);
                    }

                    @Override
                    public void blockFreed(int blockNr) {
                        extracted(blockNr);
                    }

                    private void extracted(int ignored) {
                        log.debug("block state changed reservation: {} ({}; routeSequenceId: {}; block: {} ({}))",
                                current.getRoute().getName(), current.getRoute().getId(), current.getId(), trackBlock.getName(), trackBlock.getId());
                        reserveNextRouteForCurrentRoute(scenarioId, next).subscribe()
                                .with(unused -> log.debug("next routeSequenceId {} reserved from routeSequenceId {}", current.getId(), next.getId()),
                                        failure -> log.error("failed to reserve routeSequenceId {} from routeSequenceId {}", current.getId(), next.getId()));
                    }
                });
            } catch (DeviceAccessException e) {
                final var msg = "device error during reserve next route: " + current.getRoute().getName();
                log.error(msg, e);
                throw new ScenarioExecutionInterruptException(msg, e);
            }
        }
    }

    public synchronized Uni<Void> reserveNextRouteForCurrentRoute(long scenarioId,
                                                                  RouteSequence next) {
        if (routeReservationCoordinator.isAlreadyReserved(next)) {
            log.debug("route sequence {} already reserved", next.getId());
            return Uni.createFrom().voidItem();
        }
        if (checkForNextRouteToRun(next)) {
            routeReservationCoordinator.reserve(scenarioId, next);
        }
        return Uni.createFrom().voidItem();
    }

    private synchronized void addBlockListener(RouteSequence routeSequence, Device device,
                                               BusDataConfiguration busDataConfiguration, BlockListener listener)
            throws DeviceAccessException {
        log.debug("add block listener for routeSeqId {}: {}", routeSequence.getId(), busDataConfiguration);
        final var busAddressIdentifier = new BusAddressIdentifier(busDataConfiguration);
        SelectrixHelper.getBlockModule(device, busAddressIdentifier).addBlockListener(listener);
        final var key = routeSequence.getId();
        if (!blockListenersOfRouteSequence.containsKey(key)) {
            blockListenersOfRouteSequence.put(key, new CopyOnWriteArrayList<>());
        }
        blockListenersOfRouteSequence.get(key).add(Tuple2.of(busAddressIdentifier, listener));
    }

    void addRunningRouteSequence(RouteSequence routeSequence) {
        log.debug("addRunningRouteSequence: {} ({}) [seqId: {}]", routeSequence.getRoute().getName(),
                routeSequence.getRoute().getId(), routeSequence.getId());
        runningRouteSequences.add(routeSequence);
    }

    public void cleanup(RouteSequence routeSequence) throws DeviceAccessException {
        log.debug("cleanup for: {} ({}) [seqId: {}]", routeSequence.getRoute().getName(),
                routeSequence.getRoute().getId(), routeSequence.getId());

        runningRouteSequences.remove(routeSequence);

        final var key = routeSequence.getId();
        if (blockListenersOfRouteSequence.containsKey(key)) {
            for (Tuple2<BusAddressIdentifier, BlockListener> entry : blockListenersOfRouteSequence.get(key)) {
                SelectrixHelper.getBlockModule(connectedDevice(), entry.getItem1())
                        .removeBlockListener(entry.getItem2());
            }
            blockListenersOfRouteSequence.remove(key);
        }
    }

    private Device connectedDevice() throws DeviceAccessException {
        return deviceManager.getConnectedDevice()
                .orElseThrow(() -> new DeviceAccessException("no connected device"));
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
        return trackProvider.getTrack().stream()
            .filter(x -> x instanceof BlockStraight)
            .map(BlockStraight.class::cast)
            .map(BlockStraight::getAllTrackBlocks)
            .filter(allTrackBlocks -> allTrackBlocks.contains(left))
            .flatMap(Collection::stream)
            .collect(Collectors.toSet());
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
