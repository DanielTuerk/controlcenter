package net.wbz.moba.controlcenter.web.server.train;

import com.db4o.ObjectSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import net.wbz.moba.controlcenter.db.Database;
import net.wbz.moba.controlcenter.db.DatabaseFactory;
import net.wbz.moba.controlcenter.web.server.EventBroadcaster;
import net.wbz.moba.controlcenter.web.shared.train.*;
import net.wbz.selectrix4java.device.Device;
import net.wbz.selectrix4java.device.DeviceAccessException;
import net.wbz.selectrix4java.device.DeviceConnectionListener;
import net.wbz.selectrix4java.device.DeviceManager;
import net.wbz.selectrix4java.train.TrainDataListener;
import net.wbz.selectrix4java.train.TrainModule;

import java.util.List;
import java.util.Map;

/**
 * Created by Daniel on 08.03.14.
 */
@Singleton
public class TrainManager {

    private final Map<Long, Train> trains = Maps.newConcurrentMap();
    private static final String TRAINS_DB_KEY = "trains";

    private final Database database;

    @Inject
    public TrainManager(@Named(TRAINS_DB_KEY) DatabaseFactory databaseFactory, final EventBroadcaster eventBroadcaster,
                        final DeviceManager deviceManager) {

        database = databaseFactory.getOrCreateDatabase(TRAINS_DB_KEY);
        loadFromDatabase();

        deviceManager.addDeviceConnectionListener(new DeviceConnectionListener() {
            @Override
            public void connected(Device device) {
                try {
                    for (final Train train : getTrains()) {
                        deviceManager.getConnectedDevice().getTrainModule((byte) train.getAddress()).addTrainDataListener(new TrainDataListener() {
                            @Override
                            public void drivingLevelChanged(int i) {
                                train.setDrivingLevel(i);
                                eventBroadcaster.fireEvent(new TrainDrivingLevelEvent(train.getId(), i));
                            }

                            @Override
                            public void drivingDirectionChanged(TrainModule.DRIVING_DIRECTION driving_direction) {
                                train.setDrivingDirection(Train.DIRECTION.valueOf(driving_direction.name()));
                                eventBroadcaster.fireEvent(new TrainDrivingDirectionEvent(train.getId(),
                                        TrainDrivingDirectionEvent.DRIVING_DIRECTION.valueOf(
                                                driving_direction.name())));
                            }

                            @Override
                            public void functionStateChanged(byte functionAddress, int functionBit, boolean active) {
                                TrainFunction.FUNCTION trainFunction;
                                switch (functionBit) {
                                    case 1:
                                        trainFunction = TrainFunction.FUNCTION.F1;
                                        break;
                                    case 2:
                                        trainFunction = TrainFunction.FUNCTION.F2;
                                        break;
                                    case 3:
                                        trainFunction = TrainFunction.FUNCTION.F3;
                                        break;
                                    case 4:
                                        trainFunction = TrainFunction.FUNCTION.F4;
                                        break;
                                    case 5:
                                        trainFunction = TrainFunction.FUNCTION.F5;
                                        break;
                                    case 6:
                                        trainFunction = TrainFunction.FUNCTION.F6;
                                        break;
                                    case 7:
                                        trainFunction = TrainFunction.FUNCTION.F7;
                                        break;
                                    case 8:
                                        trainFunction = TrainFunction.FUNCTION.F8;
                                        break;
                                    default:
                                        throw new RuntimeException("no function available for bit: " + functionBit);
                                }
                                train.getFunction(trainFunction).setState(active);
                                eventBroadcaster.fireEvent(new TrainFunctionStateEvent(train.getId(), trainFunction,
                                        active));
                            }

                            @Override
                            public void lightStateChanged(boolean state) {
                                train.getFunction(TrainFunction.FUNCTION.LIGHT).setState(state);
                                eventBroadcaster.fireEvent(new TrainLightStateEvent(train.getId(), state));
                            }

                            @Override
                            public void hornStateChanged(boolean state) {
                                train.getFunction(TrainFunction.FUNCTION.HORN).setState(state);
                                eventBroadcaster.fireEvent(new TrainHornStateEvent(train.getId(), state));
                            }
                        });
                    }
                } catch (DeviceAccessException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void disconnected(Device device) {

            }
        });

    }

    public Train getTrain(long id) {
        if (trains.containsKey(id)) {
            return trains.get(id);
        }
        throw new RuntimeException(String.format("no train found for id %d", id));
    }

    public void storeTrain(Train train) throws Exception {
        database.getObjectContainer().store(train);
        database.getObjectContainer().commit();

        loadFromDatabase();
    }

    private void loadFromDatabase() {
        trains.clear();
        ObjectSet<Train> storedDevices = database.getObjectContainer().query(Train.class);
        for (Train train : storedDevices) {
            trains.put(train.getId(), train);
        }
    }

    public List<Train> getTrains() {
        return Lists.newArrayList(trains.values());
    }

    public Train getTrainByAddress(int address) throws TrainException {
        for (Train train : trains.values()) {
            if (train.getAddress() == address) {
                return train;
            }
        }
        throw new TrainException(String.format("no train for address %d found!", address));
    }
}
