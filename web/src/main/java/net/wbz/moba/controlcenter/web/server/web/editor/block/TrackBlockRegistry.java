package net.wbz.moba.controlcenter.web.server.web.editor.block;

import java.util.Collection;
import java.util.Map;

import net.wbz.selectrix4java.block.FeedbackBlockModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import net.wbz.moba.controlcenter.web.server.EventBroadcaster;
import net.wbz.moba.controlcenter.web.server.web.train.TrainManager;
import net.wbz.moba.controlcenter.web.server.web.train.TrainServiceImpl;
import net.wbz.moba.controlcenter.web.shared.bus.FeedbackBlockEvent;
import net.wbz.moba.controlcenter.web.shared.track.model.BusDataConfiguration;
import net.wbz.moba.controlcenter.web.shared.track.model.TrackBlock;
import net.wbz.moba.controlcenter.web.shared.track.model.TrackBlock.DRIVING_LEVEL_ADJUST_TYPE;
import net.wbz.moba.controlcenter.web.shared.train.Train;
import net.wbz.moba.controlcenter.web.shared.viewer.TrackPartBlockEvent;
import net.wbz.selectrix4java.block.FeedbackBlockListener;
import net.wbz.selectrix4java.device.Device;
import net.wbz.selectrix4java.device.DeviceAccessException;

/**
 * Registry for available {@link TrackBlock}s to add the {@link FeedbackBlockListener}s for receiving the block states
 * and to adjust driving levels of trains entering or exiting the blocks.
 * Also the current position of the train is set.
 * 
 * @see Train#currentBlocks
 * @author Daniel Tuerk
 */
@Singleton
public class TrackBlockRegistry extends AbstractBlockRegistry<TrackBlock> {

    private static final Logger log = LoggerFactory.getLogger(TrackBlockRegistry.class);

    private final Map<TrackBlock, FeedbackBlockListener> feedbackBlockListeners =
            Maps.newConcurrentMap();

    @Inject
    public TrackBlockRegistry(EventBroadcaster eventBroadcaster, TrainServiceImpl trainService,
            TrainManager trainManager) {
        super(eventBroadcaster, trainService, trainManager);
    }

    @Override
    protected void doInit(Collection<TrackBlock> trackBlocks) {
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
                        getEventBroadcaster().fireEvent(new TrackPartBlockEvent(blockFunction,
                                bitState ? TrackPartBlockEvent.STATE.USED : TrackPartBlockEvent.STATE.FREE));
                    }
                });
            }
        }
    }

    protected void addFeedbackBlockListener(TrackBlock trackBlock,
            FeedbackBlockListener feedbackBlockListener) {
        if (!feedbackBlockListeners.containsKey(trackBlock)) {
            feedbackBlockListeners.put(trackBlock, feedbackBlockListener);
        }
    }

    @Override
    public void registerListeners(Device device) throws DeviceAccessException {
        for (Map.Entry<TrackBlock, FeedbackBlockListener> entry : feedbackBlockListeners.entrySet()) {
            FeedbackBlockModule feedbackBlockModule = getFeedbackBlockModule(device,
                    getBusAddressIdentifier(entry.getKey().getBlockFunction()));
            feedbackBlockModule.addFeedbackBlockListener(entry.getValue());
        }
    }

    @Override
    public void removeListeners(Device device) throws DeviceAccessException {
        for (Map.Entry<TrackBlock, FeedbackBlockListener> entry : feedbackBlockListeners.entrySet()) {
            getFeedbackBlockModule(device, getBusAddressIdentifier(entry.getKey().getBlockFunction()))
                    .removeFeedbackBlockListener(entry.getValue());
        }
    }

    private void handleTrainOnBlock(boolean enterBlock, int blockNumber, int trainAddress, boolean forward,
            TrackBlock trackBlock) {
        // update current block of the train
        Train train = getTrainManager().getTrain(trainAddress);
        if (train != null) {
            if (enterBlock) {
                train.addCurrentBlock(trackBlock);
            } else {
                train.removeCurrentBlock(trackBlock);
            }
            // update automatic driving level
            DRIVING_LEVEL_ADJUST_TYPE adjustType = trackBlock.getDrivingLevelAdjustType();
            if (adjustType != DRIVING_LEVEL_ADJUST_TYPE.NONE) {
                if ((enterBlock && adjustType == DRIVING_LEVEL_ADJUST_TYPE.ENTER)
                        || (!enterBlock && adjustType == DRIVING_LEVEL_ADJUST_TYPE.EXIT)) {
                    Integer drivingLevelForTrain = forward ? trackBlock.getForwardTargetDrivingLevel()
                            : trackBlock.getBackwardTargetDrivingLevel();
                    if (drivingLevelForTrain != null) {
                        getTrainService().updateAutomaticDrivingLevel(train, drivingLevelForTrain);
                    }
                }
            }
        }
        // fire position event of train to clients
        getEventBroadcaster().fireEvent(new FeedbackBlockEvent(
                enterBlock ? FeedbackBlockEvent.STATE.ENTER : FeedbackBlockEvent.STATE.EXIT,
                trackBlock.getBlockFunction().getBus(),
                trackBlock.getBlockFunction().getAddress(),
                blockNumber, trainAddress, forward));
    }

    public void addFeedbackListener(Device device, TrackBlock trackBlock, FeedbackBlockListener listener) {
        try {
            getFeedbackBlockModule(device, getBusAddressIdentifier(trackBlock.getBlockFunction()))
                    .addFeedbackBlockListener(listener);
        } catch (DeviceAccessException e) {
            log.error("can't add feedback listener");
        }
    }

    public void removeFeedbackListener(Device device, TrackBlock trackBlock, FeedbackBlockListener listener) {
        try {
            getFeedbackBlockModule(device, getBusAddressIdentifier(trackBlock.getBlockFunction()))
                    .removeFeedbackBlockListener(listener);
        } catch (DeviceAccessException e) {
            log.error("can't add feedback listener");
        }
    }

}
