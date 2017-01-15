package net.wbz.moba.controlcenter.web.server.web.editor;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;
import net.wbz.moba.controlcenter.web.server.EventBroadcaster;
import net.wbz.moba.controlcenter.web.server.persist.construction.ConstructionDao;
import net.wbz.moba.controlcenter.web.server.persist.construction.ConstructionEntity;
import net.wbz.moba.controlcenter.web.server.persist.construction.track.AbstractTrackPartEntity;
import net.wbz.moba.controlcenter.web.server.persist.construction.track.BusDataConfigurationEntity;
import net.wbz.moba.controlcenter.web.server.persist.construction.track.TrackPartDao;
import net.wbz.moba.controlcenter.web.server.web.DataMapper;
import net.wbz.moba.controlcenter.web.server.web.TrackPartDataMapper;
import net.wbz.moba.controlcenter.web.server.web.constrution.ConstructionServiceImpl;
import net.wbz.moba.controlcenter.web.shared.bus.FeedbackBlockEvent;
import net.wbz.moba.controlcenter.web.shared.editor.TrackEditorService;
import net.wbz.moba.controlcenter.web.shared.track.model.AbstractTrackPart;
import net.wbz.moba.controlcenter.web.shared.track.model.BusDataConfiguration;
import net.wbz.moba.controlcenter.web.shared.track.model.EventConfiguration;
import net.wbz.moba.controlcenter.web.shared.track.model.HasToggleFunction;
import net.wbz.moba.controlcenter.web.shared.track.model.Signal;
import net.wbz.moba.controlcenter.web.shared.viewer.TrackPartBlockEvent;
import net.wbz.moba.controlcenter.web.shared.viewer.TrackPartStateEvent;
import net.wbz.selectrix4java.block.FeedbackBlockListener;
import net.wbz.selectrix4java.bus.BusAddress;
import net.wbz.selectrix4java.bus.BusAddressBitListener;
import net.wbz.selectrix4java.bus.BusAddressListener;
import net.wbz.selectrix4java.bus.BusListener;
import net.wbz.selectrix4java.device.Device;
import net.wbz.selectrix4java.device.DeviceAccessException;
import net.wbz.selectrix4java.device.DeviceConnectionListener;
import net.wbz.selectrix4java.device.DeviceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author Daniel Tuerk
 */
@Singleton
public class TrackEditorServiceImpl extends RemoteServiceServlet implements TrackEditorService {

    private static final Logger log = LoggerFactory.getLogger(TrackEditorServiceImpl.class);

    private final ConstructionServiceImpl constructionService;

    private final Map<BusAddressIdentifier, List<BusListener>> busAddressListenersOfTheCurrentTrack = Maps
            .newConcurrentMap();
    private final Map<BusAddressIdentifier, FeedbackBlockListener> busAddressFeedbackBlockListenersOfTheCurrentTrack =
            Maps.newConcurrentMap();
    private final DeviceManager deviceManager;
    private final EventBroadcaster eventBroadcaster;
    private final DataMapper<? extends AbstractTrackPart, ? extends AbstractTrackPartEntity> dataMapper = new DataMapper<>(AbstractTrackPart.class, AbstractTrackPartEntity.class);
    private final TrackPartDao dao;
    private final ConstructionDao constructionDao;
    private final TrackPartDataMapper trackPartDataMapper;
    private final DataMapper<BusDataConfiguration, BusDataConfigurationEntity> busDataMapper = new DataMapper<>(BusDataConfiguration.class, BusDataConfigurationEntity.class);

    @Inject
    public TrackEditorServiceImpl(ConstructionServiceImpl constructionService, DeviceManager deviceManager,
                                  EventBroadcaster eventBroadcaster, TrackPartDao dao, ConstructionDao constructionDao, TrackPartDataMapper trackPartDataMapper) {
        this.constructionService = constructionService;
        this.eventBroadcaster = eventBroadcaster;
        this.deviceManager = deviceManager;
        this.dao = dao;
        this.constructionDao = constructionDao;
        this.trackPartDataMapper = trackPartDataMapper;

        deviceManager.addDeviceConnectionListener(new DeviceConnectionListener() {
            @Override
            public void connected(Device device) {
                addBusAddressListeners(device);
            }

            @Override
            public void disconnected(Device device) {
                // TODO is to late to remove, need connected device, maybe other way?
//                removeBusAddressListeners(device);
            }
        });
    }


    private void addBusAddressListeners(Device device) {
        try {
            for (Map.Entry<BusAddressIdentifier, List<BusListener>> entry : busAddressListenersOfTheCurrentTrack
                    .entrySet()) {
                device.getBusAddress(entry.getKey().getBus(),
                        (byte) entry.getKey().getAddress()).addListeners(entry.getValue());
            }
        } catch (DeviceAccessException e) {
            log.error("can't register listeners to active device", e);
        }

        try {
            for (Map.Entry<BusAddressIdentifier, FeedbackBlockListener> entry : busAddressFeedbackBlockListenersOfTheCurrentTrack
                    .entrySet()) {
                device.getFeedbackBlockModule(
                        entry.getKey().getAddress(),
                        (entry.getKey().getAddress() + 2),
                        (entry.getKey().getAddress() + 1)).addFeedbackBlockListener(entry.getValue());
            }
        } catch (DeviceAccessException e) {
            log.error("can't register feedback block listeners to active device", e);
        }
    }

    private void removeBusAddressListeners(Device device) {
        try {
            for (Map.Entry<BusAddressIdentifier, List<BusListener>> entry : busAddressListenersOfTheCurrentTrack
                    .entrySet()) {
                device.getBusAddress(entry.getKey().getBus(),
                        (byte) entry.getKey().getAddress()).removeListeners(entry.getValue());
            }
//            for (Map.Entry<BusAddressIdentifier, FeedbackBlockListener> entry : busAddressFeedbackBlockListenersOfTheCurrentTrack.entrySet()) {
//                device.getBusAddress(entry.getKey().getBus(),
//                        (byte) entry.getKey().getAddress()).removeListener(entry.getValue());
//            }
            //TODO remove feedback listeners
        } catch (DeviceAccessException e) {
            log.error("can't remove listeners to active device", e);
        }
    }

    @Override
    @Transactional
    public void saveTrack(Collection<AbstractTrackPart> trackParts) {
        ConstructionEntity constructionEntity = constructionDao.findById(constructionService.getCurrentConstruction().getId());

        // load all existing to detect deleted track parts
        List<AbstractTrackPartEntity> existingTrackParts = Lists.newArrayList(dao.findByConstructionId(constructionEntity.getId()));

        for (AbstractTrackPart trackPart : trackParts) {
            AbstractTrackPartEntity entity = trackPartDataMapper.transformTrackPart(trackPart);
            entity.setConstruction(constructionEntity);
            if (entity.getId() == null) {
                dao.create(entity);
            } else {
                dao.update(entity);
            }
        }
        // delete track part which was not updated
        for (AbstractTrackPartEntity abstractTrackPartEntity : existingTrackParts) {
            boolean found = false;
            for (AbstractTrackPart trackPart : trackParts) {
                if (abstractTrackPartEntity.getId().equals(trackPart.getId())) {
                    found = true;
                }
            }
            if (!found) {
                dao.delete(abstractTrackPartEntity);
            }
        }

    }

    @Override
    public Collection<AbstractTrackPart> loadTrack() {
        log.info("load track parts from db");
        List<AbstractTrackPartEntity> result = dao.findByConstructionId(constructionService.getCurrentConstruction().getId());
        if (!result.isEmpty()) {
            log.info("return track parts");
            return trackPartDataMapper.transformTrackPartEntities(result);
        } else {
            log.warn("the current construction is empty");
            return Lists.newArrayList();
        }
    }

    /**
     * Register the {@link net.wbz.selectrix4java.bus.consumption.BusAddressDataConsumer}s for each address of the given
     * {@link AbstractTrackPartEntity}s.
     * <p/>
     * TODO: maybe bullshit -> re-register by second browser
     *
     * @param trackParts {@link AbstractTrackPartEntity}s to register the
     *                   containing {@link BusDataConfigurationEntity}
     */
    @Override
    public void registerConsumersByConnectedDeviceForTrackParts(Collection<AbstractTrackPart> trackParts) {

        if (trackParts.size() == 0) {
            return;
        }

        // unregister existing to create new ones from given track parts
        try {
            removeBusAddressListeners(deviceManager.getConnectedDevice());
        } catch (DeviceAccessException e) {
            // ignore
        }
        busAddressListenersOfTheCurrentTrack.clear();
        busAddressFeedbackBlockListenersOfTheCurrentTrack.clear();

        // create consumers for the configuration of the track parts
        log.info("create consumers of track parts");

        // TODO: required anymore?
        List<BusDataConfigurationEntity> uniqueTrackPartConfigs = Lists.newArrayList();

        for (final AbstractTrackPart trackPart : trackParts) {

//            AbstractTrackPartEntity trackPartEntity = dao.findById(trackPart.getId());

//            registerEventConfigurationOfTrackPart(trackPart);

            registerBlockFunctionOfTrackPart(trackPart);

            registerToggleFunctionOfTrackPart(trackPart);

            registerSignal(trackPart);

            for (final BusDataConfiguration trackPartConfiguration : trackPart.getConfigurationsOfFunctions()) {

                if (trackPartConfiguration != null && trackPartConfiguration.isValid()) {

                    // TODO bus nr - remove quick hack!
//                    trackPartConfiguration.setBus(1);

                    if (trackPart instanceof Signal) {
                        // add bit listeners for the configured addresses of the signal

                        // TODO
//                        for (Map.Entry<BusAddressIdentifier, List<BusAddressBitListener>> entry : new SignalFunctionReceiver(
//                                (Signal) trackPart, eventBroadcaster).getBusAddressListeners().entrySet()) {
//                            if (!busAddressListenersOfTheCurrentTrack.containsKey(entry.getKey())) {
//                                busAddressListenersOfTheCurrentTrack.put(entry.getKey(), new ArrayList<BusListener>());
//                            }
//                            busAddressListenersOfTheCurrentTrack.get(entry.getKey()).addAll(entry.getValue());
//                        }
                    } else {
//                        // add address listener for the default toggle function of the track part
//                        BusDataConfigurationEntity busDataConfigurationEntity = busDataMapper.transformTarget(trackPartConfiguration);
//                        addBusListener(busDataConfigurationEntity, new BusAddressListener() {
//                            private boolean firstCall = true;
//
//                            @Override
//                            public void dataChanged(byte oldValue, byte newValue) {
//                                // TODO: remove quick hack for unset bus nr of old stored widgets
//
//                                // TODO
//                                trackPartConfiguration.setBus(1);
//
//                                // fire event for changed bit state of the bus address
//                                boolean bitStateChanged = BigInteger.valueOf(newValue).testBit(
//                                        trackPartConfiguration.getBit() - 1) != BigInteger.valueOf(oldValue).testBit(
//                                        trackPartConfiguration.getBit() - 1);
//
////                                BusDataConfiguration busDataConfiguration = busDataMapper.transformSource(trackPartConfiguration);
//
//                                if (firstCall || bitStateChanged) {
//                                    eventBroadcaster.fireEvent(new TrackPartStateEvent(trackPartConfiguration,
//                                            BigInteger.valueOf(newValue).testBit(trackPartConfiguration.getBit() - 1)));
//                                }
//                                firstCall = false;
//                            }
//                        });
                    }


                }
            }

        }
        // register consumers if an device is already connected
        try {
            addBusAddressListeners(deviceManager.getConnectedDevice());
        } catch (DeviceAccessException e) {
            // ignore
        }
    }

    private void registerSignal(AbstractTrackPart trackPart) {
        if (trackPart instanceof Signal) {
            // add bit listeners for the configured addresses of the signal

            // TODO
            Map<BusAddressIdentifier, List<BusAddressBitListener>> busAddressListeners = new SignalFunctionReceiver(
                    (Signal) trackPart, eventBroadcaster).getBusAddressListeners();

            for (Map.Entry<BusAddressIdentifier, List<BusAddressBitListener>> entry : busAddressListeners.entrySet()) {
                if (!busAddressListenersOfTheCurrentTrack.containsKey(entry.getKey())) {
                    busAddressListenersOfTheCurrentTrack.put(entry.getKey(), new ArrayList<BusListener>());
                }
                busAddressListenersOfTheCurrentTrack.get(entry.getKey()).addAll(entry.getValue());
            }
        }
    }

    private void registerToggleFunctionOfTrackPart(AbstractTrackPart trackPart) {
        if (trackPart instanceof HasToggleFunction) {
            HasToggleFunction trackPartConfiguration = (HasToggleFunction) trackPart;
            // add address listener for the default toggle function of the track part
//            BusDataConfigurationEntity busDataConfigurationEntity = busDataMapper.transformTarget(trackPartConfiguration);
            final BusDataConfiguration toggleFunction = trackPartConfiguration.getToggleFunction();
            if (toggleFunction != null && toggleFunction.isValid()) {
                addBusListener(toggleFunction, new BusAddressListener() {
                    private boolean firstCall = true;

                    @Override
                    public void dataChanged(byte oldValue, byte newValue) {
                        // TODO: remove quick hack for unset bus nr of old stored widgets

                        // TODO
//                    trackPartConfiguration.setBus(1);

                        // fire event for changed bit state of the bus address
                        boolean bitStateChanged = BigInteger.valueOf(newValue).testBit(
                                toggleFunction.getBit() - 1) != BigInteger.valueOf(oldValue).testBit(
                                toggleFunction.getBit() - 1);

//                                BusDataConfiguration busDataConfiguration = busDataMapper.transformSource(trackPartConfiguration);

                        if (firstCall || bitStateChanged) {
                            eventBroadcaster.fireEvent(new TrackPartStateEvent(toggleFunction,
                                    BigInteger.valueOf(newValue).testBit(toggleFunction.getBit() - 1)));
                        }
                        firstCall = false;
                    }
                });

                registerEventConfigurationOfTrackPart(trackPartConfiguration);
            }
        }
    }

    private void registerBlockFunctionOfTrackPart(final AbstractTrackPart trackPart) {
        // configure feedback blocks

        if (trackPart.getBlockFunction() != null && trackPart.getBlockFunction().isValid()) {

            final BusDataConfiguration trackPartConfiguration = trackPart.getBlockFunction();

            BusAddressIdentifier busAddressIdentifier = new BusAddressIdentifier(trackPartConfiguration
                    .getBus(), trackPartConfiguration.getAddress());
            if (!busAddressFeedbackBlockListenersOfTheCurrentTrack.containsKey(busAddressIdentifier)) {
                busAddressFeedbackBlockListenersOfTheCurrentTrack.put(busAddressIdentifier,
                        new FeedbackBlockListener() {
                            @Override
                            public void trainEnterBlock(int blockNumber, int trainAddress,
                                                        boolean drivingDirection) {
                                eventBroadcaster.fireEvent(new FeedbackBlockEvent(
                                        FeedbackBlockEvent.STATE.ENTER,
                                        trackPart.getBlockFunction().getBus(),
                                        trackPart.getBlockFunction().getAddress(),
                                        blockNumber, trainAddress, drivingDirection));
                            }

                            @Override
                            public void trainLeaveBlock(int blockNumber, int trainAddress,
                                                        boolean drivingDirection) {
                                eventBroadcaster.fireEvent(new FeedbackBlockEvent(
                                        FeedbackBlockEvent.STATE.EXIT,
                                        trackPart.getBlockFunction().getBus(),
                                        trackPart.getBlockFunction().getAddress(),
                                        blockNumber, trainAddress, drivingDirection));
                            }

                            @Override
                            public void blockOccupied(int blockNr) {
                                fireBlockEvent(true);
                            }

                            @Override
                            public void blockFreed(int blockNr) {
                                fireBlockEvent(false);
                            }

                            private void fireBlockEvent(boolean bitState) {
                                eventBroadcaster.fireEvent(new TrackPartBlockEvent(new BusDataConfiguration(
                                        trackPart.getBlockFunction().getBus(),
                                        trackPart.getBlockFunction().getAddress(),
                                        trackPart.getBlockFunction().getBit(),
                                        bitState
                                ), bitState ? TrackPartBlockEvent.STATE.USED : TrackPartBlockEvent.STATE.FREE));
                            }
                        });
            }
        }
    }

    private void addBusListener(BusDataConfiguration trackPartConfiguration, BusListener listener) {
        BusAddressIdentifier busAddressIdentifier = new BusAddressIdentifier(trackPartConfiguration.getBus(),
                trackPartConfiguration.getAddress());
        if (!busAddressListenersOfTheCurrentTrack.containsKey(busAddressIdentifier)) {
            busAddressListenersOfTheCurrentTrack.put(busAddressIdentifier, new ArrayList<BusListener>());
        }
        busAddressListenersOfTheCurrentTrack.get(busAddressIdentifier).add(listener);
    }

    private void registerEventConfigurationOfTrackPart(final HasToggleFunction toggleFunctionEntity) {
//        if (trackPart instanceof HasToggleFunction) {
//            HasToggleFunction toggleFunctionEntity = (HasToggleFunction) trackPart;

        // TODO ? && toggleFunctionEntity.getEventConfiguration().isActive() ??
        EventConfiguration eventConfiguration = toggleFunctionEntity.getEventConfiguration();
        if (eventConfiguration != null && eventConfiguration.isActive()) {

            // add state 'ON'
            final BusDataConfiguration stateOnConfig = eventConfiguration.getStateOnConfig();
            if (stateOnConfig.isValid()) {
                addBusListener(stateOnConfig, new BusAddressBitListener(stateOnConfig.getBit()) {
                    @Override
                    public void bitChanged(boolean oldValue, boolean newValue) {
                        if ((newValue && stateOnConfig.getBitState())
                                || (!newValue && !stateOnConfig.getBitState())) {
                            try {
//                                BusDataConfigurationEntity trackPartConfig = trackPart.getDefaultToggleFunctionConfig();
                                switchToggleFunction(toggleFunctionEntity, true);
//                                deviceManager.getConnectedDevice().getBusAddress(trackPartConfig.getBus(),
//                                        trackPartConfig.getAddress().byteValue()).setBit(trackPartConfig.getBit()).send();
                            } catch (DeviceAccessException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }

            // add state 'OFF'
            final BusDataConfiguration stateOffConfig = eventConfiguration.getStateOffConfig();
            if (stateOffConfig.isValid()) {
                addBusListener(stateOffConfig, new BusAddressBitListener(stateOffConfig.getBit()) {
                    @Override
                    public void bitChanged(boolean oldValue, boolean newValue) {
                        if ((newValue && stateOffConfig.getBitState())
                                || (!newValue && !stateOffConfig.getBitState())) {

                            try {
                                switchToggleFunction(toggleFunctionEntity, false);
                            } catch (DeviceAccessException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        }
//        }
    }

    private void switchToggleFunction(HasToggleFunction toggleFunctionEntity, boolean stateOn) throws DeviceAccessException {
        BusDataConfiguration trackPartConfig = toggleFunctionEntity.getToggleFunction();
        if (trackPartConfig != null && trackPartConfig.isValid()) {
            BusAddress busAddress = deviceManager.getConnectedDevice().getBusAddress(trackPartConfig.getBus(),
                    trackPartConfig.getAddress().byteValue());
            if (stateOn) {
                busAddress.setBit(trackPartConfig.getBit());
            } else {
                busAddress.clearBit(trackPartConfig.getBit());
            }
            busAddress.send();
        }
    }


}
