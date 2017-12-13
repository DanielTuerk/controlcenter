package net.wbz.moba.controlcenter.web.server.web.scenario.execution;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.wbz.moba.controlcenter.web.server.SelectrixHelper;
import net.wbz.moba.controlcenter.web.server.web.editor.block.BusAddressIdentifier;
import net.wbz.moba.controlcenter.web.server.web.scenario.ScenarioManager;
import net.wbz.moba.controlcenter.web.shared.scenario.Route;
import net.wbz.moba.controlcenter.web.shared.scenario.Scenario;
import net.wbz.moba.controlcenter.web.shared.scenario.Scenario.RUN_STATE;
import net.wbz.moba.controlcenter.web.shared.track.model.TrackBlock;
import net.wbz.selectrix4java.block.BlockListener;
import net.wbz.selectrix4java.block.BlockNumberListener;
import net.wbz.selectrix4java.block.FeedbackBlockModule;
import net.wbz.selectrix4java.device.Device;
import net.wbz.selectrix4java.device.DeviceAccessException;

/**
 * Listener for {@link TrackBlock}s to monitor the state. As soon as all blocks are free the callback {@link #ready()}
 * is called.
 * 
 * @author Daniel Tuerk
 */
abstract class FreeTrackListener {

    private static final Logger LOG = LoggerFactory.getLogger(FreeTrackListener.class);

    /**
     * All blocks on the track.
     */
    private final Set<TrackBlock> trackBlocks;
    /**
     * Current state of each track block.
     */
    private final Map<TrackBlock, Boolean> trackBlockStates = new ConcurrentHashMap<>();
    /**
     * End block of the track.
     */
    private final Device device;
    /**
     * Registered blocks to check.
     */
    private final List<BlockListener> blockListeners = new ArrayList<>();
    private final ScenarioManager scenarioManager;
    /**
     * Actual executed {@link Scenario}.
     */
    private final Scenario scenario;
    /**
     * Actual running {@link Route} in the route sequences of the {@link Scenario}.
     */
    private final Route route;

    /**
     * Create listener.
     *
     * @param scenario {@link Scenario}
     * @param route {@link Route}
     * @param device connected {@link Device}
     * @param scenarioManager {@link ScenarioManager}
     */
    FreeTrackListener(Scenario scenario, @NotNull Route route, @NotNull Device device,
            ScenarioManager scenarioManager) {
        this.scenario = scenario;
        this.route = route;
        this.trackBlocks = route.getTrack().getTrackBlocks();
        trackBlocks.add(route.getEnd());
        this.device = device;
        this.scenarioManager = scenarioManager;
    }

    /**
     * Callback for a free track.
     */
    abstract void ready();

    /**
     * Start listen to the start, track and end point. After all blocks are free the method {@link #ready()} is called.
     * 
     * @throws DeviceAccessException
     */
    void listen() throws DeviceAccessException {
        // check for free track atm
        for (TrackBlock trackBlock : trackBlocks) {
            trackBlockStates.put(trackBlock, blockIsFree(trackBlock));
        }

        if (!check()) {
            // no free track, add listeners to wait for free track
            for (TrackBlock trackBlock : trackBlocks) {
                addListener(trackBlock);
            }
        }
    }

    private void addListener(@NotNull final TrackBlock trackBlock) throws DeviceAccessException {
        BlockNumberListener listener = new BlockNumberListener(trackBlock.getBlockFunction().getBit()) {
            @Override
            public void freed() {
                if (trackBlockStates.containsKey(trackBlock)) {
                    trackBlockStates.put(trackBlock, true);
                }
                check();
            }

            @Override
            public void occupied() {
                if (trackBlockStates.containsKey(trackBlock)) {
                    trackBlockStates.put(trackBlock, false);
                }
            }
        };
        blockListeners.add(listener);
        getFeedbackBlockModule(trackBlock).addBlockListener(listener);
    }

    private boolean check() {
        if (isTrackFree()) {
            if (noDependingRoutesRunning(trackBlocks)) {
                // unregister
                try {
                    for (TrackBlock trackBlock : trackBlocks) {
                        for (BlockListener blockListener : blockListeners) {
                            getFeedbackBlockModule(trackBlock).removeBlockListener(blockListener);
                        }
                    }
                } catch (DeviceAccessException e) {
                    LOG.error("can't remove listeners", e);
                }
                ready();
                return true;
            }
        }
        return false;
    }

    private boolean noDependingRoutesRunning(Collection<TrackBlock> blocks) {
        boolean run = true;
        while (run && scenario.getRunState() != RUN_STATE.STOPPED) {
            // TODO refactor to listeners or (and) route dependencies
            if (scenarioManager.isDependingRouteRunning(route, blocks)) {
                // dependency running, wait and recheck
                try {
                    Thread.sleep(500L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                // no dependent route running, stop check
                run = false;
            }
        }
        return true;
    }

    private boolean isTrackFree() {
        return !trackBlockStates.values().contains(Boolean.FALSE);
    }

    private boolean blockIsFree(TrackBlock trackBlock) throws DeviceAccessException {
        if (trackBlock == null) {
            return true;
        }
        FeedbackBlockModule feedbackBlockModule = getFeedbackBlockModule(trackBlock);
        return !feedbackBlockModule.getLastReceivedBlockState(trackBlock.getBlockFunction().getBit());
    }

    private FeedbackBlockModule getFeedbackBlockModule(TrackBlock trackBlock) throws DeviceAccessException {
        return SelectrixHelper.getFeedbackBlockModule(
                device, new BusAddressIdentifier(trackBlock.getBlockFunction()));
    }
}
