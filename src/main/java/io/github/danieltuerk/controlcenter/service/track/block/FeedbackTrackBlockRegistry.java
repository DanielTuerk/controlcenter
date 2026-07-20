package io.github.danieltuerk.controlcenter.service.track.block;

import io.github.danieltuerk.controlcenter.BusAddressIdentifier;
import io.github.danieltuerk.controlcenter.EventBroadcaster;
import io.github.danieltuerk.controlcenter.SelectrixHelper;
import io.github.danieltuerk.controlcenter.service.track.TrackProvider;
import io.github.danieltuerk.controlcenter.service.train.TrainManager;
import io.github.danieltuerk.controlcenter.service.train.TrainService;
import io.github.danieltuerk.controlcenter.shared.constrution.CurrentConstructionChangeEvent;
import io.github.danieltuerk.controlcenter.shared.device.DeviceConnectionEvent;
import io.github.danieltuerk.controlcenter.shared.track.model.BlockStraight;
import io.github.danieltuerk.controlcenter.shared.track.model.BusDataConfiguration;
import io.github.danieltuerk.controlcenter.shared.track.model.TrackBlock;
import io.github.danieltuerk.controlcenter.shared.track.model.TrackBlock.DRIVING_LEVEL_ADJUST_TYPE;
import io.github.danieltuerk.controlcenter.shared.train.Train;
import io.github.danieltuerk.controlcenter.shared.viewer.TrackPartBlockEvent;
import io.github.danieltuerk.controlcenter.shared.viewer.TrainInBlockEvent;
import io.github.danieltuerk.selectrix4java.block.BlockListener;
import io.github.danieltuerk.selectrix4java.block.BlockModule;
import io.github.danieltuerk.selectrix4java.block.FeedbackBlockListener;
import io.github.danieltuerk.selectrix4java.block.FeedbackBlockModule;
import io.github.danieltuerk.selectrix4java.device.Device;
import io.github.danieltuerk.selectrix4java.device.DeviceAccessException;
import io.vertx.core.impl.ConcurrentHashSet;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

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

    private final Map<TrackBlock, List<BlockListener>> feedbackBlockListeners = new ConcurrentHashMap<>();
    private final Map<TrackBlock, Set<Long>> trainsOnBlocks = new ConcurrentHashMap<>();
    private final Map<Long, Set<BusDataConfiguration>> blockStraightOccupations = new ConcurrentHashMap<>();

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
        initBlockStraights(trackProvider.getTrack().stream()
                .filter(x -> x instanceof BlockStraight)
                .map(x -> (BlockStraight) x)
                .collect(Collectors.toSet()));
    }

    public void onDeviceConnectionEvent(@Observes DeviceConnectionEvent event) {
        log.info("device connection changed for device ID: {}", event.deviceInfo().getId());
        trainsOnBlocks.clear();
        blockStraightOccupations.clear();
    }

    private synchronized void initBlockStraights(Collection<BlockStraight> blockStraights) {
        log.debug("init blocks of block straights");
        blockStraights.forEach(blockStraight -> {
            blockStraightOccupations.put(blockStraight.getId(), ConcurrentHashMap.newKeySet());

            blockStraight.getAllTrackBlocks().forEach(trackBlock -> {

                if (checkBlockFunction(trackBlock)) {

                    final var blockFunction = trackBlock.getBlockFunction();
                    addBlockListener(trackBlock, createFeedbackBlockListener(blockStraight, trackBlock, blockFunction));
                }
            });
        });
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
                        }

                        @Override
                        public void blockFreed(int blockNr) {
                        }
                    });
                }
            }
        }
    }

    private FeedbackBlockListener createFeedbackBlockListener(BlockStraight blockStraight, TrackBlock trackBlock,
                                                              BusDataConfiguration blockFunction) {
        final var blockStraightId = blockStraight.getId();
        return new FeedbackBlockListener() {

            @Override
            public void trainEnterBlock(int blockNumber, int trainAddress, boolean forward) {
                if (blockNumber == blockFunction.getBit()) {
                    sendTrainOnBlockEvent(true, trainAddress, forward, blockStraightId);
                }
            }

            @Override
            public void trainLeaveBlock(int blockNumber, int trainAddress, boolean forward) {
                if (blockNumber == blockFunction.getBit()) {
                    sendTrainOnBlockEvent(false, trainAddress, forward, blockStraightId);
                }
            }

            @Override
            public void blockOccupied(int blockNr) {
                if (blockNr == blockFunction.getBit()) {
                    addForBlockStraight(blockStraightId, trackBlock.getBlockFunction());
                }
            }

            @Override
            public void blockFreed(int blockNr) {
                if (blockNr == blockFunction.getBit()) {
                    removeForBlockStraight(blockStraightId, trackBlock);
                }
            }
        };
    }

    private void removeForBlockStraight(Long blockStraightId, TrackBlock trackBlock) {
        final var entries = blockStraightOccupations.get(blockStraightId);
        entries.remove(trackBlock.getBlockFunction());
        if (entries.isEmpty()) {
            fireBlockEvent(blockStraightId, false);
        }
    }

    private synchronized void addForBlockStraight(long blockStraightId, BusDataConfiguration blockFunction) {
        final var entries = blockStraightOccupations.get(blockStraightId);
        if (entries.isEmpty()) {
            fireBlockEvent(blockStraightId, true);
        }
        entries.add(blockFunction);
    }

    private void sendTrainOnBlockEvent(boolean enter, int trainAddress, boolean forward, long blockStraightId) {
        eventBroadcaster.fireEvent(new TrainInBlockEvent(blockStraightId, enter, trainAddress,
                forward ? Train.DRIVING_DIRECTION.FORWARD : Train.DRIVING_DIRECTION.BACKWARD));
    }

    private void fireBlockEvent(long trackPartId, boolean occupied) {
        eventBroadcaster.fireEvent(new TrackPartBlockEvent(trackPartId, occupied));
    }

    private void addBlockListener(TrackBlock trackBlock, BlockListener blockListener) {
        if (!feedbackBlockListeners.containsKey(trackBlock)) {
            feedbackBlockListeners.put(trackBlock, new ArrayList<>());
        }
        feedbackBlockListeners.get(trackBlock).add(blockListener);
    }

    public void registerListeners(Device device) throws DeviceAccessException {
        for (Map.Entry<TrackBlock, List<BlockListener>> entry : feedbackBlockListeners.entrySet()) {
            for (BlockListener blockListener : entry.getValue()) {
                if (blockListener instanceof FeedbackBlockListener) {
                    getFeedbackBlockModule(device, getBusAddressIdentifier(entry.getKey().getBlockFunction()))
                            .ifPresent(feedbackBlockModule ->
                                    feedbackBlockModule.addFeedbackBlockListener((FeedbackBlockListener) blockListener));
                } else {
                    getBlockModule(device, getBusAddressIdentifier(entry.getKey().getBlockFunction()))
                            .ifPresent(blockModule -> blockModule.addBlockListener(blockListener));
                }
            }
        }
    }

    public void removeListeners(Device device) throws DeviceAccessException {
        for (Map.Entry<TrackBlock, List<BlockListener>> entry : feedbackBlockListeners.entrySet()) {
            for (BlockListener blockListener : entry.getValue()) {
                if (blockListener instanceof FeedbackBlockListener) {
                    getFeedbackBlockModule(device, getBusAddressIdentifier(entry.getKey().getBlockFunction()))
                            .ifPresent(feedbackBlockModule ->
                                    feedbackBlockModule.removeFeedbackBlockListener((FeedbackBlockListener) blockListener));
                } else {
                    getBlockModule(device, getBusAddressIdentifier(entry.getKey().getBlockFunction()))
                            .ifPresent(blockModule -> blockModule.removeBlockListener(blockListener));
                }
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
            var adjustType = trackBlock.getDrivingLevelAdjustType();
            if (adjustType != DRIVING_LEVEL_ADJUST_TYPE.NONE) {
                if ((enterBlock && adjustType == DRIVING_LEVEL_ADJUST_TYPE.ENTER)
                        || (!enterBlock && adjustType == DRIVING_LEVEL_ADJUST_TYPE.EXIT)) {
                    var drivingLevelForTrain = forward ? trackBlock.getForwardTargetDrivingLevel()
                            : trackBlock.getBackwardTargetDrivingLevel();
                    if (drivingLevelForTrain != null) {
                        trainService.updateAutomaticDrivingLevel(train$.get(), drivingLevelForTrain);
                    }
                }
            }
        }
    }

}
