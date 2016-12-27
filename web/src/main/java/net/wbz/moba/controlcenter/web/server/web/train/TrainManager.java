package net.wbz.moba.controlcenter.web.server.web.train;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;
import net.wbz.moba.controlcenter.web.server.EventBroadcaster;
import net.wbz.moba.controlcenter.web.server.persist.train.TrainDao;
import net.wbz.moba.controlcenter.web.server.persist.train.TrainEntity;
import net.wbz.moba.controlcenter.web.server.web.DataMapper;
import net.wbz.moba.controlcenter.web.shared.train.*;
import net.wbz.selectrix4java.device.Device;
import net.wbz.selectrix4java.device.DeviceAccessException;
import net.wbz.selectrix4java.device.DeviceConnectionListener;
import net.wbz.selectrix4java.device.DeviceManager;
import net.wbz.selectrix4java.train.TrainDataListener;
import net.wbz.selectrix4java.train.TrainModule;

import java.util.Collection;


/**
 * Manager to access the {@link TrainEntity}s from database.
 * Each {@link TrainEntity} register an
 * {@link net.wbz.selectrix4java.train.TrainDataListener} and throw the state changes by
 * the {@link net.wbz.moba.controlcenter.web.server.EventBroadcaster} as train events to the client.
 *
 * @author Daniel Tuerk
 */
@Singleton
public class TrainManager {

    private final TrainDao dao;
    private final EventBroadcaster eventBroadcaster;
    private final DeviceManager deviceManager;
    private final DataMapper<Train, TrainEntity> dataMapper = new DataMapper<>(Train.class, TrainEntity.class);

    @Inject
    public TrainManager(final EventBroadcaster eventBroadcaster,
                        final DeviceManager deviceManager, TrainDao dao) {
        this.dao = dao;
        this.eventBroadcaster = eventBroadcaster;
        this.deviceManager = deviceManager;

        deviceManager.addDeviceConnectionListener(new DeviceConnectionListener() {
            @Override
            public void connected(Device device) {
                try {
                    for (final TrainEntity trainEntity : TrainManager.this.dao.getTrains()) {
                        reregisterConsumer(dataMapper.transformSource(trainEntity), deviceManager, eventBroadcaster);
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

    private void reregisterConsumer(final Train train, DeviceManager deviceManager, final EventBroadcaster eventBroadcaster) throws DeviceAccessException {
        if (train.getAddressByte() >= 0 && deviceManager.isConnected()) {
            TrainModule trainModule = deviceManager.getConnectedDevice().getTrainModule(train.getAddressByte());
            trainModule.removeAllTrainDataListeners();
            trainModule.addTrainDataListener(
                    new TrainDataListener() {
                        @Override
                        public void drivingLevelChanged(int i) {
                            train.setDrivingLevel(i);
                            eventBroadcaster.fireEvent(new TrainDrivingLevelEvent(train.getId(), i));
                        }

                        @Override
                        public void drivingDirectionChanged(TrainModule.DRIVING_DIRECTION driving_direction) {
                            train.setForward(driving_direction == TrainModule.DRIVING_DIRECTION.FORWARD);
                            eventBroadcaster.fireEvent(new TrainDrivingDirectionEvent(train.getId(),
                                    TrainDrivingDirectionEvent.DRIVING_DIRECTION.valueOf(
                                            driving_direction.name())
                            ));
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

                            // TODO functions

//                            train.getFunction(trainFunction).setState(active);
                            eventBroadcaster.fireEvent(new TrainFunctionStateEvent(train.getId(), trainFunction,
                                    active));
                        }

                        @Override
                        public void lightStateChanged(boolean state) {
//                            train.getFunction(TrainFunction.FUNCTION.LIGHT).setState(state);
                            eventBroadcaster.fireEvent(new TrainLightStateEvent(train.getId(), state));
                        }

                        @Override
                        public void hornStateChanged(boolean state) {
//                            train.getFunction(TrainFunction.FUNCTION.HORN).setState(state);
                            eventBroadcaster.fireEvent(new TrainHornStateEvent(train.getId(), state));
                        }
                    }
            );
        }
    }

    public Train getTrain(long id) {
        return dataMapper.transformSource(dao.findById(id));
//        if (train != null) {
//            return train;
//        }
//        throw new RuntimeException(String.format("no train for id %d found!", id));
    }

    @Transactional
    public void createTrain(Train train) {
        TrainEntity entity = updateEntitryFromDto(new TrainEntity(), train);
        dao.create(entity);

        // TODO update or create train functions

        try {
            reregisterConsumer(train, deviceManager, eventBroadcaster);
        } catch (DeviceAccessException e) {
            e.printStackTrace();
        }
    }

    @Transactional
    public void updateTrain(Train train) {

        TrainEntity entity = updateEntitryFromDto(dao.findById(train.getId()), train);
        dao.update(entity);

        // TODO update or create train functions

        try {
            reregisterConsumer(train, deviceManager, eventBroadcaster);
        } catch (DeviceAccessException e) {
            e.printStackTrace();
        }


    }

    private TrainEntity updateEntitryFromDto(TrainEntity entity, Train train) {
        entity.setName(train.getName());
        entity.setAddress(train.getAddress());
        return entity;
    }

    public Collection<Train> getTrains() {
        return dataMapper.transformSource(dao.getTrains());
    }

    public Train getTrain(int address) {
        try {
            return dataMapper.transformSource(dao.getTrainByAddress(address));
        } catch (TrainException e) {
//            LOG.error("can't find train", e);
        }
        return null;
    }

    @Transactional
    public void deleteTrain(long trainId) {
        dao.delete(dao.findById(trainId));
    }
}
