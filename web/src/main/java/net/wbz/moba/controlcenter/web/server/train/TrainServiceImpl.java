package net.wbz.moba.controlcenter.web.server.train;

import com.google.gwt.user.client.rpc.RpcTokenException;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.wbz.moba.controlcenter.communication.api.Device;
import net.wbz.moba.controlcenter.communication.api.DeviceAccessException;
import net.wbz.moba.controlcenter.communication.api.OutputModule;
import net.wbz.moba.controlcenter.communication.manager.DeviceManager;
import net.wbz.moba.controlcenter.web.shared.train.Train;
import net.wbz.moba.controlcenter.web.shared.train.TrainFunction;
import net.wbz.moba.controlcenter.web.shared.train.TrainService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.math.BigInteger;

/**
 * Created by Daniel on 08.03.14.
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
        if (level >= 0 && level <= 127) {
            int address = trainManager.getTrain(id).getAddress();
            BigInteger wrappedLevel = BigInteger.valueOf((long) ((int)level));
            try {
                deviceManager.getConnectedDevice().getTrainModule((byte) address)
                        .setBit(Device.BIT.BIT_1, wrappedLevel.testBit(0))
                        .setBit(Device.BIT.BIT_2, wrappedLevel.testBit(1))
                        .setBit(Device.BIT.BIT_3, wrappedLevel.testBit(2))
                        .setBit(Device.BIT.BIT_4, wrappedLevel.testBit(3))
                        .setBit(Device.BIT.BIT_5, wrappedLevel.testBit(4))
                        .sendData();
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
            deviceManager.getConnectedDevice().getTrainModule((byte) address)
                    // true = backward; false = forward
                    .setBit(Device.BIT.BIT_6, direction == Train.DIRECTION.BACKWARD)
                    .sendData();
        } catch (DeviceAccessException e) {
            String msg = "can't change level of train " + id;
            LOG.error(msg, e);
            throw new RpcTokenException(msg);
        }
    }

    @Override
    public void setFunctionState(long id, TrainFunction.FUNCTION function, boolean state) {
        int address = trainManager.getTrain(id).getAddress();
        OutputModule trainModule;
        try {
            switch (function) {
                case LIGHT:
                    trainModule = deviceManager.getConnectedDevice().getTrainModule((byte) address);
                    trainModule.setBit(Device.BIT.BIT_7, state).sendData();
                    break;
                case HORN:
                    trainModule = deviceManager.getConnectedDevice().getTrainModule((byte) address);
                    trainModule.setBit(Device.BIT.BIT_8, state).sendData();
                    break;
                case F1:
                    toggleExtraFunction(address, Device.BIT.BIT_1, state);
                    break;
                case F2:
                    toggleExtraFunction(address, Device.BIT.BIT_2, state);
                    break;
                case F3:
                    toggleExtraFunction(address, Device.BIT.BIT_3, state);
                    break;
                case F4:
                    toggleExtraFunction(address, Device.BIT.BIT_4, state);
                    break;
                case F5:
                    toggleExtraFunction(address, Device.BIT.BIT_5, state);
                    break;
                case F6:
                    toggleExtraFunction(address, Device.BIT.BIT_6, state);
                    break;
                case F7:
                    toggleExtraFunction(address, Device.BIT.BIT_7, state);
                    break;
                case F8:
                    toggleExtraFunction(address, Device.BIT.BIT_8, state);
                    break;

                default:
                    throw new NotImplementedException();
            }

        } catch (DeviceAccessException e) {
            String msg = String.format("can't change state of function %s of train %d", function.name(), id);
            LOG.error(msg, e);
            throw new RpcTokenException(msg);
        }
    }

    private void toggleExtraFunction(int address, Device.BIT bit, boolean state) throws DeviceAccessException {
        deviceManager.getConnectedDevice().getTrainModule((byte) (address + 1)).setBit(bit, state).sendData();
    }
}