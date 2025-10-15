package net.wbz.moba.controlcenter.web.server.web.editor.block;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import net.wbz.moba.controlcenter.web.server.event.EventBroadcaster;
import net.wbz.moba.controlcenter.web.server.web.train.TrainManager;
import net.wbz.moba.controlcenter.web.server.web.train.TrainService;
import net.wbz.moba.controlcenter.web.server.web.viewer.TrackViewerService;
import net.wbz.moba.controlcenter.shared.track.model.BusDataConfiguration;
import net.wbz.moba.controlcenter.shared.track.model.Signal;
import net.wbz.moba.controlcenter.shared.train.Train;
import net.wbz.selectrix4java.block.FeedbackBlockListener;
import net.wbz.selectrix4java.block.FeedbackBlockModule;
import net.wbz.selectrix4java.device.Device;
import net.wbz.selectrix4java.device.DeviceAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Block registry for the {@link SignalBlock}s. Register listeners for the blocks of the {@link SignalBlock} to update
 * the actual state of the {@link SignalBlock}. {@link Train} entering or exiting and occupied state of blocks are
 * detected.
 * </p>
 * <p>
 * The registry reacts to free monitoring blocks of the {@link SignalBlock} and will start waiting {@link Train}s.
 * </p>
 * TODO wie mit re-register umgehen?
 *
 * @author Daniel Tuerk
 */
@Singleton
public class SignalBlockRegistry extends AbstractBlockRegistry<Signal> {

    private static final Logger log = LoggerFactory.getLogger(SignalBlockRegistry.class);

    /**
     * The unique {@link BusAddressIdentifier}s for the {@link FeedbackBlockModule}s which have to register the {@link
     * AbstractSignalBlockListener}s by connected device.
     */
    private final Map<BusAddressIdentifier, List<AbstractSignalBlockListener>> feedbackBlockListeners = Maps
        .newConcurrentMap();

    /**
     * Mapping for all {@link SignalBlock}s which have the same monitoring blocks.
     */
    private final Map<BusDataConfiguration, List<SignalBlock>> monitoringBlockSignals = Maps.newConcurrentMap();

    /**
     * Service to control the {@link Signal}s.
     */
    private final TrackViewerService trackViewerService;

    private Collection<Signal> signals;

    @Inject
    public SignalBlockRegistry(EventBroadcaster eventBroadcaster, TrainService trainService,
        TrainManager trainManager, TrackViewerService trackViewerService) {
        super(eventBroadcaster, trainService, trainManager);
        this.trackViewerService = trackViewerService;
    }

    public Collection<Signal> getSignals() {
        return signals;
    }

    @Override
    protected void doInit(Collection<Signal> signals) {
        log.debug("init signal blocks");
        feedbackBlockListeners.clear();

        this.signals = signals;
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
                    monitoringBlockSignals.put(monitoringBlockFunction, new ArrayList<>());
                }
                monitoringBlockSignals.get(monitoringBlockFunction).add(signalBlock);

                // monitoring block
                addFeedbackBlockListener(
                    new SignalMonitoringBlockListener(signalBlock, trackViewerService, getTrainManager()));

                // TODO disabled until needed, but also wrong behaviors for train feedback
                // /*
                // * Only react for breaking and stop only be non exit signals.
                // */
                // if (signal.getType() != TYPE.EXIT) {
                // // stop block
                // addFeedbackBlockListener(new SignalStopBlockListener(signalBlock, getTrainManager(),
                // getTrainService()));
                // // breaking block
                // if (checkBlockFunction(signal.getBreakingBlock())) {
                // addFeedbackBlockListener(new SignalBreakingBlockListener(signalBlock, getTrainManager()));
                // }
                // // entering block
                // if (checkBlockFunction(signal.getEnteringBlock())) {
                // addFeedbackBlockListener(new SignalEnteringBlockListener(signalBlock, getTrainManager()));
                // }
                // }
            }
        }
    }

    @Override
    public void registerListeners(Device device) throws DeviceAccessException {
        for (Map.Entry<BusAddressIdentifier, List<AbstractSignalBlockListener>> entry : feedbackBlockListeners
            .entrySet()) {
            for (FeedbackBlockListener feedbackBlockListener : entry.getValue()) {
                FeedbackBlockModule feedbackBlockModule = getFeedbackBlockModule(device, entry.getKey());
                log.debug("Feedback Module ({}): register listener {}", feedbackBlockModule, feedbackBlockListener);
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

    private void addFeedbackBlockListener(AbstractSignalBlockListener signalBlockListener) {
        final BusAddressIdentifier busAddressIdentifier = getBusAddressIdentifier(
            signalBlockListener.getTrackBlock().getBlockFunction());
        if (!feedbackBlockListeners.containsKey(busAddressIdentifier)) {
            feedbackBlockListeners.put(busAddressIdentifier, new ArrayList<>());
        }
        feedbackBlockListeners.get(busAddressIdentifier).add(signalBlockListener);
    }

}
