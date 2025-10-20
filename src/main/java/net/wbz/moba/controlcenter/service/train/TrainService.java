package net.wbz.moba.controlcenter.service.train;

import com.google.common.collect.Sets;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import java.util.Set;
import net.wbz.moba.controlcenter.shared.track.model.BusDataConfiguration;
import net.wbz.moba.controlcenter.shared.train.Train;
import net.wbz.moba.controlcenter.shared.train.TrainFunction;
import net.wbz.selectrix4java.device.Device;
import net.wbz.selectrix4java.device.DeviceAccessException;
import net.wbz.selectrix4java.device.DeviceConnectionListener;
import net.wbz.selectrix4java.device.DeviceManager;
import net.wbz.selectrix4java.train.TrainDataListener;
import net.wbz.selectrix4java.train.TrainModule;
import org.jboss.logging.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of the {@link TrainService}.
 */
@ApplicationScoped
public class TrainService {

    private static final Logger LOG = Logger.getLogger(TrainService.class);

    private final TrainManager trainManager;
    private final DeviceManager deviceManager;

    @Inject
    public TrainService(TrainManager trainManager, DeviceManager deviceManager) {
        this.trainManager = trainManager;
        this.deviceManager = deviceManager;

        deviceManager.addDeviceConnectionListener(new DeviceConnectionListener() {
            @Override
            public void connected(Device device) {
                try {
                    for (final Train train : trainManager.load()) {
                        reregisterConsumer(train, deviceManager);
                    }
                } catch (DeviceAccessException e) {
                    LOG.error("can't register consumer for device {}", device, e);
                }
            }

            @Override
            public void disconnected(Device device) {

            }
        });
    }

    private void reregisterConsumer(final Train train, DeviceManager deviceManager) throws DeviceAccessException {
        if (train.getAddressByte() >= 0 && deviceManager.isConnected()) {
            TrainModule trainModule = getTrainModule(train, deviceManager);
            trainModule.removeAllTrainDataListeners();
            trainModule.addTrainDataListener(new TrainDataListener() {
                @Override
                public void drivingLevelChanged(int i) {
                    train.setDrivingLevel(i);
//                    eventBroadcaster.fireEvent(new TrainDrivingLevelEvent(train.getId(), i));
                }

                @Override
                public void drivingDirectionChanged(TrainModule.DRIVING_DIRECTION driving_direction) {
                    train.setForward(driving_direction == TrainModule.DRIVING_DIRECTION.FORWARD);
//                    eventBroadcaster.fireEvent(new TrainDrivingDirectionEvent(train.getId(),
//                        TrainDrivingDirectionEvent.DRIVING_DIRECTION.valueOf(driving_direction.name())));
                }

                @Override
                public void functionStateChanged(int functionAddress, int functionBit, boolean active) {
                    for (TrainFunction trainFunction : train.getFunctions()) {
                        if (trainFunction.getConfiguration().getAddress() == functionAddress
                            && trainFunction.getConfiguration().getBit() == functionBit) {
//                            eventBroadcaster
//                                .fireEvent(new TrainFunctionStateEvent(train.getId(), trainFunction, active));
                            break;
                        }
                    }
                }

                @Override
                public void lightStateChanged(boolean state) {
//                    eventBroadcaster.fireEvent(new TrainLightStateEvent(train.getId(), state));
                }

                @Override
                public void hornStateChanged(boolean state) {
//                    eventBroadcaster.fireEvent(new TrainHornStateEvent(train.getId(), state));
                }
            });
        }
    }

    public void updateAutomaticDrivingLevel(Train train, int level) {
        if (train.getDrivingLevel() > 0) {
            updateDrivingLevel(train.getId(), level);
        }
    }

    public void updateDrivingLevel(long id, int level) {
        updateDrivingLevel(getTrain(id), level);
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
        int address = getTrain(id).getAddress();
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

    private Train getTrain(long id) {
        return trainManager.getById(id)
            .orElseThrow(() -> new NotFoundException("no train for id %d exists".formatted(id)));
    }

    /**
     * TODO Avoid null pointer for non connected device
     *
     * @param id id of train
     * @param additionalAddresses additional addresses
     * @return {@link TrainModule}
     */
    private TrainModule getTrainModule(long id, int... additionalAddresses) {
        int address = getTrain(id).getAddress();
        try {
            return deviceManager.getConnectedDevice().getTrainModule(address, additionalAddresses);
        } catch (DeviceAccessException e) {
            throw new RuntimeException("can't found train for address");
        }
    }

    private TrainModule getTrainModule(Train train, DeviceManager deviceManager) throws DeviceAccessException {
        Set<Integer> additionalAddresses = Sets.newHashSet();
        for (TrainFunction trainFunction : train.getFunctions()) {
            if (trainFunction != null && trainFunction.getConfiguration() != null) {
                additionalAddresses.add(trainFunction.getConfiguration().getAddress());
            }
        }
        return deviceManager.getConnectedDevice().getTrainModule(train.getAddressByte(),
            additionalAddresses.stream().mapToInt(Integer::intValue).toArray());
    }

}
