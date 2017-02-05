package net.wbz.moba.controlcenter.web.server.web.editor;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import net.wbz.moba.controlcenter.web.server.web.viewer.TrackViewerServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import net.wbz.moba.controlcenter.web.server.EventBroadcaster;
import net.wbz.moba.controlcenter.web.server.web.train.TrainManager;
import net.wbz.moba.controlcenter.web.server.web.train.TrainServiceImpl;
import net.wbz.moba.controlcenter.web.shared.track.model.BusDataConfiguration;
import net.wbz.moba.controlcenter.web.shared.track.model.Signal;
import net.wbz.moba.controlcenter.web.shared.track.model.Signal.FUNCTION;
import net.wbz.moba.controlcenter.web.shared.train.Train;
import net.wbz.moba.controlcenter.web.shared.viewer.TrackViewerService;
import net.wbz.selectrix4java.block.FeedbackBlockListener;
import net.wbz.selectrix4java.device.Device;
import net.wbz.selectrix4java.device.DeviceAccessException;

/**
 * TODO alles abhänig vom rail voltage machen? derzeit connected device
 * 
 * @author Daniel Tuerk
 */
@Singleton
class SignalBlockRegistry extends AbstractBlockRegistry<Signal> {

    private static final Logger log = LoggerFactory.getLogger(SignalBlockRegistry.class);
    public static final int DRIVING_LEVEL_STOP = 0;
    public static final int DRIVING_LEVEL_START = 4;

    private final Map<BusAddressIdentifier, List<FeedbackBlockListener>> feedbackBlockListeners =
            Maps.newConcurrentMap();

    private final Map<BusDataConfiguration, List<SignalBlock>> monitoringBlockSignals = Maps.newConcurrentMap();

    private final List<SignalBlock> signalBlocks = Lists.newArrayList();

    private final ExecutorService taskExecutor;

    private final TrackViewerService trackViewerService;

    @Inject
    SignalBlockRegistry(EventBroadcaster eventBroadcaster, TrainServiceImpl trainService, TrainManager trainManager,
        TrackViewerServiceImpl trackViewerService) {
        super(eventBroadcaster, trainService, trainManager);
        this.trackViewerService = trackViewerService;

        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("signal-block-registry-%d").build();
        // TODO shutdown
        taskExecutor = Executors.newSingleThreadExecutor(namedThreadFactory);
    }

    @Override
    protected void doInit(Collection<Signal> signals) {
        feedbackBlockListeners.clear();

        log.debug("init signal blocks");
        for (final Signal signal : signals) {
            if (signal.getMonitoringBlock() != null && signal.getStopBlock() != null) {

                final BusDataConfiguration monitoringBlockFunction = signal.getMonitoringBlock().getBlockFunction();
                if (checkBlockFunction(monitoringBlockFunction)
                        && checkBlockFunction(signal.getStopBlock().getBlockFunction())) {

                    final SignalBlock signalBlock = new SignalBlock(signal);
                    signalBlocks.add(signalBlock);

                    if (!monitoringBlockSignals.containsKey(monitoringBlockFunction)) {
                        monitoringBlockSignals.put(monitoringBlockFunction, Lists.<SignalBlock> newArrayList());
                    }
                    monitoringBlockSignals.get(monitoringBlockFunction).add(signalBlock);

                    // monitoring block
                    addFeedbackBlockListener(monitoringBlockFunction,
                            new FeedbackBlockListener() {
                                @Override
                                public void trainEnterBlock(int blockNumber, int trainAddress, boolean forward) {

                                    // remove waiting train from block which start the train
                                    if (blockNumber == monitoringBlockFunction.getBit()) {
                                        if (signalBlock.getWaitingTrain() != null && signalBlock.getWaitingTrain()
                                                .getAddress() == trainAddress) {
                                            signalBlock.setWaitingTrain(null);
                                        }
                                    }

                                }

                                @Override
                                public void trainLeaveBlock(int blockNumber, int trainAddress, boolean forward) {
                                }

                                @Override
                                public void blockOccupied(int blockNr) {
                                    if (blockNr == monitoringBlockFunction.getBit()) {
                                        signalBlock.setMonitoringBlockFree(false);
                                        trackViewerService.switchSignal(signalBlock.getSignal(), FUNCTION.HP0);
                                    }
                                }

                                @Override
                                public void blockFreed(int blockNr) {
                                    if (blockNr == monitoringBlockFunction.getBit()) {

                                        SignalBlock next = getNextWaitingTrain(monitoringBlockFunction);
                                        if (next != null) {
                                            // TODO search route, apply, allocate
                                            // TODO wie ohne route vorgehen?
                                            // TODO hat der monitoring block abhänigkeiten?
                                            next.setMonitoringBlockFree(true);


//                                            try {
                                                taskExecutor.submit(new FreeBlockTask(signalBlock, getTrainService(),
                                                        trackViewerService));
//                                            } catch (InterruptedException | ExecutionException e) {
//                                                e.printStackTrace();
//                                            }
                                        }
                                    }
                                }
                            });
                    // stop block
                    addFeedbackBlockListener(signal.getStopBlock().getBlockFunction(),
                            new FeedbackBlockListener() {
                                @Override
                                public void trainEnterBlock(int blockNumber, int trainAddress, boolean forward) {
                                    if (!signalBlock.isMonitoringBlockFree() && blockNumber == monitoringBlockFunction
                                            .getBit()) {
                                        Train train = getTrainManager().getTrain(trainAddress);
                                        if (train != null) {
                                            signalBlock.setWaitingTrain(train);
                                            getTrainService().updateDrivingLevel(train.getId(), DRIVING_LEVEL_STOP);
                                        }
                                    }
                                }

                                @Override
                                public void trainLeaveBlock(int blockNumber, int trainAddress, boolean forward) {
                                    if (blockNumber == monitoringBlockFunction.getBit()) {
                                        // signalBlock.setWaitingTrain(null);
                                        // Train train = signalBlock.getWaitingTrain();
                                        // if (train != null) {
                                        // getTrainService().updateDrivingLevel(train.getId(), DRIVING_LEVEL_START);
                                        // }
                                    }
                                }

                                @Override
                                public void blockOccupied(int blockNr) {
                                    if (blockNr == monitoringBlockFunction.getBit()) {
                                        // signal.setMonitoringBlockFree(false);
                                    }
                                }

                                @Override
                                public void blockFreed(int blockNr) {
                                    if (blockNr == monitoringBlockFunction.getBit()) {
                                        // signal.setMonitoringBlockFree(true);
                                    }
                                }
                            });

                    // TODO breaking block
                    // TODO entering block
                }
            }
        }
    }

    private SignalBlock getNextWaitingTrain(BusDataConfiguration monitoringBlockFunction) {
        for (SignalBlock signalBlock : monitoringBlockSignals.get(monitoringBlockFunction)) {
            if (!signalBlock.isMonitoringBlockFree()) {
                return signalBlock;
            }
        }
        return null;
    }

    @Override
    void registerListeners(Device device) throws DeviceAccessException {
        for (Map.Entry<BusAddressIdentifier, List<FeedbackBlockListener>> entry : feedbackBlockListeners
                .entrySet()) {
            for (FeedbackBlockListener feedbackBlockListener : entry.getValue()) {
                getFeedbackBlockModule(device, entry.getKey()).addFeedbackBlockListener(feedbackBlockListener);
            }
        }
    }

    @Override
    void removeListeners(Device device) throws DeviceAccessException {
        for (Map.Entry<BusAddressIdentifier, List<FeedbackBlockListener>> entry : feedbackBlockListeners
                .entrySet()) {
            for (FeedbackBlockListener feedbackBlockListener : entry.getValue()) {
                getFeedbackBlockModule(device, entry.getKey()).removeFeedbackBlockListener(feedbackBlockListener);
            }
        }
    }

    protected void addFeedbackBlockListener(BusDataConfiguration blockFunction,
            FeedbackBlockListener feedbackBlockListener) {
        final BusAddressIdentifier busAddressIdentifier = getBusAddressIdentifier(blockFunction);
        if (!feedbackBlockListeners.containsKey(busAddressIdentifier)) {
            feedbackBlockListeners.put(busAddressIdentifier, Lists.<FeedbackBlockListener> newArrayList());
        }
        feedbackBlockListeners.get(busAddressIdentifier).add(feedbackBlockListener);
    }

}
