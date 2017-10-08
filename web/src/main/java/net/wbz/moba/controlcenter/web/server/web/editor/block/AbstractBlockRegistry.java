package net.wbz.moba.controlcenter.web.server.web.editor.block;

import java.util.Collection;

import net.wbz.moba.controlcenter.web.server.SelectrixHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.wbz.moba.controlcenter.web.server.EventBroadcaster;
import net.wbz.moba.controlcenter.web.server.web.train.TrainManager;
import net.wbz.moba.controlcenter.web.server.web.train.TrainServiceImpl;
import net.wbz.moba.controlcenter.web.shared.track.model.AbstractDto;
import net.wbz.moba.controlcenter.web.shared.track.model.BusDataConfiguration;
import net.wbz.moba.controlcenter.web.shared.track.model.TrackBlock;
import net.wbz.selectrix4java.block.FeedbackBlockModule;
import net.wbz.selectrix4java.device.Device;
import net.wbz.selectrix4java.device.DeviceAccessException;

/**
 * Abstract registry for blocks.
 * 
 * @author Daniel Tuerk
 */
public abstract class AbstractBlockRegistry<T extends AbstractDto> {

    private static final Logger log = LoggerFactory.getLogger(AbstractBlockRegistry.class);

    private final EventBroadcaster eventBroadcaster;
    private final TrainServiceImpl trainService;
    private final TrainManager trainManager;

    /**
     * Create new registry.
     *
     * @param eventBroadcaster {@link EventBroadcaster}
     * @param trainService {@link net.wbz.moba.controlcenter.web.shared.train.TrainService}
     * @param trainManager {@link TrainManager}
     */
    public AbstractBlockRegistry(EventBroadcaster eventBroadcaster, TrainServiceImpl trainService,
            TrainManager trainManager) {
        this.eventBroadcaster = eventBroadcaster;
        this.trainService = trainService;
        this.trainManager = trainManager;
    }

    /**
     * Register the given {@link T}s
     *
     * @param trackBlocks blocks to register
     */
    protected abstract void doInit(Collection<T> trackBlocks);

    /**
     * Register the listeners of the registry to the given {@link Device}.
     *
     * @param device {@link Device}
     * @throws DeviceAccessException
     */
    public abstract void registerListeners(Device device) throws DeviceAccessException;

    /**
     * Remove all listeners of registry from the given {@link Device}.
     *
     * @param device {@link Device}
     * @throws DeviceAccessException
     */
    public abstract void removeListeners(Device device) throws DeviceAccessException;

    /**
     * Initialize the given blocks.
     *
     * @param blocks collection of {@link T} to initialize
     */
    public void initBlocks(final Collection<T> blocks) {
        log.info("init blocks");

        doInit(blocks);
    }

    protected FeedbackBlockModule getFeedbackBlockModule(Device device,
            BusAddressIdentifier entry) throws DeviceAccessException {
        return SelectrixHelper.getFeedbackBlockModule(device, entry);
    }

    protected boolean checkBlockFunction(TrackBlock trackBlock) {
        return trackBlock != null && trackBlock.getBlockFunction() != null && trackBlock.getBlockFunction().isValid();
    }

    protected BusAddressIdentifier getBusAddressIdentifier(BusDataConfiguration blockFunction) {
        return new BusAddressIdentifier(blockFunction);
    }

    protected EventBroadcaster getEventBroadcaster() {
        return eventBroadcaster;
    }

    protected TrainServiceImpl getTrainService() {
        return trainService;
    }

    protected TrainManager getTrainManager() {
        return trainManager;
    }

}
