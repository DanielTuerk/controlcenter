package net.wbz.moba.controlcenter.web.server.editor;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;
import net.sf.gilead.core.PersistentBeanManager;
import net.sf.gilead.gwt.PersistentRemoteService;
import net.wbz.moba.controlcenter.web.server.EventBroadcaster;
import net.wbz.moba.controlcenter.web.server.constrution.ConstructionServiceImpl;
import net.wbz.moba.controlcenter.web.shared.bus.FeedbackBlockEvent;
import net.wbz.moba.controlcenter.web.shared.constrution.Construction;
import net.wbz.moba.controlcenter.web.shared.editor.TrackEditorService;
import net.wbz.moba.controlcenter.web.shared.track.model.Configuration;
import net.wbz.moba.controlcenter.web.shared.track.model.Signal;
import net.wbz.moba.controlcenter.web.shared.track.model.TrackPart;
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
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
@Singleton
public class TrackEditorServiceImpl extends PersistentRemoteService implements TrackEditorService {

    private static final Logger log = LoggerFactory.getLogger(TrackEditorServiceImpl.class);

    private final ConstructionServiceImpl constructionService;

    private final Map<BusAddressIdentifier, List<BusListener>> busAddressListenersOfTheCurrentTrack = Maps.newConcurrentMap();
    private final Map<BusAddressIdentifier, FeedbackBlockListener> busAddressFeedbackBlockListenersOfTheCurrentTrack = Maps.newConcurrentMap();
    private final DeviceManager deviceManager;
    private final EventBroadcaster eventBroadcaster;
    private final Provider<EntityManager> entityManagerProvider;

    @Inject
    public TrackEditorServiceImpl(ConstructionServiceImpl constructionService, DeviceManager deviceManager,
                                  EventBroadcaster eventBroadcaster, Provider<EntityManager> entityManager,
                                  PersistentBeanManager persistentBeanManager) {
        this.constructionService = constructionService;
        this.eventBroadcaster = eventBroadcaster;
        this.deviceManager = deviceManager;
        this.entityManagerProvider = entityManager;

        setBeanManager(persistentBeanManager);

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
            for (Map.Entry<BusAddressIdentifier, List<BusListener>> entry : busAddressListenersOfTheCurrentTrack.entrySet()) {
                device.getBusAddress(entry.getKey().getBus(),
                        (byte) entry.getKey().getAddress()).addListeners(entry.getValue());
            }
        } catch (DeviceAccessException e) {
            log.error("can't register listeners to active device", e);
        }

        try {
            for (Map.Entry<BusAddressIdentifier, FeedbackBlockListener> entry : busAddressFeedbackBlockListenersOfTheCurrentTrack.entrySet()) {
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
            for (Map.Entry<BusAddressIdentifier, List<BusListener>> entry : busAddressListenersOfTheCurrentTrack.entrySet()) {
                device.getBusAddress(entry.getKey().getBus(),
                        (byte) entry.getKey().getAddress()).removeListeners(entry.getValue());
            }
        } catch (DeviceAccessException e) {
            log.error("can't remove listeners to active device", e);
        }
    }

    @Override
    @Transactional
    public void saveTrack(TrackPart[] trackParts) {
        Construction currentConstruction = constructionService.getCurrentConstruction();
        for (TrackPart trackPart : trackParts) {
            trackPart.setConstructionId(currentConstruction.getId());
            EntityManager entityManager = this.entityManagerProvider.get();
            if(!entityManager.contains(trackPart.getGridPosition())) {
                entityManager.persist(trackPart.getGridPosition());
            }
            entityManager.persist(trackPart);
        }
    }

    @Override
    public TrackPart[] loadTrack() {
        log.info("load track parts from db");

        Query typedQuery = entityManagerProvider.get().createQuery(
                "SELECT x FROM TrackPart x where x.constructionId=:constructionId");
        typedQuery.setParameter("constructionId", constructionService.getCurrentConstruction().getId());

        List<TrackPart> result = typedQuery.getResultList();
        if (result.size() > 0) {
            log.info("return track parts");
            return result.toArray(new TrackPart[result.size()]);
        } else {
            log.warn("the current construction is empty");
            return new TrackPart[0];
        }
    }

    /**
     * Register the {@link net.wbz.selectrix4java.bus.consumption.BusDataConsumer}s for each address of the given
     * {@link net.wbz.moba.controlcenter.web.shared.track.model.TrackPart}s.
     * <p/>
     * TODO: maybe bullshit -> reregister by second browser
     *
     * @param trackParts {@link net.wbz.moba.controlcenter.web.shared.track.model.TrackPart}s to register the
     *                   containing {@link net.wbz.moba.controlcenter.web.shared.track.model.Configuration}
     */
    public void registerConsumersByConnectedDeviceForTrackParts(TrackPart[] trackParts) {

        if(trackParts.length == 0) {
            return;
        }

        //unregister existing to create new ones from given track parts
        try {
            removeBusAddressListeners(deviceManager.getConnectedDevice());
        } catch (DeviceAccessException e) {
            //ignore
        }
        busAddressListenersOfTheCurrentTrack.clear();

        // create consumers for the configuration of the track parts
        log.info("create consumers of track parts");

        //TODO: required anymore?
        List<Configuration> uniqueTrackPartConfigs = Lists.newArrayList();

        for (final TrackPart trackPart : trackParts) {

            registerEventConfigurationOfTrackPart(trackPart);

            for (final Configuration trackPartConfiguration : trackPart.getConfigurationsOfFunctions()) {

                if (trackPartConfiguration != null && trackPartConfiguration.isValid()) {

                    //TODO bus nr - remove quick hack!
                    trackPartConfiguration.setBus(1);

                    if (trackPart instanceof Signal) {
                        // add bit listeners for the configured addresses of the signal
                        for (Map.Entry<BusAddressIdentifier, List<BusAddressBitListener>> entry :
                                new SignalFunctionReceiver((Signal) trackPart, eventBroadcaster).getBusAddressListeners().entrySet()) {
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
                                        trackPartConfiguration.getBit() - 1)
                                        != BigInteger.valueOf(oldValue).testBit(trackPartConfiguration.getBit() - 1);

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
                        BusAddressIdentifier busAddressIdentifier = new BusAddressIdentifier(trackPartConfiguration.getBus(), trackPartConfiguration.getAddress());
                        if (!busAddressFeedbackBlockListenersOfTheCurrentTrack.containsKey(busAddressIdentifier)) {
                            busAddressFeedbackBlockListenersOfTheCurrentTrack.put(busAddressIdentifier, new FeedbackBlockListener() {

                                @Override
                                public void trainEnterBlock(int blockNumber, int trainAddress, boolean drivingDirection) {
                                    eventBroadcaster.fireEvent(new FeedbackBlockEvent(FeedbackBlockEvent.STATE.ENTER,
                                            trackPart.getDefaultBlockFunctionConfig().getBus(),
                                            trackPart.getDefaultBlockFunctionConfig().getAddress(),
                                            blockNumber, trainAddress, drivingDirection));
                                }

                                @Override
                                public void trainLeaveBlock(int blockNumber, int trainAddress, boolean drivingDirection) {
                                    eventBroadcaster.fireEvent(new FeedbackBlockEvent(FeedbackBlockEvent.STATE.EXIT,
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
            //ignore
        }
    }

    private void addBusListener(Configuration trackPartConfiguration, BusListener listener) {
        BusAddressIdentifier busAddressIdentifier = new BusAddressIdentifier(trackPartConfiguration.getBus(), trackPartConfiguration.getAddress());
        if (!busAddressListenersOfTheCurrentTrack.containsKey(busAddressIdentifier)) {
            busAddressListenersOfTheCurrentTrack.put(busAddressIdentifier, new ArrayList<BusListener>());
        }
        busAddressListenersOfTheCurrentTrack.get(busAddressIdentifier).add(listener);
    }

    private void registerEventConfigurationOfTrackPart(final TrackPart trackPart) {
        if (trackPart.hasActiveEventConfiguration()) {

            // add state 'ON'
            final Configuration stateOnConfig = trackPart.getEventConfiguration().getStateOnConfig();
            if (stateOnConfig.isValid()) {
                addBusListener(stateOnConfig, new BusAddressBitListener(stateOnConfig.getBit()) {
                    @Override
                    public void bitChanged(boolean oldValue, boolean newValue) {
                        if ((newValue && stateOnConfig.isBitState())
                                || (!newValue && !stateOnConfig.isBitState())) {
                            try {
                                Configuration trackPartConfig = trackPart.getDefaultToggleFunctionConfig();
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
            final Configuration stateOffConfig = trackPart.getEventConfiguration().getStateOffConfig();
            if (stateOffConfig.isValid()) {
                addBusListener(stateOffConfig, new BusAddressBitListener(stateOffConfig.getBit()) {
                    @Override
                    public void bitChanged(boolean oldValue, boolean newValue) {
                        if ((newValue && stateOffConfig.isBitState())
                                || (!newValue && !stateOffConfig.isBitState())) {

                            try {
                                Configuration trackPartConfig = trackPart.getDefaultToggleFunctionConfig();
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
