package net.wbz.moba.controlcenter.service.train;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import net.wbz.moba.controlcenter.EventBroadcaster;
import net.wbz.moba.controlcenter.shared.track.model.BusDataConfiguration;
import net.wbz.moba.controlcenter.shared.train.Train;
import net.wbz.moba.controlcenter.shared.train.TrainDataChangedEvent;
import net.wbz.moba.controlcenter.shared.train.TrainDrivingDirectionEvent;
import net.wbz.moba.controlcenter.shared.train.TrainDrivingLevelEvent;
import net.wbz.moba.controlcenter.shared.train.TrainFunction;
import net.wbz.moba.controlcenter.shared.train.TrainFunctionStateEvent;
import net.wbz.moba.controlcenter.shared.train.TrainHornStateEvent;
import net.wbz.moba.controlcenter.shared.train.TrainInstance;
import net.wbz.moba.controlcenter.shared.train.TrainLightStateEvent;
import net.wbz.selectrix4java.device.Device;
import net.wbz.selectrix4java.device.DeviceAccessException;
import net.wbz.selectrix4java.device.DeviceConnectionListener;
import net.wbz.selectrix4java.device.DeviceManager;
import net.wbz.selectrix4java.train.TrainDataListener;
import net.wbz.selectrix4java.train.TrainModule;
import org.jboss.logging.Logger;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementation of the {@link TrainService}.
 */
@ApplicationScoped
public class TrainService {

    private static final Logger LOG = Logger.getLogger(TrainService.class);

    private final TrainManager trainManager;
    private final DeviceManager deviceManager;
    private final EventBroadcaster eventBroadcaster;
    private final List<TrainInstance> trainInstances;

    @Inject
    public TrainService(TrainManager trainManager, DeviceManager deviceManager, EventBroadcaster eventBroadcaster) {
        this.trainManager = trainManager;
        this.deviceManager = deviceManager;
        this.eventBroadcaster = eventBroadcaster;

        trainInstances = trainManager.load().stream()
            .map(TrainInstance::of)
            .sorted(Comparator.comparing(x -> x.train().getName()))
            .collect(Collectors.toList());

        deviceManager.addDeviceConnectionListener(new DeviceConnectionListener() {
            @Override
            public void connected(Device device) {
                trainInstances.forEach(trainInstance -> {
                    try {
                        // TODO re-register or add/remove for changes trains are missing
                        reregisterConsumer(trainInstance, deviceManager);
                    } catch (DeviceAccessException e) {
                        LOG.error("can't register consumer for train {}", trainInstance.train(), e);
                    }
                });
            }

            @Override
            public void disconnected(Device device) {
                // TODO remove all listeners
            }
        });
    }

    public TrainInstance trainInstanceByTrain(Train train) {
        return trainInstances.stream().filter(trainInstance -> trainInstance.train().getId().equals(train.getId())).findFirst()
            .orElseThrow(() -> new NotFoundException("no train instance for train %d exists".formatted(train.getId())));
    }

    public void onTrainDataChanged(@Observes TrainDataChangedEvent event) {
        LOG.infof("Train data changed for ID: %s, refreshing service state...", event.getItemId());

        trainManager.getById(event.getItemId())
            .ifPresentOrElse(train -> {
                LOG.infof("Train data found for ID: %s, refreshing train instance...", event.getItemId());
                var updatedInstance = TrainInstance.of(train);
                trainInstances.removeIf(instance -> instance.train().getId().equals(train.getId()));
                trainInstances.add(updatedInstance);
                trainInstances.sort(Comparator.comparing(trainInstance -> trainInstance.train().getName()));

                if (deviceManager.isConnected()) {
                    try {
                        reregisterConsumer(updatedInstance, deviceManager);
                    } catch (DeviceAccessException e) {
                        LOG.error("can't register consumer for train {}", updatedInstance.train(), e);
                    }
                }
            }, () -> trainInstances.removeIf(y -> y.train().getId() == event.itemId));
    }

    private void reregisterConsumer(final TrainInstance trainInstance, DeviceManager deviceManager) throws DeviceAccessException {
        if (trainInstance.train().getAddressByte() >= 0 && deviceManager.isConnected()) {
            TrainModule trainModule = getTrainModule(trainInstance.train(), deviceManager);
            trainModule.removeAllTrainDataListeners();
            trainModule.addTrainDataListener(new TrainDataListener() {
                @Override
                public void drivingLevelChanged(int i) {
                    trainInstance.trainStatus().setDrivingLevel(i);
                    eventBroadcaster.fireEvent(new TrainDrivingLevelEvent(trainInstance.train().getId(), i));
                }

                @Override
                public void drivingDirectionChanged(TrainModule.DRIVING_DIRECTION driving_direction) {
                    trainInstance.trainStatus().setDirection(switch (driving_direction) {
                        case FORWARD -> Train.DRIVING_DIRECTION.FORWARD;
                        case BACKWARD -> Train.DRIVING_DIRECTION.BACKWARD;
                    });
                    eventBroadcaster.fireEvent(new TrainDrivingDirectionEvent(trainInstance.train().getId(),
                        TrainDrivingDirectionEvent.DRIVING_DIRECTION.valueOf(driving_direction.name())));
                }

                @Override
                public void functionStateChanged(int functionAddress, int functionBit, boolean active) {
                    for (TrainFunction trainFunction : trainInstance.train().getFunctions()) {
                        if (trainFunction.getConfiguration().getAddress() == functionAddress
                            && trainFunction.getConfiguration().getBit() == functionBit) {
                            eventBroadcaster
                                .fireEvent(new TrainFunctionStateEvent(trainInstance.train().getId(),
                                    trainFunction, active));
                            break;
                        }
                    }
                }

                @Override
                public void lightStateChanged(boolean state) {
                    // TODO migrate to train function
                    eventBroadcaster.fireEvent(new TrainLightStateEvent(trainInstance.train().getId(), state));
                }

                @Override
                public void hornStateChanged(boolean state) {
                    // TODO migrate to train function
                    eventBroadcaster.fireEvent(new TrainHornStateEvent(trainInstance.train().getId(), state));
                }
            });
        }
    }

    public void updateAutomaticDrivingLevel(Train train, int level) {
        trainInstances.stream().filter(trainInstance -> trainInstance.train().getId().equals(train.getId())).findFirst()
            .ifPresent(trainInstance -> {
                if (trainInstance.trainStatus().getDrivingLevel() > 0) {
                    updateDrivingLevel(train.getId(), level);
                }
            });
    }

    public void updateDrivingLevel(long id, int level) {
        updateDrivingLevel(getTrain(id), level);
    }

    public void updateDrivingLevel(Train train, int level) {
        if (level >= 0 && level <= 31) {
            int address = train.getAddress();
            try {
                deviceManager.getConnectedDevice().orElseThrow(() -> new RuntimeException("no connected device"))
                    .getTrainModule((byte) address).setDrivingLevel(level);
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
            deviceManager.getConnectedDevice().orElseThrow(() -> new RuntimeException("no connected device"))
                .getTrainModule((byte) address)
                .setDirection(forward ? TrainModule.DRIVING_DIRECTION.FORWARD : TrainModule.DRIVING_DIRECTION.BACKWARD);
        } catch (DeviceAccessException e) {
            String msg = "can't change level of train " + id;
            LOG.error(msg, e);
            throw new RuntimeException(msg);
        }
    }

    public void toggleHorn(long id, boolean on) {
        // TODO refactor to functionState
        getTrainModule(id).setHorn(on);
    }

    public void toggleLight(long id, boolean on) {
        // TODO refactor to functionState
        getTrainModule(id).setLight(on);
    }

    public void toggleFunctionState(long id, TrainFunction function, boolean state) {
        try {
            TrainModule trainModule = getTrainModule(id);
            BusDataConfiguration functionConfiguration = function.getConfiguration();
            // TODO remove bus nr quick fix
            functionConfiguration.setBus(trainModule.getBus());
            trainModule.setFunctionState(
                deviceManager.getConnectedDevice().orElseThrow(() -> new RuntimeException("no connected device"))
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
            return deviceManager.getConnectedDevice().orElseThrow(() -> new RuntimeException("no connected device"))
                .getTrainModule(address, additionalAddresses);
        } catch (DeviceAccessException e) {
            throw new RuntimeException("can't found train for address");
        }
    }

    private TrainModule getTrainModule(Train train, DeviceManager deviceManager) throws DeviceAccessException {
        Set<Integer> additionalAddresses = new HashSet<>();
        for (TrainFunction trainFunction : train.getFunctions()) {
            if (trainFunction != null && trainFunction.getConfiguration() != null) {
                additionalAddresses.add(trainFunction.getConfiguration().getAddress());
            }
        }
        return deviceManager.getConnectedDevice().orElseThrow(() -> new RuntimeException("no connected device"))
            .getTrainModule(train.getAddressByte(),
            additionalAddresses.stream().mapToInt(Integer::intValue).toArray());
    }

}
