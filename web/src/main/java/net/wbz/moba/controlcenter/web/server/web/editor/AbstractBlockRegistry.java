package net.wbz.moba.controlcenter.web.server.web.editor;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import net.wbz.moba.controlcenter.web.server.EventBroadcaster;
import net.wbz.moba.controlcenter.web.server.web.train.TrainManager;
import net.wbz.moba.controlcenter.web.server.web.train.TrainServiceImpl;
import net.wbz.moba.controlcenter.web.shared.track.model.BusDataConfiguration;
import net.wbz.selectrix4java.block.FeedbackBlockModule;
import net.wbz.selectrix4java.device.Device;
import net.wbz.selectrix4java.device.DeviceAccessException;

/**
 * TODO find name
 * 
 * @author Daniel Tuerk
 */
abstract class AbstractBlockRegistry<T> {

    private static final Logger log = LoggerFactory.getLogger(AbstractBlockRegistry.class);

    private final EventBroadcaster eventBroadcaster;
    private final TrainServiceImpl trainService;
    private final TrainManager trainManager;

    @Inject
    AbstractBlockRegistry(EventBroadcaster eventBroadcaster, TrainServiceImpl trainService, TrainManager trainManager) {
        this.eventBroadcaster = eventBroadcaster;
        this.trainService = trainService;
        this.trainManager = trainManager;
    }

    protected abstract void doInit(Collection<T> trackBlocks);

    abstract void registerListeners(Device device) throws DeviceAccessException;

    abstract void removeListeners(Device device) throws DeviceAccessException;

    void initBlocks(final Collection<T> blocks) {
        log.info("create consumers of track blocks");

        doInit(blocks);
    }

    protected FeedbackBlockModule getFeedbackBlockModule(Device device,
            BusAddressIdentifier entry) throws DeviceAccessException {
        return device.getFeedbackBlockModule(
                entry.getAddress(),
                (entry.getAddress() + 2),
                (entry.getAddress() + 1));
    }

    protected boolean checkBlockFunction(BusDataConfiguration blockFunction) {
        return blockFunction != null && blockFunction.isValid();
    }

    protected BusAddressIdentifier getBusAddressIdentifier(BusDataConfiguration blockFunction) {
        return new BusAddressIdentifier(blockFunction
                .getBus(), blockFunction.getAddress());
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
