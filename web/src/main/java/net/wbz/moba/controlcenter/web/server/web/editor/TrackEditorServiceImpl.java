package net.wbz.moba.controlcenter.web.server.web.editor;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;
import net.wbz.moba.controlcenter.web.server.EventBroadcaster;
import net.wbz.moba.controlcenter.web.server.persist.construction.track.*;
import net.wbz.moba.controlcenter.web.server.web.constrution.ConstructionServiceImpl;
import net.wbz.moba.controlcenter.web.shared.bus.FeedbackBlockEvent;
import net.wbz.moba.controlcenter.web.shared.constrution.Construction;
import net.wbz.moba.controlcenter.web.shared.editor.TrackEditorService;
import net.wbz.moba.controlcenter.web.shared.viewer.TrackPartStateEvent;
import net.wbz.selectrix4java.block.FeedbackBlockListener;
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
import javax.inject.Provider;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Daniel Tuerk
 */
@Singleton
public class TrackEditorServiceImpl  extends RemoteServiceServlet implements TrackEditorService {

    private static final Logger log = LoggerFactory.getLogger(TrackEditorServiceImpl.class);

    private final ConstructionServiceImpl constructionService;

    private final Map<BusAddressIdentifier, List<BusListener>> busAddressListenersOfTheCurrentTrack = Maps
            .newConcurrentMap();
    private final Map<BusAddressIdentifier, FeedbackBlockListener> busAddressFeedbackBlockListenersOfTheCurrentTrack =
            Maps.newConcurrentMap();
    private final DeviceManager deviceManager;
    private final EventBroadcaster eventBroadcaster;
    private final Provider<EntityManager> entityManagerProvider;

    @Inject
    public TrackEditorServiceImpl(ConstructionServiceImpl constructionService, DeviceManager deviceManager,
                                  EventBroadcaster eventBroadcaster, Provider<EntityManager> entityManager) {
        this.constructionService = constructionService;
        this.eventBroadcaster = eventBroadcaster;
        this.deviceManager = deviceManager;
        this.entityManagerProvider = entityManager;

        deviceManager.addDeviceConnectionListener(new DeviceConnectionListener() {
            @Override
            public void connected(Device device) {
                addBusAddressListeners(device);
            }

            @Override
            public void disconnected(Device device) {
                removeBusAddressListeners(device);
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
        } catch (DeviceAccessException e) {
            log.error("can't remove listeners to active device", e);
        }
    }

    @Transactional
    public void saveTrack(List<TrackPartEntity> trackParts) {
        EntityManager entityManager = this.entityManagerProvider.get();

        Construction currentConstruction = constructionService.getCurrentConstruction();
        for (TrackPartEntity trackPart : trackParts) {
            //TODO
            trackPart.setConstruction(currentConstruction);

            if (entityManager.find(GridPositionEntity.class, trackPart.getGridPositionEntity().getId()) == null) {
                entityManager.persist(trackPart.getGridPositionEntity());
            } else {
                trackPart.setGridPositionEntity(entityManager.merge(trackPart.getGridPositionEntity()));
            }

            for (TrackPartFunctionEntity trackPartFunction : trackPart.getFunctions()) {

                // trackPartFunction.setTrackPart(trackPart);

                saveOrUpdateConfiguration(entityManager, trackPartFunction.getConfiguration());

                if (entityManager.find(TrackPartFunctionEntity.class, trackPartFunction.getId()) == null) {
                    entityManager.persist(trackPartFunction);
                } else {
                    entityManager.merge(trackPartFunction);
                }
            }

            EventConfigurationEntity eventConfigurationEntity = trackPart.getEventConfiguration();
            if (eventConfigurationEntity != null) {

                saveOrUpdateConfiguration(entityManager, eventConfigurationEntity.getStateOnConfig());
                saveOrUpdateConfiguration(entityManager, eventConfigurationEntity.getStateOffConfig());

                if (entityManager.find(EventConfigurationEntity.class, eventConfigurationEntity.getId()) == null) {
                    entityManager.persist(eventConfigurationEntity);
                } else {
                    entityManager.merge(eventConfigurationEntity);
                }
            }

            entityManager.persist(entityManager.merge(trackPart));
        }
    }

    private void saveOrUpdateConfiguration(EntityManager entityManager, TrackPartConfigurationEntity configuration) {
        if (configuration != null) {
            if (entityManager.find(TrackPartConfigurationEntity.class, configuration.getId()) == null) {
                entityManager.persist(configuration);
            } else {
                entityManager.merge(configuration);
            }
        }
    }
    @Transactional
    public List<TrackPartEntity> loadTrack() {
        log.info("load track parts from db");

        Query typedQuery = entityManagerProvider.get().createQuery(
                "SELECT x FROM TrackPartEntity x where x.construction=:construction");
        typedQuery.setParameter("construction", constructionService.getCurrentConstruction());

        List<TrackPartEntity> result = typedQuery.getResultList();
        if (result.size() > 0) {
            log.info("return track parts");
            return result;
        } else {
            log.warn("the current construction is empty");
            return Lists.newArrayList();
        }
    }

    /**
     * Register the {@link net.wbz.selectrix4java.bus.consumption.BusAddressDataConsumer}s for each address of the given
     * {@link TrackPartEntity}s.
     * <p/>
     * TODO: maybe bullshit -> re-register by second browser
     *
     * @param trackParts {@link TrackPartEntity}s to register the
     *                   containing {@link TrackPartConfigurationEntity}
     */ @Transactional
    public void registerConsumersByConnectedDeviceForTrackParts(List<TrackPartEntity> trackParts) {

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

        // create consumers for the configuration of the track parts
        log.info("create consumers of track parts");

        // TODO: required anymore?
        List<TrackPartConfigurationEntity> uniqueTrackPartConfigs = Lists.newArrayList();

        for (final TrackPartEntity trackPart : trackParts) {

            registerEventConfigurationOfTrackPart(trackPart);

            for (final TrackPartConfigurationEntity trackPartConfiguration : trackPart.getConfigurationsOfFunctions()) {

                if (trackPartConfiguration != null && trackPartConfiguration.isValid()) {

                    // TODO bus nr - remove quick hack!
                    trackPartConfiguration.setBus(1);

                    if (trackPart instanceof SignalEntity) {
                        // add bit listeners for the configured addresses of the signal
                        for (Map.Entry<BusAddressIdentifier, List<BusAddressBitListener>> entry : new SignalFunctionReceiver(
                                (SignalEntity) trackPart, eventBroadcaster).getBusAddressListeners().entrySet()) {
                            if (!busAddressListenersOfTheCurrentTrack.containsKey(entry.getKey())) {
                                busAddressListenersOfTheCurrentTrack.put(entry.getKey(), new ArrayList<BusListener>());
                            }
                            busAddressListenersOfTheCurrentTrack.get(entry.getKey()).addAll(entry.getValue());
                        }
                    } else {
                        // add address listener for the default toggle function of the track part
                        addBusListener(trackPartConfiguration, new BusAddressListener() {
                            private boolean firstCall = true;

                            @Override
                            public void dataChanged(byte oldValue, byte newValue) {
                                // TODO: remove quick hack for unset bus nr of old stored widgets
                                trackPartConfiguration.setBus(1);

                                // fire event for changed bit state of the bus address
                                boolean bitStateChanged = BigInteger.valueOf(newValue).testBit(
                                        trackPartConfiguration.getBit() - 1) != BigInteger.valueOf(oldValue).testBit(
                                        trackPartConfiguration.getBit() - 1);

                                if (firstCall || bitStateChanged) {
                                    eventBroadcaster.fireEvent(new TrackPartStateEvent(trackPartConfiguration,
                                            BigInteger.valueOf(newValue).testBit(trackPartConfiguration.getBit() - 1)));
                                }
                                firstCall = false;
                            }
                        });
                    }

                    // configure feedback blocks
                    if (trackPart.getDefaultBlockFunctionConfig().isValid()) {
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
                                                    trackPart.getDefaultBlockFunctionConfig().getBus(),
                                                    trackPart.getDefaultBlockFunctionConfig().getAddress(),
                                                    blockNumber, trainAddress, drivingDirection));
                                        }

                                        @Override
                                        public void trainLeaveBlock(int blockNumber, int trainAddress,
                                                                    boolean drivingDirection) {
                                            eventBroadcaster.fireEvent(new FeedbackBlockEvent(
                                                    FeedbackBlockEvent.STATE.EXIT,
                                                    trackPart.getDefaultBlockFunctionConfig().getBus(),
                                                    trackPart.getDefaultBlockFunctionConfig().getAddress(),
                                                    blockNumber, trainAddress, drivingDirection));
                                        }

                                        @Override
                                        public void blockOccupied(int blockNr) {
                                        }

                                        @Override
                                        public void blockFreed(int blockNr) {
                                        }
                                    });

                        }
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

    private void addBusListener(TrackPartConfigurationEntity trackPartConfiguration, BusListener listener) {
        BusAddressIdentifier busAddressIdentifier = new BusAddressIdentifier(trackPartConfiguration.getBus(),
                trackPartConfiguration.getAddress());
        if (!busAddressListenersOfTheCurrentTrack.containsKey(busAddressIdentifier)) {
            busAddressListenersOfTheCurrentTrack.put(busAddressIdentifier, new ArrayList<BusListener>());
        }
        busAddressListenersOfTheCurrentTrack.get(busAddressIdentifier).add(listener);
    }

    private void registerEventConfigurationOfTrackPart(final TrackPartEntity trackPart) {
        if (trackPart.hasActiveEventConfiguration()) {

            // add state 'ON'
            final TrackPartConfigurationEntity stateOnConfig = trackPart.getEventConfiguration().getStateOnConfig();
            if (stateOnConfig.isValid()) {
                addBusListener(stateOnConfig, new BusAddressBitListener(stateOnConfig.getBit()) {
                    @Override
                    public void bitChanged(boolean oldValue, boolean newValue) {
                        if ((newValue && stateOnConfig.isBitState())
                                || (!newValue && !stateOnConfig.isBitState())) {
                            try {
                                TrackPartConfigurationEntity trackPartConfig = trackPart.getDefaultToggleFunctionConfig();
                                deviceManager.getConnectedDevice().getBusAddress(trackPartConfig.getBus(),
                                        (byte) trackPartConfig.getAddress()).setBit(trackPartConfig.getBit()).send();
                            } catch (DeviceAccessException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }

            // add state 'OFF'
            final TrackPartConfigurationEntity stateOffConfig = trackPart.getEventConfiguration().getStateOffConfig();
            if (stateOffConfig.isValid()) {
                addBusListener(stateOffConfig, new BusAddressBitListener(stateOffConfig.getBit()) {
                    @Override
                    public void bitChanged(boolean oldValue, boolean newValue) {
                        if ((newValue && stateOffConfig.isBitState())
                                || (!newValue && !stateOffConfig.isBitState())) {

                            try {
                                TrackPartConfigurationEntity trackPartConfig = trackPart.getDefaultToggleFunctionConfig();
                                deviceManager.getConnectedDevice().getBusAddress(trackPartConfig.getBus(),
                                        (byte) trackPartConfig.getAddress()).clearBit(trackPartConfig.getBit()).send();
                            } catch (DeviceAccessException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        }
    }


}
