package net.wbz.moba.controlcenter.web.server.web.editor;

import java.util.Collection;
import java.util.Map;

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
 *
 * @author Daniel Tuerk
 */
@Singleton
class TrackBlockRegistry extends AbstractBlockRegistry<TrackBlock> {

    private static final Logger log = LoggerFactory.getLogger(TrackBlockRegistry.class);

    private final Map<BusAddressIdentifier, FeedbackBlockListener> feedbackBlockListeners =
            Maps.newConcurrentMap();

    @Inject
    TrackBlockRegistry(EventBroadcaster eventBroadcaster, TrainServiceImpl trainService, TrainManager trainManager) {
        super(eventBroadcaster, trainService, trainManager);
    }

    @Override
    protected void doInit(Collection<TrackBlock> trackBlocks) {
        feedbackBlockListeners.clear();
        log.debug("init track blocks");
        for (final TrackBlock trackBlock : trackBlocks) {

            final BusDataConfiguration blockFunction = trackBlock.getBlockFunction();

            if (checkBlockFunction(blockFunction)) {
                addFeedbackBlockListener(blockFunction, new FeedbackBlockListener() {
                    @Override
                    public void trainEnterBlock(int blockNumber, int trainAddress, boolean forward) {
                        handleTrainOnBlock(true, blockNumber, trainAddress, forward, trackBlock, blockFunction);
                    }

                    @Override
                    public void trainLeaveBlock(int blockNumber, int trainAddress, boolean forward) {
                        handleTrainOnBlock(false, blockNumber, trainAddress, forward, trackBlock, blockFunction);
                    }

                    @Override
                    public void blockOccupied(int blockNr) {
                        fireBlockEvent(true, blockNr);
                    }

                    @Override
                    public void blockFreed(int blockNr) {
                        fireBlockEvent(false, blockNr);
                    }

                    private void fireBlockEvent(boolean bitState, int blockNr) {
                        getEventBroadcaster().fireEvent(new TrackPartBlockEvent(new BusDataConfiguration(
                                blockFunction.getBus(),
                                blockFunction.getAddress(),
                                blockNr,
                                bitState), bitState ? TrackPartBlockEvent.STATE.USED
                                        : TrackPartBlockEvent.STATE.FREE));
                    }
                });
            }
        }
    }

    protected void addFeedbackBlockListener(BusDataConfiguration blockFunction,
            FeedbackBlockListener feedbackBlockListener) {
        final BusAddressIdentifier busAddressIdentifier = getBusAddressIdentifier(blockFunction);
        if (!feedbackBlockListeners.containsKey(busAddressIdentifier)) {
            feedbackBlockListeners.put(busAddressIdentifier, feedbackBlockListener);
        }
    }

    @Override
    void registerListeners(Device device) throws DeviceAccessException {
        for (Map.Entry<BusAddressIdentifier, FeedbackBlockListener> entry : feedbackBlockListeners
                .entrySet()) {
            getFeedbackBlockModule(device, entry.getKey()).addFeedbackBlockListener(entry.getValue());
        }
    }

    @Override
    void removeListeners(Device device) throws DeviceAccessException {
        for (Map.Entry<BusAddressIdentifier, FeedbackBlockListener> entry : feedbackBlockListeners
                .entrySet()) {
            getFeedbackBlockModule(device, entry.getKey()).removeFeedbackBlockListener(entry.getValue());
        }
    }

    private void handleTrainOnBlock(boolean enterBlock, int blockNumber, int trainAddress, boolean forward,
            TrackBlock trackBlock, BusDataConfiguration blockFunction) {
        DRIVING_LEVEL_ADJUST_TYPE adjustType = trackBlock.getDrivingLevelAdjustType();
        if (adjustType != DRIVING_LEVEL_ADJUST_TYPE.NONE) {
            Train train = getTrainManager().getTrain(trainAddress);
            if (train != null) {
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
        getEventBroadcaster().fireEvent(new FeedbackBlockEvent(
                enterBlock ? FeedbackBlockEvent.STATE.ENTER : FeedbackBlockEvent.STATE.EXIT,
                blockFunction.getBus(),
                blockFunction.getAddress(),
                blockNumber, trainAddress, forward));
    }

}
