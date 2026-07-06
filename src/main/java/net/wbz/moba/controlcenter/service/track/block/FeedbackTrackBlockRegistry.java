package net.wbz.moba.controlcenter.service.track.block;

import io.vertx.core.impl.ConcurrentHashSet;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.wbz.moba.controlcenter.BusAddressIdentifier;
import net.wbz.moba.controlcenter.EventBroadcaster;
import net.wbz.moba.controlcenter.SelectrixHelper;
import net.wbz.moba.controlcenter.service.track.TrackProvider;
import net.wbz.moba.controlcenter.service.train.TrainManager;
import net.wbz.moba.controlcenter.service.train.TrainService;
import net.wbz.moba.controlcenter.shared.constrution.CurrentConstructionChangeEvent;
import net.wbz.moba.controlcenter.shared.track.model.BlockStraight;
import net.wbz.moba.controlcenter.shared.track.model.BusDataConfiguration;
import net.wbz.moba.controlcenter.shared.track.model.TrackBlock;
import net.wbz.moba.controlcenter.shared.track.model.TrackBlock.DRIVING_LEVEL_ADJUST_TYPE;
import net.wbz.moba.controlcenter.shared.train.Train;
import net.wbz.moba.controlcenter.shared.viewer.TrackPartBlockEvent;
import net.wbz.selectrix4java.block.BlockListener;
import net.wbz.selectrix4java.block.BlockModule;
import net.wbz.selectrix4java.block.FeedbackBlockListener;
import net.wbz.selectrix4java.block.FeedbackBlockModule;
import net.wbz.selectrix4java.device.Device;
import net.wbz.selectrix4java.device.DeviceAccessException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Registry for available {@link TrackBlock}s to add the {@link FeedbackBlockListener}s for receiving the block states
 * and to adjust driving levels of trains entering or exiting the blocks.
 *
 * @author Daniel Tuerk
 */
@Slf4j
@Singleton
public class FeedbackTrackBlockRegistry {

    private final EventBroadcaster eventBroadcaster;
    private final TrainService trainService;
    private final TrainManager trainManager;
    private final TrackBlockManager trackBlockManager;
    private final TrackProvider trackProvider;

    private final Map<TrackBlock, BlockListener> feedbackBlockListeners = new ConcurrentHashMap<>();
    private final Map<TrackBlock, Set<Long>> trainsOnBlocks = new ConcurrentHashMap<>();

    public FeedbackTrackBlockRegistry(EventBroadcaster eventBroadcaster, TrainService trainService,
                                      TrainManager trainManager, TrackBlockManager trackBlockManager, TrackProvider trackProvider) {
        this.eventBroadcaster = eventBroadcaster;
        this.trainService = trainService;
        this.trainManager = trainManager;
        this.trackBlockManager = trackBlockManager;
        this.trackProvider = trackProvider;
    }

    public void onCurrentConstructionChanged(@Observes CurrentConstructionChangeEvent event) {
        log.info("current construction changed for ID: {}, refreshing track blocks...", event.construction().getId());
        initBlocks(trackBlockManager.fetch(event.construction().getId()));
    }

    private synchronized void initBlocks(Collection<TrackBlock> trackBlocks) {
        log.debug("init track blocks");
        feedbackBlockListeners.clear();
        for (final TrackBlock trackBlock : trackBlocks) {

            if (checkBlockFunction(trackBlock)) {

                final var blockFunction = trackBlock.getBlockFunction();
                if (trackBlock.getFeedback()) {
                    addBlockListener(trackBlock, new FeedbackBlockListener() {
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
                                fireBlockEvent(trackBlock, true);
                            }
                        }

                        @Override
                        public void blockFreed(int blockNr) {
                            if (blockNr == blockFunction.getBit()) {
                                fireBlockEvent(trackBlock, false);
                            }
                        }
                    });
                } else {
                    addBlockListener(trackBlock, new BlockListener() {

                        @Override
                        public void blockOccupied(int blockNr) {
                            if (blockNr == blockFunction.getBit()) {
                                fireBlockEvent(trackBlock, true);
                            }
                        }

                        @Override
                        public void blockFreed(int blockNr) {
                            if (blockNr == blockFunction.getBit()) {
                                fireBlockEvent(trackBlock, false);
                            }
                        }
                    });
                }
            }
        }
    }

    private void fireBlockEvent(TrackBlock trackBlock, boolean bitState) {
        fireBlockEvent(trackBlock, bitState, null);
    }

    private void fireBlockEvent(TrackBlock trackBlock, boolean bitState,
                                TrackPartBlockEvent.FeedbackBlockData feedbackBlockData) {
        final var trackPartId = trackProvider.getTrack().stream().filter(trackPart -> {
                if (trackPart instanceof BlockStraight) {
                    return ((BlockStraight) trackPart).getAllTrackBlocks().contains(trackBlock);
                }
                return false;
            }).findFirst()
            .orElseThrow(() -> new IllegalArgumentException("TrackPart not found for TrackBlock([%d])"
                .formatted(trackBlock.getId())))
            .getId();
        eventBroadcaster.fireEvent(new TrackPartBlockEvent(trackPartId, trackBlock.getBlockFunction().getAddress(),
            trackBlock.getBlockFunction().getBit(), bitState, feedbackBlockData));
    }

    private void addBlockListener(TrackBlock trackBlock, BlockListener blockListener) {
        if (!feedbackBlockListeners.containsKey(trackBlock)) {
            feedbackBlockListeners.put(trackBlock, blockListener);
        }
    }

    public void registerListeners(Device device) throws DeviceAccessException {
        for (Map.Entry<TrackBlock, BlockListener> entry : feedbackBlockListeners.entrySet()) {
            if (entry.getValue() instanceof FeedbackBlockListener) {
                getFeedbackBlockModule(device, getBusAddressIdentifier(entry.getKey().getBlockFunction()))
                    .ifPresent(feedbackBlockModule ->
                        feedbackBlockModule.addFeedbackBlockListener((FeedbackBlockListener) entry.getValue()));
            } else {
                getBlockModule(device, getBusAddressIdentifier(entry.getKey().getBlockFunction()))
                    .ifPresent(blockModule -> blockModule.addBlockListener(entry.getValue()));
            }
        }
    }

    public void removeListeners(Device device) throws DeviceAccessException {
        for (Map.Entry<TrackBlock, BlockListener> entry : feedbackBlockListeners.entrySet()) {
            if (entry.getValue() instanceof FeedbackBlockListener) {
                getFeedbackBlockModule(device, getBusAddressIdentifier(entry.getKey().getBlockFunction()))
                    .ifPresent(feedbackBlockModule ->
                        feedbackBlockModule.removeFeedbackBlockListener((FeedbackBlockListener) entry.getValue()));
            } else {
                getBlockModule(device, getBusAddressIdentifier(entry.getKey().getBlockFunction()))
                    .ifPresent(blockModule -> blockModule.removeBlockListener(entry.getValue()));
            }
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


    private Optional<BlockModule> getBlockModule(Device device, BusAddressIdentifier entry) throws
        DeviceAccessException {
        return device.isConnected() ? Optional.of(SelectrixHelper.getBlockModule(device, entry))
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
            fireBlockEvent(trackBlock,
                enterBlock,
                new TrackPartBlockEvent.FeedbackBlockData(trainAddress,
                    forward ? Train.DRIVING_DIRECTION.FORWARD : Train.DRIVING_DIRECTION.BACKWARD));
        }
    }

}
