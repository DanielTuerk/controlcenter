package net.wbz.moba.controlcenter.web.server.web.editor.block;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import net.wbz.moba.controlcenter.web.server.EventBroadcaster;
import net.wbz.moba.controlcenter.web.server.web.scenario.ScenarioServiceImpl;
import net.wbz.moba.controlcenter.web.server.web.scenario.ScenarioStateListener;
import net.wbz.moba.controlcenter.web.server.web.train.TrainManager;
import net.wbz.moba.controlcenter.web.server.web.train.TrainServiceImpl;
import net.wbz.moba.controlcenter.web.server.web.viewer.TrackViewerServiceImpl;
import net.wbz.moba.controlcenter.web.shared.scenario.RouteBlock;
import net.wbz.moba.controlcenter.web.shared.scenario.Scenario;
import net.wbz.moba.controlcenter.web.shared.track.model.BusDataConfiguration;
import net.wbz.moba.controlcenter.web.shared.track.model.Signal;
import net.wbz.moba.controlcenter.web.shared.track.model.Signal.FUNCTION;
import net.wbz.moba.controlcenter.web.shared.track.model.Signal.TYPE;
import net.wbz.moba.controlcenter.web.shared.train.Train;
import net.wbz.moba.controlcenter.web.shared.viewer.TrackViewerService;
import net.wbz.selectrix4java.block.FeedbackBlockListener;
import net.wbz.selectrix4java.block.FeedbackBlockModule;
import net.wbz.selectrix4java.device.Device;
import net.wbz.selectrix4java.device.DeviceAccessException;

/**
 * <p>
 * Block registry for the {@link SignalBlock}s.
 * Register listeners for the blocks of the {@link SignalBlock} to update the actual state of the {@link SignalBlock}.
 * {@link Train} entering or exiting and occupied state of blocks are detected.
 * </p>
 * <p>
 * The registry reacts to free montioring blocks of the {@link SignalBlock} and will start waiting {@link Train}s.
 * </p>
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
     * Mapping the future which runs the {@link FreeBlockTask} to the the same monitoring blocks.
     * Detect which signal has a running request and don't start tasks for all other signals with the same monitoring
     * block.
     */
    private final Map<BusDataConfiguration, Future<?>> monitoringBlockFuture = Maps.newConcurrentMap();

    /**
     * Executor to start waiting train on {@link SignalBlock} for freed track of monitoring block.
     */
    private final ExecutorService taskExecutor;

    /**
     * Service to control the {@link Signal}s.
     */
    private final TrackViewerService trackViewerService;

    /**
     * Service to update the track for running scenarios.
     */
    private final ScenarioServiceImpl scenarioService;

    @Inject
    public SignalBlockRegistry(EventBroadcaster eventBroadcaster, TrainServiceImpl trainService,
            TrainManager trainManager,
            TrackViewerServiceImpl trackViewerService, ScenarioServiceImpl scenarioService) {
        super(eventBroadcaster, trainService, trainManager);
        this.trackViewerService = trackViewerService;
        this.scenarioService = scenarioService;

        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("signal-block-registry-%d").build();
        // TODO shutdown
        taskExecutor = Executors.newSingleThreadExecutor(namedThreadFactory);

        scenarioService.addScenarioStateListener(new ScenarioStateListener() {
            @Override
            public void scenarioStarted(Scenario scenario) {
                Optional<RouteBlock> firstRouteBlock = scenario.getFirstRouteBlock();
                if (firstRouteBlock.isPresent()
                        && firstRouteBlock.get().getStartPoint().getType() == TYPE.EXIT) {
                    requestDriveForTrainOnExitSignal(scenario.getTrain(), firstRouteBlock.get().getStartPoint());
                }
            }
        });
    }

    @Override
    protected void doInit(Collection<Signal> signals) {
        log.debug("init signal blocks");
        feedbackBlockListeners.clear();

        for (final Signal signal : signals) {

            if (checkBlockFunction(signal.getMonitoringBlock()) && checkBlockFunction(signal.getStopBlock())) {

                SignalBlock signalBlock = new SignalBlock(signal);
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
                        SignalBlock signalBlock = getSignalBlock();
                        log.debug("track clear: signalBlock {}", signalBlock);
                        if (signalBlock.getWaitingTrain() != null) {
                            // start a waiting train in the signal stop block
                            startWaitingTrainForFreeTrack(signalBlock);
                        } else {
                            // set the signal to free for next trains entering which check this state
                            signalBlock.setMonitoringBlockFree(true);
                        }
                    }
                });

                if (signal.getType() != TYPE.EXIT) {
                    // all other signals handle the states of incoming trains

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
                                requestDriveForEnteringSignalBlock(getSignalBlock());
                            }
                        });
                    }
                } else {
                    /*
                     * Exit signals getting the waiting train from the scenario and only have to clear the waiting train
                     * in case of leaving the stop block.
                     */
                    addFeedbackBlockListener(new ExitSignalStopBlockListener(signalBlock, getTrainManager(),
                            getTrainService()));

                }
            }
        }
    }

    /**
     * <p>
     * Request to drive for the train which is waiting for the signal of type {@link Signal.TYPE#EXIT}.
     * It will start immediately if the signals monitoring block is free. Otherwise the signal will delay the start of
     * the waiting train until the track is freed.
     * </p>
     * <p>
     * <b>It's only for waiting trains on exit signal because this train could be in a scenario which have own trigger
     * to start the waiting train. All other signal types are handled as block signal which will start the train
     * immediately for a free track.</b>
     * </p>
     * 
     * @param train {@link Train} to start
     * @param signal {@link Signal} of type EXIT for the train
     */
    public synchronized void requestDriveForTrainOnExitSignal(Train train, Signal signal) {
        Preconditions.checkNotNull(signal);
        Preconditions.checkState(signal.getType() == TYPE.EXIT, "signal not of type EXIT");

        log.debug("request drive on exit signal {} for train: {}", signal, train.getName());
        SignalBlock signalBlock = getSignalBlockBySignal(signal);
        if (signalBlock != null) {
            getTrainService().toggleLight(train.getId(), true);
            // set the train as waiting train in the stop block of the signal
            signalBlock.setWaitingTrain(train);
            if (signalBlock.isMonitoringBlockFree()) {
                // start a waiting train in the signal stop block
                startWaitingTrainForFreeTrack(signalBlock);
                // otherwise the signal will start the train when the monitoring block is freed
            }
        } else {
            log.error("no signal block found to request drive on exit signal for signal: {}", signal);
        }
    }

    private SignalBlock getSignalBlockBySignal(Signal signal) {
        for (List<SignalBlock> signalBlocks : monitoringBlockSignals.values()) {
            for (SignalBlock signalBlock : signalBlocks) {
                if (signalBlock.getSignal().equals(signal)) {
                    return signalBlock;
                }
            }
        }
        return null;
    }

    private synchronized void startWaitingTrainForFreeTrack(SignalBlock signalBlock) {
        log.debug("start waiting train for signalBlock: {}", signalBlock);

        BusDataConfiguration monitoringBlockFunctionOfSignalBlock = signalBlock.getSignal().getMonitoringBlock()
                .getBlockFunction();

        // check for running requests to start train
        if (monitoringBlockFuture.containsKey(monitoringBlockFunctionOfSignalBlock)) {
            Future<?> future = monitoringBlockFuture.get(monitoringBlockFunctionOfSignalBlock);
            if (future != null && !future.isDone()) {
                // another signal block has submit a task for the same monitoring block
                return;
            }
        }

        Iterator<SignalBlock> iterator = getSignalBlocksWithWaitingTrains(monitoringBlockFunctionOfSignalBlock)
                .iterator();
        // start the first train that is waiting
        if (iterator.hasNext()) {
            SignalBlock next = iterator.next();
            if (next != null) {
                int startDrivingLevel = FreeBlockTask.DRIVING_LEVEL_START;
                // search route, and update track
                Optional<Scenario> scenario = scenarioService.getRunningScenarioOfTrain(signalBlock
                        .getWaitingTrain());
                if (scenario.isPresent()) {
                    // TODO allocate route or track necessary?
                    scenarioService.updateTrack(scenario.get(), next.getSignal());
                    if (scenario.get().getStartDrivingLevel() != null) {
                        startDrivingLevel = scenario.get().getStartDrivingLevel();
                    }
                }
                // TODO wie ohne route vorgehen?
                // TODO hat der monirequest drive on exit signal
                Future<Void> future = taskExecutor
                        .submit(new FreeBlockTask(next, getTrainService(), trackViewerService, startDrivingLevel));
                monitoringBlockFuture.put(monitoringBlockFunctionOfSignalBlock, future);
            }
        }
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
                // return input != null && input.getWaitingTrain() != null && !input.isMonitoringBlockFree();
                // TODO free monitoring check removed, is that still working for block signals?
                return input != null && input.getWaitingTrain() != null && input.getTrainInStopBlock() != null; // TODO
                                                                                                                // verify
                                                                                                                // getTrainInStopBlock
            }
        });
    }

}
