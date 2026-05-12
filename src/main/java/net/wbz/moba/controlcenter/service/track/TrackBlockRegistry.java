package net.wbz.moba.controlcenter.service.track;

import io.vertx.core.impl.ConcurrentHashSet;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import net.wbz.moba.controlcenter.BusAddressIdentifier;
import net.wbz.moba.controlcenter.EventBroadcaster;
import net.wbz.moba.controlcenter.SelectrixHelper;
import net.wbz.moba.controlcenter.service.constrution.ConstructionService;
import net.wbz.moba.controlcenter.service.train.TrainManager;
import net.wbz.moba.controlcenter.service.train.TrainService;
import net.wbz.moba.controlcenter.shared.bus.FeedbackBlockEvent;
import net.wbz.moba.controlcenter.shared.track.model.BusDataConfiguration;
import net.wbz.moba.controlcenter.shared.track.model.TrackBlock;
import net.wbz.moba.controlcenter.shared.track.model.TrackBlock.DRIVING_LEVEL_ADJUST_TYPE;
import net.wbz.moba.controlcenter.shared.viewer.TrackPartBlockEvent;
import net.wbz.selectrix4java.block.FeedbackBlockListener;
import net.wbz.selectrix4java.block.FeedbackBlockModule;
import net.wbz.selectrix4java.device.Device;
import net.wbz.selectrix4java.device.DeviceAccessException;
import org.jboss.logging.Logger;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Registry for available {@link TrackBlock}s to add the {@link FeedbackBlockListener}s for receiving the block states
 * and to adjust driving levels of trains entering or exiting the blocks.
 * Also, the current position of the train is observed in {@link #trackBlocks}.
 * 
 * @author Daniel Tuerk
 */
@Singleton
public class TrackBlockRegistry {

    private static final Logger log = Logger.getLogger(TrackBlockRegistry.class);

    private final EventBroadcaster eventBroadcaster;
    private final TrainService trainService;
    private final TrainManager trainManager;

    private final Map<TrackBlock, FeedbackBlockListener> feedbackBlockListeners = new ConcurrentHashMap<>();
    private Collection<TrackBlock> trackBlocks;

    private final Map<TrackBlock, Set<Long>> trainsOnBlocks = new ConcurrentHashMap<>();

    @Inject
    public TrackBlockRegistry(EventBroadcaster eventBroadcaster, TrainService trainService,
        TrainManager trainManager, ConstructionService constructionService, TrackBlockManager trackBlockManager) {
        this.eventBroadcaster = eventBroadcaster;
        this.trainService = trainService;
        this.trainManager = trainManager;

        constructionService.addListener(construction ->
            initBlocks(trackBlockManager.fetch(construction.getId())));
    }

    public Collection<TrackBlock> getTrackBlocks() {
        return trackBlocks;
    }

    private void addFeedbackBlockListener(TrackBlock trackBlock,
        FeedbackBlockListener feedbackBlockListener) {
        if (!feedbackBlockListeners.containsKey(trackBlock)) {
            feedbackBlockListeners.put(trackBlock, feedbackBlockListener);
        }
    }

    private void initBlocks(Collection<TrackBlock> trackBlocks) {
        log.debug("init track blocks");
        this.trackBlocks = trackBlocks;
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
                            bitState ? TrackPartBlockEvent.STATE.USED : TrackPartBlockEvent.STATE.FREE));
                    }
                });
            }
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
            log.warnf("no train with address '%d' found for block number: %d (%s)",
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
