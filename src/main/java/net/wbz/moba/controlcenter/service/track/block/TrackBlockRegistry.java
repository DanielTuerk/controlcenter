package net.wbz.moba.controlcenter.service.track.block;

import io.vertx.core.impl.ConcurrentHashSet;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.wbz.moba.controlcenter.BusAddressIdentifier;
import net.wbz.moba.controlcenter.EventBroadcaster;
import net.wbz.moba.controlcenter.SelectrixHelper;
import net.wbz.moba.controlcenter.service.train.TrainManager;
import net.wbz.moba.controlcenter.service.train.TrainService;
import net.wbz.moba.controlcenter.shared.bus.FeedbackBlockEvent;
import net.wbz.moba.controlcenter.shared.constrution.CurrentConstructionChangeEvent;
import net.wbz.moba.controlcenter.shared.track.model.BusDataConfiguration;
import net.wbz.moba.controlcenter.shared.track.model.TrackBlock;
import net.wbz.moba.controlcenter.shared.track.model.TrackBlock.DRIVING_LEVEL_ADJUST_TYPE;
import net.wbz.moba.controlcenter.shared.viewer.TrackPartBlockEvent;
import net.wbz.selectrix4java.block.FeedbackBlockListener;
import net.wbz.selectrix4java.block.FeedbackBlockModule;
import net.wbz.selectrix4java.device.Device;
import net.wbz.selectrix4java.device.DeviceAccessException;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Registry for available {@link TrackBlock}s to add the {@link FeedbackBlockListener}s for receiving the block states
 * and to adjust driving levels of trains entering or exiting the blocks.
 *
 * @author Daniel Tuerk
 */
@Slf4j
@Singleton
public class TrackBlockRegistry {

    @Inject
    EventBroadcaster eventBroadcaster;
    @Inject
    TrainService trainService;
    @Inject
    TrainManager trainManager;
    @Inject
    TrackBlockManager trackBlockManager;

    private final Map<TrackBlock, FeedbackBlockListener> feedbackBlockListeners = new ConcurrentHashMap<>();
    private final Map<TrackBlock, Set<Long>> trainsOnBlocks = new ConcurrentHashMap<>();

    public void onCurrentConstructionChanged(@Observes CurrentConstructionChangeEvent event) {
        log.info("current construction changed for ID: {}, refreshing track blocks...", event.construction().getId());
        initBlocks(trackBlockManager.fetch(event.construction().getId()));
    }

    private void initBlocks(Collection<TrackBlock> trackBlocks) {
        log.debug("init track blocks");
        feedbackBlockListeners.clear();
        for (final TrackBlock trackBlock : trackBlocks) {

            if (checkBlockFunction(trackBlock)) {

                final BusDataConfiguration blockFunction = trackBlock.getBlockFunction();
                addFeedbackBlockListener(trackBlock, new FeedbackBlockListener() {
                    @Override
                    public void trainEnterBlock(int blockNumber, int trainAddress, boolean forward) {
                        if (blockNumber == blockFunction.getBit()) {
                            handleTrainOnBlock(true, blockNumber, trainAddress, forward, trackBlock);
                        }
                    }

                    @Override
                    public void trainLeaveBlock(int blockNumber, int trainAddress, boolean forward) {
                        if (blockNumber == blockFunction.getBit()) {
                            handleTrainOnBlock(false, blockNumber, trainAddress, forward, trackBlock);
                        }
                    }

                    @Override
                    public void blockOccupied(int blockNr) {
                        if (blockNr == blockFunction.getBit()) {
                            fireBlockEvent(true);
                        }
                    }

                    @Override
                    public void blockFreed(int blockNr) {
                        if (blockNr == blockFunction.getBit()) {
                            fireBlockEvent(false);
                        }
                    }

                    private void fireBlockEvent(boolean bitState) {
                        eventBroadcaster.fireEvent(new TrackPartBlockEvent(blockFunction,
                            bitState ? TrackPartBlockEvent.BLOCK_STATE.USED : TrackPartBlockEvent.BLOCK_STATE.FREE));
                    }
                });
            }
        }
    }

    private void addFeedbackBlockListener(TrackBlock trackBlock,
                                          FeedbackBlockListener feedbackBlockListener) {
        if (!feedbackBlockListeners.containsKey(trackBlock)) {
            feedbackBlockListeners.put(trackBlock, feedbackBlockListener);
        }
    }

    public void registerListeners(Device device) throws DeviceAccessException {
        for (Map.Entry<TrackBlock, FeedbackBlockListener> entry : feedbackBlockListeners.entrySet()) {
            getFeedbackBlockModule(device, getBusAddressIdentifier(entry.getKey().getBlockFunction()))
                .ifPresent(feedbackBlockModule ->
                    feedbackBlockModule.addFeedbackBlockListener(entry.getValue()));
        }
    }

    public void removeListeners(Device device) throws DeviceAccessException {
        for (Map.Entry<TrackBlock, FeedbackBlockListener> entry : feedbackBlockListeners.entrySet()) {
            getFeedbackBlockModule(device, getBusAddressIdentifier(entry.getKey().getBlockFunction()))
                .ifPresent(feedbackBlockModule ->
                    feedbackBlockModule.removeFeedbackBlockListener(entry.getValue()));
        }
    }

    public boolean isCurrentlyInBlock(Long trainId, TrackBlock... trackBlocks) {
        return Arrays.stream(trackBlocks)
            .anyMatch(block ->
                trainsOnBlocks.getOrDefault(block, Set.of()).contains(trainId)
            );
    }

    private Optional<FeedbackBlockModule> getFeedbackBlockModule(Device device, BusAddressIdentifier entry) throws
        DeviceAccessException {
        return device.isConnected() ? Optional.of(SelectrixHelper.getFeedbackBlockModule(device, entry))
            : Optional.empty();
    }

    private boolean checkBlockFunction(TrackBlock trackBlock) {
        return trackBlock != null && trackBlock.getBlockFunction() != null && trackBlock.getBlockFunction().isValid();
    }

    private BusAddressIdentifier getBusAddressIdentifier(BusDataConfiguration blockFunction) {
        return new BusAddressIdentifier(blockFunction);
    }

    private void handleTrainOnBlock(boolean enterBlock, int blockNumber, int trainAddress, boolean forward,
            TrackBlock trackBlock) {
        // update current block of the train
        var train$ = trainManager.getByAddress(trainAddress);
        if (train$.isEmpty()) {
            log.warn("no train with address '{}' found for block number: {} ({})",
                trainAddress, blockNumber, trackBlock);
        } else {
            var trainsOnThisBlock = trainsOnBlocks.computeIfAbsent(trackBlock, k -> new ConcurrentHashSet<>());
            if (enterBlock) {
                trainsOnThisBlock.add(train$.get().getId());
            } else {
                trainsOnThisBlock.remove(train$.get().getId());
            }
            // update automatic driving level
            DRIVING_LEVEL_ADJUST_TYPE adjustType = trackBlock.getDrivingLevelAdjustType();
            if (adjustType != DRIVING_LEVEL_ADJUST_TYPE.NONE) {
                if ((enterBlock && adjustType == DRIVING_LEVEL_ADJUST_TYPE.ENTER)
                        || (!enterBlock && adjustType == DRIVING_LEVEL_ADJUST_TYPE.EXIT)) {
                    Integer drivingLevelForTrain = forward ? trackBlock.getForwardTargetDrivingLevel()
                            : trackBlock.getBackwardTargetDrivingLevel();
                    if (drivingLevelForTrain != null) {
                        trainService.updateAutomaticDrivingLevel(train$.get(), drivingLevelForTrain);
                    }
                }
            }
            // fire position event of train to clients
            eventBroadcaster.fireEvent(new FeedbackBlockEvent(
                enterBlock ? FeedbackBlockEvent.STATE.ENTER : FeedbackBlockEvent.STATE.EXIT,
                trackBlock.getBlockFunction().getBus(),
                trackBlock.getBlockFunction().getAddress(),
                blockNumber, trainAddress, forward));
        }
    }

}
