package net.wbz.moba.controlcenter.web.server.web.train;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.wbz.moba.controlcenter.web.shared.track.model.BusDataConfiguration;
import net.wbz.moba.controlcenter.web.shared.train.Train;
import net.wbz.moba.controlcenter.web.shared.train.TrainFunction;
import net.wbz.selectrix4java.device.DeviceAccessException;
import net.wbz.selectrix4java.device.DeviceManager;
import net.wbz.selectrix4java.train.TrainModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of the {@link TrainService}.
 */
@Singleton
public class TrainService {

    private static final Logger LOG = LoggerFactory.getLogger(TrainService.class);

    private final TrainManager trainManager;
    private final DeviceManager deviceManager;

    @Inject
    public TrainService(TrainManager trainManager, DeviceManager deviceManager) {
        this.trainManager = trainManager;
        this.deviceManager = deviceManager;
    }

    public void updateAutomaticDrivingLevel(Train train, int level) {
        if (train.getDrivingLevel() > 0) {
            updateDrivingLevel(train.getId(), level);
        }
    }

    public void updateDrivingLevel(long id, int level) {
        updateDrivingLevel(trainManager.getTrain(id), level);
    }

    public void updateDrivingLevel(Train train, int level) {
        if (level >= 0 && level <= 31) {
            int address = train.getAddress();
            try {
                deviceManager.getConnectedDevice().getTrainModule((byte) address).setDrivingLevel(level);
            } catch (DeviceAccessException e) {
                String msg = "can't change level of train " + train;
                LOG.error(msg, e);
                throw new RuntimeException(msg);
            }
        } else {
            throw new RuntimeException("invalid level " + level + " (0-31)");
        }
    }

    public void toggleDrivingDirection(long id, boolean forward) {
        int address = trainManager.getTrain(id).getAddress();
        try {
            deviceManager.getConnectedDevice().getTrainModule((byte) address).setDirection(
                forward ? TrainModule.DRIVING_DIRECTION.FORWARD : TrainModule.DRIVING_DIRECTION.BACKWARD);
        } catch (DeviceAccessException e) {
            String msg = "can't change level of train " + id;
            LOG.error(msg, e);
            throw new RuntimeException(msg);
        }
    }

    public void toggleHorn(long id, boolean on) {
        getTrainModule(id).setHorn(on);
    }

    public void toggleLight(long id, boolean on) {
        getTrainModule(id).setLight(on);
    }


    /**
     * TODO Avoid null pointer for non connected device
     *
     * @param id id of train
     * @param additionalAddresses additional addresses
     * @return {@link TrainModule}
     */
    private TrainModule getTrainModule(long id, int... additionalAddresses) {
        int address = trainManager.getTrain(id).getAddress();
        try {
            return deviceManager.getConnectedDevice().getTrainModule(address, additionalAddresses);
        } catch (DeviceAccessException e) {
            throw new RuntimeException("can't found train for address");
        }
    }

    public void toggleFunctionState(long id, TrainFunction function, boolean state) {
        try {
            TrainModule trainModule = getTrainModule(id);
            BusDataConfiguration functionConfiguration = function.getConfiguration();
            // TODO remove bus nr quick fix
            functionConfiguration.setBus(trainModule.getBus());
            trainModule.setFunctionState(deviceManager.getConnectedDevice()
                    .getBusAddress(functionConfiguration.getBus(), functionConfiguration.getAddress()),
                functionConfiguration.getBit(), state);
        } catch (DeviceAccessException e) {
            String msg = String.format("can't change state of function %s of train %d", function.getAlias(), id);
            LOG.error(msg, e);
            throw new RuntimeException(msg);
        }
    }

}
