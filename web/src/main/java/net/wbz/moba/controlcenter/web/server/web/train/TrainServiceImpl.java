package net.wbz.moba.controlcenter.web.server.web.train;

import com.google.gwt.user.client.rpc.RpcTokenException;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.wbz.moba.controlcenter.web.shared.track.model.BusDataConfiguration;
import net.wbz.moba.controlcenter.web.shared.train.TrainFunction;
import net.wbz.moba.controlcenter.web.shared.train.TrainService;
import net.wbz.selectrix4java.device.DeviceAccessException;
import net.wbz.selectrix4java.device.DeviceManager;
import net.wbz.selectrix4java.train.TrainModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of the {@link TrainService}.
 */
@Singleton
public class TrainServiceImpl extends RemoteServiceServlet implements TrainService {

    private static final Logger LOG = LoggerFactory.getLogger(TrainServiceImpl.class);

    private final TrainManager trainManager;
    private final DeviceManager deviceManager;

    @Inject
    public TrainServiceImpl(TrainManager trainManager, DeviceManager deviceManager) {
        this.trainManager = trainManager;
        this.deviceManager = deviceManager;
    }

    @Override
    public void updateDrivingLevel(long id, int level) {
        if (level >= 0 && level <= 31) {
            int address = trainManager.getTrain(id).getAddress();
            try {
                deviceManager.getConnectedDevice().getTrainModule((byte) address).setDrivingLevel(level);
            } catch (DeviceAccessException e) {
                String msg = "can't change level of train " + id;
                LOG.error(msg, e);
                throw new RpcTokenException(msg);
            }
        } else {
            throw new RpcTokenException("invalid level " + level + " (0-127)");
        }
    }

    @Override
    public void toggleDrivingDirection(long id, boolean forward) {
        int address = trainManager.getTrain(id).getAddress();
        try {
            deviceManager.getConnectedDevice().getTrainModule((byte) address).setDirection(
                    forward ? TrainModule.DRIVING_DIRECTION.FORWARD : TrainModule.DRIVING_DIRECTION.BACKWARD);
        } catch (DeviceAccessException e) {
            String msg = "can't change level of train " + id;
            LOG.error(msg, e);
            throw new RpcTokenException(msg);
        }
    }

    @Override
    public void toggleHorn(long id, boolean on) {
        getTrainModule(id).setHorn(on);
    }

    @Override
    public void toggleLight(long id, boolean on) {
        getTrainModule(id).setLight(on);
    }

//    private TrainModule getTrainModule(long id) {
//
//    }

    /**
     * TODO Avoid null pointer for non connected device
     *
     * @param id
     * @param additionalAddresses
     * @return
     */
    private TrainModule getTrainModule(long id, int... additionalAddresses) {
        int address = trainManager.getTrain(id).getAddress();
        try {
            return deviceManager.getConnectedDevice().getTrainModule(address, additionalAddresses);
        } catch (DeviceAccessException e) {
            throw new RuntimeException("can't found train for address");
        }
    }


    @Override
    public void toggleFunctionState(long id, TrainFunction function, boolean state) {
        try {
            TrainModule trainModule = getTrainModule(id);
            BusDataConfiguration functionConfiguration = function.getConfiguration();
            trainModule.setFunctionState(deviceManager.getConnectedDevice().getBusAddress(
                    functionConfiguration.getBus(),
                    functionConfiguration.getAddress()),
                    functionConfiguration.getBit(), state);
        } catch (DeviceAccessException e) {
            String msg = String.format("can't change state of function %s of train %d", function.getAlias(), id);
            LOG.error(msg, e);
            throw new RpcTokenException(msg);
        }
    }

}
