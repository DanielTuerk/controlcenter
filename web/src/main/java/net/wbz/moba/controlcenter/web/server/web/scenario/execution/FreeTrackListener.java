package net.wbz.moba.controlcenter.web.server.web.scenario.execution;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.wbz.moba.controlcenter.web.server.SelectrixHelper;
import net.wbz.moba.controlcenter.web.server.web.editor.block.BusAddressIdentifier;
import net.wbz.moba.controlcenter.web.shared.track.model.TrackBlock;
import net.wbz.selectrix4java.block.BlockListener;
import net.wbz.selectrix4java.block.BlockNumberListener;
import net.wbz.selectrix4java.block.FeedbackBlockModule;
import net.wbz.selectrix4java.device.Device;
import net.wbz.selectrix4java.device.DeviceAccessException;

/**
 * TODO doc
 * 
 * @author Daniel Tuerk
 */
abstract class FreeTrackListener {

    private static final Logger LOG = LoggerFactory.getLogger(FreeTrackListener.class);

    private final TrackBlock monitoringBlock;
    private final TrackBlock endBlock;
    private final Device device;
    private final List<BlockListener> blockListeners = new ArrayList<>();
    private boolean freeTrack = false;
    private boolean freeEnd = false;

    FreeTrackListener(@NotNull TrackBlock monitoringBlock, @NotNull TrackBlock endBlock, @NotNull Device device) {
        this.monitoringBlock = monitoringBlock;
        this.endBlock = endBlock;
        this.device = device;
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
        freeTrack = blockIsFree(monitoringBlock);
        freeEnd = blockIsFree(endBlock);

        check();

        addListener(monitoringBlock);
        addListener(endBlock);
    }

    private void addListener(@NotNull final TrackBlock trackBlock) throws DeviceAccessException {
        // TODO remove nullable, nach belegtmelder anschluss, auch bei "blockFree"
        BlockNumberListener listener = new BlockNumberListener(trackBlock.getBlockFunction().getBit()) {
            @Override
            public void freed() {
                // if (trackBlock == startBlock) {
                // freeStart = true;
                if (trackBlock == monitoringBlock) {
                    freeTrack = true;
                } else if (trackBlock == endBlock) {
                    freeEnd = true;
                }
                check();
            }

            @Override
            public void occupied() {
                // if (trackBlock == startBlock) {
                // freeStart = false;
                if (trackBlock == monitoringBlock) {
                    freeTrack = false;
                } else if (trackBlock == endBlock) {
                    freeEnd = false;
                }
            }
        };
        blockListeners.add(listener);
        getFeedbackBlockModule(trackBlock).addBlockListener(listener);
    }

    private void check() {
        if (freeTrack && freeEnd) {
            // unregister
            for (BlockListener blockListener : blockListeners) {
                try {
                    // getFeedbackBlockModule(startBlock).removeBlockListener(blockListener);
                    getFeedbackBlockModule(monitoringBlock).removeBlockListener(blockListener);
                    getFeedbackBlockModule(endBlock).removeBlockListener(blockListener);
                } catch (DeviceAccessException e) {
                    LOG.error("can't remove listeners", e);
                }
            }
            ready();
        }

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
