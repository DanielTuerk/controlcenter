package net.wbz.moba.controlcenter.web.server.web.editor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;

import net.wbz.moba.controlcenter.web.server.EventBroadcaster;
import net.wbz.moba.controlcenter.web.server.persist.construction.ConstructionDao;
import net.wbz.moba.controlcenter.web.server.persist.construction.ConstructionEntity;
import net.wbz.moba.controlcenter.web.server.persist.construction.track.AbstractTrackPartEntity;
import net.wbz.moba.controlcenter.web.server.persist.construction.track.TrackBlockDao;
import net.wbz.moba.controlcenter.web.server.persist.construction.track.TrackBlockEntity;
import net.wbz.moba.controlcenter.web.server.persist.construction.track.TrackPartDao;
import net.wbz.moba.controlcenter.web.server.web.DataMapper;
import net.wbz.moba.controlcenter.web.server.web.TrackPartDataMapper;
import net.wbz.moba.controlcenter.web.server.web.editor.block.BusAddressIdentifier;
import net.wbz.moba.controlcenter.web.server.web.editor.block.SignalBlockRegistry;
import net.wbz.moba.controlcenter.web.server.web.editor.block.TrackBlockRegistry;
import net.wbz.moba.controlcenter.web.shared.constrution.Construction;
import net.wbz.moba.controlcenter.web.shared.track.model.AbstractTrackPart;
import net.wbz.moba.controlcenter.web.shared.track.model.BusDataConfiguration;
import net.wbz.moba.controlcenter.web.shared.track.model.EventConfiguration;
import net.wbz.moba.controlcenter.web.shared.track.model.HasToggleFunction;
import net.wbz.moba.controlcenter.web.shared.track.model.Signal;
import net.wbz.moba.controlcenter.web.shared.track.model.TrackBlock;
import net.wbz.moba.controlcenter.web.shared.viewer.TrackPartStateEvent;
import net.wbz.selectrix4java.bus.BusAddress;
import net.wbz.selectrix4java.bus.BusAddressBitListener;
import net.wbz.selectrix4java.bus.BusListener;
import net.wbz.selectrix4java.device.Device;
import net.wbz.selectrix4java.device.DeviceAccessException;
import net.wbz.selectrix4java.device.DeviceConnectionListener;
import net.wbz.selectrix4java.device.DeviceManager;

/**
 * @author Daniel Tuerk
 */
@Singleton
public class TrackManager {

    private static final Logger log = LoggerFactory.getLogger(TrackManager.class);

    /**
     * Map for all {@link BusListener}s of a single {@link BusAddressIdentifier} to avoid duplicated listeners to
     * register and to call from {@link net.wbz.selectrix4java.data.BusDataChannel}.
     */
    private final Map<BusAddressIdentifier, List<BusListener>> busAddressListenersOfTheCurrentTrack = Maps
            .newConcurrentMap();

    private final DeviceManager deviceManager;
    private final EventBroadcaster eventBroadcaster;
    private final TrackPartDao trackPartDao;
    private final ConstructionDao constructionDao;
    private final TrackPartDataMapper trackPartDataMapper;
    private final TrackBlockDao trackBlockDao;
    private final DataMapper<TrackBlock, TrackBlockEntity> trackBlockDataMapper = new DataMapper<>(TrackBlock.class,
            TrackBlockEntity.class);
    private final TrackBlockRegistry trackBlockRegistry;
    private final SignalBlockRegistry signalBlockRegistry;

    /**
     * Cached track of current {@link Construction}.
     */
    private final Collection<AbstractTrackPart> cachedData = Lists.newArrayList();
    /**
     * Cached {@link TrackBlock}s of current {@link Construction}.
     */
    private final Collection<TrackBlock> cachedTrackBlocks = Lists.newArrayList();
    /**
     * The current {@link Construction}.
     */
    private Construction currentConstruction;

    @Inject
    public TrackManager(DeviceManager deviceManager, EventBroadcaster eventBroadcaster, TrackPartDao trackPartDao,
            ConstructionDao constructionDao, TrackPartDataMapper trackPartDataMapper, TrackBlockDao trackBlockDao,
            TrackBlockRegistry trackBlockRegistry, SignalBlockRegistry signalBlockRegistry) {
        this.eventBroadcaster = eventBroadcaster;
        this.deviceManager = deviceManager;
        this.trackPartDao = trackPartDao;
        this.constructionDao = constructionDao;
        this.trackPartDataMapper = trackPartDataMapper;
        this.trackBlockDao = trackBlockDao;
        this.trackBlockRegistry = trackBlockRegistry;
        this.signalBlockRegistry = signalBlockRegistry;

        deviceManager.addDeviceConnectionListener(new DeviceConnectionListener() {
            @Override
            public void connected(Device device) {
                registerConsumersByConnectedDeviceForTrackParts();
            }

            @Override
            public void disconnected(Device device) {
                /*
                 * Nothing to do, the device reset itself.
                 * Local listeners can be reused for next connected device.
                 */
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
            trackBlockRegistry.registerListeners(device);
        } catch (DeviceAccessException e) {
            log.error("can't register track block listeners to active device", e);
        }

        try {
            signalBlockRegistry.registerListeners(device);
        } catch (DeviceAccessException e) {
            log.error("can't register signal block listeners to active device", e);
        }
    }

    private void removeBusAddressListeners(Device device) {
        try {
            for (Map.Entry<BusAddressIdentifier, List<BusListener>> entry : busAddressListenersOfTheCurrentTrack
                    .entrySet()) {
                device.getBusAddress(entry.getKey().getBus(),
                        (byte) entry.getKey().getAddress()).removeListeners(entry.getValue());
            }

            trackBlockRegistry.removeListeners(device);
            signalBlockRegistry.removeListeners(device);

        } catch (DeviceAccessException e) {
            log.error("can't remove listeners to active device", e);
        }
    }

    @Transactional
    public void saveTrack(Collection<AbstractTrackPart> trackParts) {
        ConstructionEntity constructionEntity = getCurrentConstruction();

        // load all existing to detect deleted track parts
        List<AbstractTrackPartEntity> existingTrackParts = Lists.newArrayList(trackPartDao.findByConstructionId(
                constructionEntity.getId()));

        for (AbstractTrackPart trackPart : trackParts) {
            AbstractTrackPartEntity entity = trackPartDataMapper.transformTrackPart(trackPart);
            entity.setConstruction(constructionEntity);
            if (entity.getId() == null) {
                trackPartDao.create(entity);
            } else {
                trackPartDao.update(entity);
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
                trackPartDao.delete(abstractTrackPartEntity);
            }
        }

        // reload track and register for connected device
        loadData();
    }

    private ConstructionEntity getCurrentConstruction() {
        return constructionDao.findById(currentConstruction.getId());
    }

    @Transactional
    public void deleteTrackBlock(TrackBlock trackBlock) {
        List<AbstractTrackPartEntity> byBlockId = trackPartDao.findByBlockId(trackBlock.getId());
        if (!byBlockId.isEmpty()) {
            for (AbstractTrackPartEntity trackPartEntity : byBlockId) {
                trackPartEntity.setTrackBlock(null);
                trackPartDao.update(trackPartEntity);
            }
            trackPartDao.flush();
        }
        trackBlockDao.delete(trackBlockDataMapper.transformTarget(trackBlock));
        trackBlockDao.flush();
        // reload data
        loadTrackBlocksData();
        loadTrackPartData();
    }

    @Transactional
    public void saveTrackBlocks(Collection<TrackBlock> trackBlocks) {
        for (TrackBlock trackBlock : trackBlocks) {
            trackBlock.setConstruction(currentConstruction);
            TrackBlockEntity entity = trackBlockDataMapper.transformTarget(trackBlock);
            if (entity.getId() == null) {
                trackBlockDao.create(entity);
            } else {
                trackBlockDao.update(entity);
            }
        }
        // reload data
        loadData();
    }

    public Collection<AbstractTrackPart> getTrack() {
        return cachedData;
    }

    public Collection<TrackBlock> loadTrackBlocks() {
        return cachedTrackBlocks;
    }

    private void loadData() {
        loadTrackBlocksData();
        loadTrackPartData();
    }

    private void loadTrackPartData() {
        cachedData.clear();

        log.info("load track parts from db");
        if (currentConstruction != null) {
            List<AbstractTrackPartEntity> result = trackPartDao.findByConstructionId(currentConstruction.getId());
            if (!result.isEmpty()) {
                log.info("return track parts");
                cachedData.addAll(trackPartDataMapper.transformTrackPartEntities(result));

                if (deviceManager.isConnected()) {
                    registerConsumersByConnectedDeviceForTrackParts();
                }

            } else {
                log.warn("the current construction is empty");
            }
        }
    }

    private void loadTrackBlocksData() {
        cachedTrackBlocks.clear();
        if (currentConstruction != null) {
            List<TrackBlockEntity> trackBlocks = trackBlockDao.findByConstructionId(currentConstruction.getId());
            if (!trackBlocks.isEmpty()) {
                cachedTrackBlocks.addAll(trackBlockDataMapper.transformSource(trackBlocks));
            }
        }
    }

    /**
     * Register the {@link net.wbz.selectrix4java.bus.consumption.BusAddressDataConsumer}s for each address of the given
     * {@link AbstractTrackPartEntity}s.
     * <p/>
     * TODO auto register for trackpart of current track
     * TODO re-register by changed trackparts (event by track editor service?, or better by manager/trackPartDao)
     */
    private void registerConsumersByConnectedDeviceForTrackParts() {

        // unregister existing to create new ones from given track parts
        try {
            removeBusAddressListeners(deviceManager.getConnectedDevice());
        } catch (DeviceAccessException e) {
            // ignore
        }

        trackBlockRegistry.initBlocks(cachedTrackBlocks);

        log.info("create consumers of track parts");
        busAddressListenersOfTheCurrentTrack.clear();
        Collection<AbstractTrackPart> trackParts = cachedData;
        if (trackParts.size() == 0) {
            log.info("track is empty, skip register consumers");
            return;
        }

        List<Signal> signals = Lists.newArrayList();
        // create consumers for the configuration of the track parts
        for (final AbstractTrackPart trackPart : trackParts) {
            registerToggleFunctionOfTrackPart(trackPart);
            if (trackPart instanceof Signal) {
                Signal signal = (Signal) trackPart;
                registerSignalFunction(signal);
                signals.add(signal);
            }

        }

        // register signal blocks
        signalBlockRegistry.initBlocks(signals);

        // register consumers if an device is already connected
        try {
            addBusAddressListeners(deviceManager.getConnectedDevice());
        } catch (DeviceAccessException e) {
            // ignore
        }
    }

    private void registerSignalFunction(Signal signal) {
        Map<BusAddressIdentifier, List<BusAddressBitListener>> busAddressListeners = new SignalFunctionReceiver(
                signal, eventBroadcaster).getBusAddressListeners();
        for (Map.Entry<BusAddressIdentifier, List<BusAddressBitListener>> entry : busAddressListeners.entrySet()) {
            if (!busAddressListenersOfTheCurrentTrack.containsKey(entry.getKey())) {
                busAddressListenersOfTheCurrentTrack.put(entry.getKey(), new ArrayList<BusListener>());
            }
            busAddressListenersOfTheCurrentTrack.get(entry.getKey()).addAll(entry.getValue());
        }
    }

    private void registerToggleFunctionOfTrackPart(AbstractTrackPart trackPart) {
        if (trackPart instanceof HasToggleFunction) {
            HasToggleFunction trackPartConfiguration = (HasToggleFunction) trackPart;
            final BusDataConfiguration toggleFunction = trackPartConfiguration.getToggleFunction();
            if (toggleFunction != null && toggleFunction.isValid()) {
                addBusListener(toggleFunction, new BusAddressBitListener(toggleFunction.getBit()) {
                    @Override
                    public void bitChanged(boolean oldValue, boolean newValue) {
                        eventBroadcaster.fireEvent(new TrackPartStateEvent(toggleFunction, newValue));
                    }
                });
                registerEventConfigurationOfTrackPart(trackPartConfiguration);
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
                                switchToggleFunction(toggleFunctionEntity, true);
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
    }

    private void switchToggleFunction(HasToggleFunction toggleFunctionEntity, boolean stateOn)
            throws DeviceAccessException {
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

    /**
     * TODO refactor to listener
     * 
     * @param construction {@link Construction}
     */
    public void constructionChanged(Construction construction) {
        currentConstruction = construction;
        loadData();
    }

}
