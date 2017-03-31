package net.wbz.moba.controlcenter.web.server.web.train;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;

import net.wbz.moba.controlcenter.web.server.EventBroadcaster;
import net.wbz.moba.controlcenter.web.server.persist.train.TrainDao;
import net.wbz.moba.controlcenter.web.server.persist.train.TrainEntity;
import net.wbz.moba.controlcenter.web.server.web.DataMapper;
import net.wbz.moba.controlcenter.web.shared.train.Train;
import net.wbz.moba.controlcenter.web.shared.train.TrainDrivingDirectionEvent;
import net.wbz.moba.controlcenter.web.shared.train.TrainDrivingLevelEvent;
import net.wbz.moba.controlcenter.web.shared.train.TrainFunction;
import net.wbz.moba.controlcenter.web.shared.train.TrainFunctionStateEvent;
import net.wbz.moba.controlcenter.web.shared.train.TrainHornStateEvent;
import net.wbz.moba.controlcenter.web.shared.train.TrainLightStateEvent;
import net.wbz.selectrix4java.device.Device;
import net.wbz.selectrix4java.device.DeviceAccessException;
import net.wbz.selectrix4java.device.DeviceConnectionListener;
import net.wbz.selectrix4java.device.DeviceManager;
import net.wbz.selectrix4java.train.TrainDataListener;
import net.wbz.selectrix4java.train.TrainModule;

/**
 * Manager to access the {@link TrainEntity}s from database. Each {@link TrainEntity} register an {@link
 * net.wbz.selectrix4java.train.TrainDataListener} and throw the state changes by the {@link
 * net.wbz.moba.controlcenter.web.server.EventBroadcaster} as train events to the client.
 *
 * @author Daniel Tuerk
 */
@Singleton
public class TrainManager {

    private static final Logger LOG = LoggerFactory.getLogger(TrainManager.class);

    private final TrainDao dao;
    private final EventBroadcaster eventBroadcaster;
    private final DeviceManager deviceManager;
    private final DataMapper<Train, TrainEntity> dataMapper = new DataMapper<>(Train.class, TrainEntity.class);
    private final List<Train> cachedTrains = Lists.newArrayList();

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
                    for (final Train train : getTrains()) {
                        reregisterConsumer(train, deviceManager, eventBroadcaster);
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

    private void reregisterConsumer(final Train train, DeviceManager deviceManager,
            final EventBroadcaster eventBroadcaster) throws DeviceAccessException {
        if (train.getAddressByte() >= 0 && deviceManager.isConnected()) {
            TrainModule trainModule = getTrainModule(train, deviceManager);
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
                                            driving_direction.name())));
                        }

                        @Override
                        public void functionStateChanged(int functionAddress, int functionBit, boolean active) {
                            for (TrainFunction trainFunction : train.getFunctions()) {
                                if (trainFunction.getConfiguration().getAddress() == functionAddress
                                        && trainFunction.getConfiguration().getBit() == functionBit) {
                                    eventBroadcaster.fireEvent(new TrainFunctionStateEvent(train.getId(), trainFunction,
                                            active));
                                    break;
                            }
                        }
                        }

                        @Override
                        public void lightStateChanged(boolean state) {
                            eventBroadcaster.fireEvent(new TrainLightStateEvent(train.getId(), state));
                        }

                        @Override
                        public void hornStateChanged(boolean state) {
                            eventBroadcaster.fireEvent(new TrainHornStateEvent(train.getId(), state));
                        }
                    });
        }
    }

    private TrainModule getTrainModule(Train train, DeviceManager deviceManager) throws DeviceAccessException {

        Set<Integer> additionalAddresses = Sets.newHashSet();
        for (TrainFunction trainFunction : train.getFunctions()) {
            if (trainFunction != null && trainFunction.getConfiguration() != null) {
                additionalAddresses.add(trainFunction.getConfiguration().getAddress());
            }
        }
        return deviceManager.getConnectedDevice().getTrainModule(train.getAddressByte(), ArrayUtils.toPrimitive(
                additionalAddresses.toArray(new Integer[additionalAddresses.size()])));
    }

    private synchronized void reloadTrains() {
        cachedTrains.clear();
        Collection<Train> trains = getTrains();
        if (deviceManager.isConnected()) {
            for (Train train : trains) {
                try {
                    reregisterConsumer(train, deviceManager, eventBroadcaster);
                } catch (DeviceAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Transactional
    public void createTrain(Train train) {
        TrainEntity entity = new TrainEntity();
        entity.setName(train.getName());
        entity.setAddress(train.getAddress());
        dao.create(entity);

        reloadTrains();
    }

    @Transactional
    public void updateTrain(Train train) {
        TrainEntity entity = dataMapper.transformTarget(train);
        dao.update(entity);

        reloadTrains();
    }

    @Transactional
    public void deleteTrain(long trainId) {
        dao.delete(dao.findById(trainId));
        reloadTrains();
    }

    public Collection<Train> getTrains() {
        if (cachedTrains.isEmpty()) {
            cachedTrains.addAll(dataMapper.transformSource(dao.getTrains()));
        }
        return cachedTrains;
    }

    /**
     * Return the {@link Train} for the given id.
     *
     * @param id id of {@link Train}
     * @return {@link Train} of given id or {@code null} if not found
     */
    public Train getTrain(long id) {
        for (Train train : getTrains()) {
            if (train.getId() == id) {
                return train;
            }
        }
        return null;
    }

    /**
     * Return the {@link Train} for the given address.
     *
     * @param address address of {@link Train}
     * @return {@link Train} of given address or {@code null} if not found
     */
    public Train getTrain(int address) {
        for (Train train : getTrains()) {
            if (train.getAddress() != null && train.getAddress() == address) {
                return train;
            }
        }
        LOG.error("can't find train for address " + address);
        return null;
    }

}
