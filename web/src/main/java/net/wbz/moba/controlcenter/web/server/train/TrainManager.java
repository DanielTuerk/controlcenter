package net.wbz.moba.controlcenter.web.server.train;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;
import net.wbz.moba.controlcenter.web.server.EventBroadcaster;
import net.wbz.moba.controlcenter.web.shared.train.*;
import net.wbz.selectrix4java.device.Device;
import net.wbz.selectrix4java.device.DeviceAccessException;
import net.wbz.selectrix4java.device.DeviceConnectionListener;
import net.wbz.selectrix4java.device.DeviceManager;
import net.wbz.selectrix4java.train.TrainDataListener;
import net.wbz.selectrix4java.train.TrainModule;

import javax.inject.Provider;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;
import java.util.Map;

/**
 * Manager to access the {@link net.wbz.moba.controlcenter.web.shared.train.Train}s from database.
 * Each {@link net.wbz.moba.controlcenter.web.shared.train.Train} register an
 * {@link net.wbz.selectrix4java.train.TrainDataListener} and throw the state changes by
 * the {@link net.wbz.moba.controlcenter.web.server.EventBroadcaster} as train events to the client.
 *
 * @author Daniel Tuerk
 */
@Singleton
public class TrainManager {

    private final Map<Long, Train> trains = Maps.newConcurrentMap();

    private final Provider<EntityManager> entityManager;

    @Inject
    public TrainManager(final EventBroadcaster eventBroadcaster,
                        final DeviceManager deviceManager, Provider<EntityManager> entityManager) {
        this.entityManager = entityManager;

        deviceManager.addDeviceConnectionListener(new DeviceConnectionListener() {
            @Override
            public void connected(Device device) {
                try {
                    for (final Train train : getTrains()) {
                        deviceManager.getConnectedDevice().getTrainModule(
                                (byte) train.getAddress()).addTrainDataListener(
                                new TrainDataListener() {
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
                                }
                        );
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

    @Transactional
    public void storeTrain(Train train) {
        EntityManager manager = entityManager.get();
        if (manager.find(Train.class, train.getId()) != null) {
            train = manager.merge(train);
        }
        manager.persist(train);
    }

    public List<Train> getTrains() {
        Query typedQuery = entityManager.get().createQuery(
                "SELECT x FROM Train x");
        return typedQuery.getResultList();
    }

    public Train getTrainByAddress(int address) throws TrainException {
        Query typedQuery = entityManager.get().createQuery(
                "SELECT x FROM Train x where address=:address");
        typedQuery.setParameter("address", address);
        Train train = (Train) typedQuery.getSingleResult();
        if (train != null) {
            return train;
        }
        throw new TrainException(String.format("no train for address %d found!", address));
    }


    public Train getTrainById(long trainId) throws TrainException {
        Query typedQuery = entityManager.get().createQuery(
                "SELECT x FROM Train x where id=:id");
        typedQuery.setParameter("id", trainId);
        Train train = (Train) typedQuery.getSingleResult();
        if (train != null) {
            return train;
        }
        throw new TrainException(String.format("no train for id %d found!", trainId));
    }


    @Transactional
    public void deleteTrain(long trainId) throws TrainException {
        entityManager.get().remove(getTrainById(trainId));
    }
}
