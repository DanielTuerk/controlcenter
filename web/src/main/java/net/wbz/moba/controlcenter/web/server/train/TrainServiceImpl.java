package net.wbz.moba.controlcenter.web.server.train;

import com.google.gwt.user.client.rpc.RpcTokenException;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.wbz.moba.controlcenter.web.shared.train.Train;
import net.wbz.moba.controlcenter.web.shared.train.TrainFunction;
import net.wbz.moba.controlcenter.web.shared.train.TrainService;
import net.wbz.selectrix4java.api.device.DeviceAccessException;
import net.wbz.selectrix4java.api.train.TrainModule;
import net.wbz.selectrix4java.manager.DeviceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of the {@link net.wbz.moba.controlcenter.web.shared.train.TrainService}.
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
    public void toggleDrivingDirection(long id, Train.DIRECTION direction) {
        int address = trainManager.getTrain(id).getAddress();
        try {
            deviceManager.getConnectedDevice().getTrainModule((byte) address).setDirection(
                    direction == Train.DIRECTION.FORWARD ? TrainModule.DRIVING_DIRECTION.FORWARD : TrainModule.DRIVING_DIRECTION.BACKWARD);
        } catch (DeviceAccessException e) {
            String msg = "can't change level of train " + id;
            LOG.error(msg, e);
            throw new RpcTokenException(msg);
        }
    }

    @Override
    public void setFunctionState(long id, TrainFunction.FUNCTION function, boolean state) {
        int address = trainManager.getTrain(id).getAddress();
        try {
            switch (function) {
                case LIGHT:
                    deviceManager.getConnectedDevice().getTrainModule((byte) address).setLight(state);
                    break;
                case HORN:
                    deviceManager.getConnectedDevice().getTrainModule((byte) address).setHorn(state);
                    break;

                //TODO
                case F1:
//                    toggleExtraFunction(address, Device.BIT.BIT_1, state);
                    break;
                case F2:
//                    toggleExtraFunction(address, Device.BIT.BIT_2, state);
                    break;
                case F3:
//                    toggleExtraFunction(address, Device.BIT.BIT_3, state);
                    break;
                case F4:
//                    toggleExtraFunction(address, Device.BIT.BIT_4, state);
                    break;
                case F5:
//                    toggleExtraFunction(address, Device.BIT.BIT_5, state);
                    break;
                case F6:
//                    toggleExtraFunction(address, Device.BIT.BIT_6, state);
                    break;
                case F7:
//                    toggleExtraFunction(address, Device.BIT.BIT_7, state);
                    break;
                case F8:
//                    toggleExtraFunction(address, Device.BIT.BIT_8, state);
                    break;

                default:
                    throw new RuntimeException();
            }

        } catch (DeviceAccessException e) {
            String msg = String.format("can't change state of function %s of train %d", function.name(), id);
            LOG.error(msg, e);
            throw new RpcTokenException(msg);
        }
    }

//    private void toggleExtraFunction(int address, Device.BIT bit, boolean state) throws DeviceAccessException {
//        deviceManager.getConnectedDevice().getTrainModule((byte) (address + 1)).setBit(bit, state).sendData();
//    }
}
