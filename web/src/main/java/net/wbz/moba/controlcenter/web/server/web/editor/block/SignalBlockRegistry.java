package net.wbz.moba.controlcenter.web.server.web.editor.block;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import net.wbz.moba.controlcenter.web.server.EventBroadcaster;
import net.wbz.moba.controlcenter.web.server.web.train.TrainManager;
import net.wbz.moba.controlcenter.web.server.web.train.TrainServiceImpl;
import net.wbz.moba.controlcenter.web.server.web.viewer.TrackViewerServiceImpl;
import net.wbz.moba.controlcenter.web.shared.track.model.BusDataConfiguration;
import net.wbz.moba.controlcenter.web.shared.track.model.Signal;
import net.wbz.moba.controlcenter.web.shared.track.model.Signal.FUNCTION;
import net.wbz.moba.controlcenter.web.shared.viewer.TrackViewerService;
import net.wbz.selectrix4java.block.FeedbackBlockListener;
import net.wbz.selectrix4java.block.FeedbackBlockModule;
import net.wbz.selectrix4java.device.Device;
import net.wbz.selectrix4java.device.DeviceAccessException;

/**
 * TODO doc
 * TODO alles abhänig vom rail voltage machen? derzeit connected device
 * TODO wie mit re-register umgehen?
 * 
 * @author Daniel Tuerk
 */
@Singleton
public class SignalBlockRegistry extends AbstractBlockRegistry<Signal> {

    private static final Logger log = LoggerFactory.getLogger(SignalBlockRegistry.class);

    /**
     * The unique {@link BusAddressIdentifier}s for the {@link FeedbackBlockModule}s which have to register the
     * {@link AbstractSignalBlockListener}s by connected device.
     */
    private final Map<BusAddressIdentifier, List<AbstractSignalBlockListener>> feedbackBlockListeners =
            Maps.newConcurrentMap();

    /**
     * Mapping for all {@link SignalBlock}s which have the same monitoring blocks.
     */
    private final Map<BusDataConfiguration, List<SignalBlock>> monitoringBlockSignals = Maps.newConcurrentMap();

    /**
     * Executor to start waiting train on {@link SignalBlock} for freed track of monitoring block.
     */
    private final ExecutorService taskExecutor;

    /**
     * Service to control the {@link Signal}s.
     */
    private final TrackViewerService trackViewerService;

    @Inject
    public SignalBlockRegistry(EventBroadcaster eventBroadcaster, TrainServiceImpl trainService,
            TrainManager trainManager,
            TrackViewerServiceImpl trackViewerService) {
        super(eventBroadcaster, trainService, trainManager);
        this.trackViewerService = trackViewerService;

        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("signal-block-registry-%d").build();
        // TODO shutdown
        taskExecutor = Executors.newSingleThreadExecutor(namedThreadFactory);
    }

    @Override
    protected void doInit(Collection<Signal> signals) {
        log.debug("init signal blocks");
        feedbackBlockListeners.clear();

        for (final Signal signal : signals) {

            if (checkBlockFunction(signal.getMonitoringBlock()) && checkBlockFunction(signal.getStopBlock())) {

                final SignalBlock signalBlock = new SignalBlock(signal);
                log.debug("add signal block: {}", signalBlock);

                final BusDataConfiguration monitoringBlockFunction = signal.getMonitoringBlock().getBlockFunction();

                /*
                 * Store all signal blocks which have the same monitoring block to request drive from different stop
                 * blocks to equal monitoring block.
                 */
                if (!monitoringBlockSignals.containsKey(monitoringBlockFunction)) {
                    monitoringBlockSignals.put(monitoringBlockFunction, Lists.<SignalBlock> newArrayList());
                }
                monitoringBlockSignals.get(monitoringBlockFunction).add(signalBlock);

                // monitoring block
                addFeedbackBlockListener(new SignalMonitoringBlockListener(signalBlock, trackViewerService,
                        getTrainManager()) {
                    @Override
                    public void trackClear() {
                        startWaitingTrainForFreeTrack(signalBlock);
                    }
                });
                // stop block
                addFeedbackBlockListener(new SignalStopBlockListener(signalBlock, getTrainManager(),
                        getTrainService()));

                // breaking block
                if (checkBlockFunction(signal.getBreakingBlock())) {
                    addFeedbackBlockListener(new SignalBreakingBlockListener(signalBlock, getTrainManager()));
                }
                // entering block
                if (checkBlockFunction(signal.getEnteringBlock())) {
                    addFeedbackBlockListener(new SignalEnteringBlockListener(signalBlock, getTrainManager()) {
                        @Override
                        protected void requestFreeTrack() {
                            requestDriveForEnteringSignalBlock(signalBlock);
                        }
                    });
                }
            }
        }
    }

    private synchronized void startWaitingTrainForFreeTrack(SignalBlock signalBlock) {
        log.debug("start waiting train for signalBlock: {}", signalBlock);
        Iterator<SignalBlock> iterator = getSignalBlocksWithWaitingTrains(signalBlock.getSignal().getMonitoringBlock()
                .getBlockFunction())
                        .iterator();
        while (iterator.hasNext()) {
            SignalBlock next = iterator.next();
            if (next != null) {
                // TODO search route, apply, allocate
                // TODO wie ohne route vorgehen?
                // TODO hat der monitoring block abhänigkeiten?
                taskExecutor.submit(new FreeBlockTask(next, getTrainService(), trackViewerService));
                // stop only start one train and wait for the next clear track
                break;
            }
        }
        // clear
        signalBlock.setMonitoringBlockFree(true);
    }

    @Override
    public void registerListeners(Device device) throws DeviceAccessException {
        for (Map.Entry<BusAddressIdentifier, List<AbstractSignalBlockListener>> entry : feedbackBlockListeners
                .entrySet()) {
            for (FeedbackBlockListener feedbackBlockListener : entry.getValue()) {
                FeedbackBlockModule feedbackBlockModule = getFeedbackBlockModule(device, entry.getKey());
                log.debug("Feedback Module {}: register listener {}", feedbackBlockModule, feedbackBlockListener);
                feedbackBlockModule.addFeedbackBlockListener(feedbackBlockListener);
            }
        }
    }

    @Override
    public void removeListeners(Device device) throws DeviceAccessException {
        for (Map.Entry<BusAddressIdentifier, List<AbstractSignalBlockListener>> entry : feedbackBlockListeners
                .entrySet()) {
            for (FeedbackBlockListener feedbackBlockListener : entry.getValue()) {
                getFeedbackBlockModule(device, entry.getKey()).removeFeedbackBlockListener(feedbackBlockListener);
            }
        }
    }

    protected void addFeedbackBlockListener(AbstractSignalBlockListener signalBlockListener) {
        final BusAddressIdentifier busAddressIdentifier = getBusAddressIdentifier(signalBlockListener.getTrackBlock()
                .getBlockFunction());
        if (!feedbackBlockListeners.containsKey(busAddressIdentifier)) {
            feedbackBlockListeners.put(busAddressIdentifier, Lists.<AbstractSignalBlockListener> newArrayList());
        }
        feedbackBlockListeners.get(busAddressIdentifier).add(signalBlockListener);
    }

    /**
     * Request free drive by switching the {@link Signal} to {@link Signal.FUNCTION#HP1}.
     * All other {@link SignalBlock}s which have the same monitoring block updated to stop by
     * {@link SignalBlock#setMonitoringBlockFree(boolean)} to {@code false}.
     * Synchronized to avoid multiple request at same monitoring block for different entering blocks.
     *
     * @param signalBlock {@link SignalBlock} which should drive trough from entering train
     */
    private synchronized void requestDriveForEnteringSignalBlock(SignalBlock signalBlock) {
        if (signalBlock.isMonitoringBlockFree()) {
            log.debug("request free track for signalBlock: {}", signalBlock);
            BusDataConfiguration monitoringBlockFunction = signalBlock.getSignal().getMonitoringBlock()
                    .getBlockFunction();
            if (monitoringBlockSignals.containsKey(monitoringBlockFunction)) {
                for (SignalBlock signalBlockOfMonitoring : monitoringBlockSignals.get(
                        monitoringBlockFunction)) {
                    if (signalBlockOfMonitoring != signalBlock) {
                        // mark all others to stop the entering train and don't request free track
                        signalBlockOfMonitoring.setMonitoringBlockFree(false);
                    }
                }
            }
            trackViewerService.switchSignal(signalBlock.getSignal(), FUNCTION.HP1);
        }
    }

    /**
     * Load all {@link SignalBlock}s which have no free monitoring block and a waiting
     * {@link net.wbz.moba.controlcenter.web.shared.train.Train} on it.
     * 
     * @param monitoringBlockFunction {@link BusDataConfiguration}
     * @return {@link SignalBlock}s
     */
    private synchronized Collection<SignalBlock> getSignalBlocksWithWaitingTrains(
            BusDataConfiguration monitoringBlockFunction) {
        return Collections2.filter(monitoringBlockSignals.get(monitoringBlockFunction), new Predicate<SignalBlock>() {
            @Override
            public boolean apply(@Nullable SignalBlock input) {
                return input != null && input.getWaitingTrain() != null && !input.isMonitoringBlockFree();
            }
        });
    }

}
