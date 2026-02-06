package net.wbz.moba.controlcenter.service.track;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.wbz.moba.controlcenter.BusAddressIdentifier;
import net.wbz.moba.controlcenter.EventBroadcaster;
import net.wbz.moba.controlcenter.shared.bus.BusAddressBit;
import net.wbz.moba.controlcenter.shared.track.model.AbstractTrackPart;
import net.wbz.moba.controlcenter.shared.track.model.BusDataConfiguration;
import net.wbz.moba.controlcenter.shared.track.model.HasToggleFunction;
import net.wbz.moba.controlcenter.shared.track.model.Signal;
import net.wbz.moba.controlcenter.shared.viewer.TrackPartStateEvent;
import net.wbz.selectrix4java.bus.BusAddress;
import net.wbz.selectrix4java.bus.BusAddressBitListener;
import net.wbz.selectrix4java.bus.BusListener;
import net.wbz.selectrix4java.device.Device;
import net.wbz.selectrix4java.device.DeviceAccessException;
import net.wbz.selectrix4java.device.DeviceConnectionListener;
import net.wbz.selectrix4java.device.DeviceManager;
import org.jboss.logging.Logger;

/**
 *
 * @author Daniel Tuerk
 */
@ApplicationScoped
public class TrackViewerService {

    private static final Logger LOG = Logger.getLogger(TrackViewerService.class);
    /**
     * Map for all {@link BusListener}s of a single {@link BusAddressIdentifier} to avoid duplicated listeners to
     * register and to call from {@link net.wbz.selectrix4java.data.BusDataChannel}.
     */
    private final Map<BusAddressIdentifier, List<BusListener>> busAddressListenersOfTheCurrentTrack = Maps.newConcurrentMap();

    private final DeviceManager deviceManager;
    private final EventBroadcaster eventBroadcaster;

    private final Logger logger;

    @Inject
    public TrackViewerService(DeviceManager deviceManager, TrackProvider trackProvider,
        EventBroadcaster eventBroadcaster, Logger logger) {
        this.deviceManager = deviceManager;
        this.eventBroadcaster = eventBroadcaster;
        this.logger = logger;

        trackProvider.getTrack().stream()
            .filter(t -> t instanceof HasToggleFunction)
            .forEach(this::registerToggleFunctionOfTrackPart);

        deviceManager.addDeviceConnectionListener(new DeviceConnectionListener() {
            @Override
            public void connected(Device device) {

                registerBusAddressListeners(device);
            }

            @Override
            public void disconnected(Device device) {
                removeBusAddressListeners(device);
            }
        });
    }

    private void registerToggleFunctionOfTrackPart(AbstractTrackPart trackPart) {
        if (trackPart instanceof HasToggleFunction trackPartConfiguration) {
            final var toggleFunction = trackPartConfiguration.getToggleFunction();
            if (toggleFunction != null && toggleFunction.isValid()) {
                addBusListener(toggleFunction, new BusAddressBitListener(toggleFunction.getBit()) {
                    @Override
                    public void bitChanged(boolean oldValue, boolean newValue) {
                        eventBroadcaster.fireEvent(
                            new TrackPartStateEvent(trackPart.getId(), toggleFunction, newValue));
                    }
                });
//                registerEventConfigurationOfTrackPart(trackPartConfiguration);
            }
        }
    }


    private void addBusListener(BusDataConfiguration trackPartConfiguration, BusListener listener) {
        BusAddressIdentifier busAddressIdentifier = new BusAddressIdentifier(trackPartConfiguration.getBus(),
            trackPartConfiguration.getAddress());
        if (!busAddressListenersOfTheCurrentTrack.containsKey(busAddressIdentifier)) {
            busAddressListenersOfTheCurrentTrack.put(busAddressIdentifier, new ArrayList<>());
        }
        busAddressListenersOfTheCurrentTrack.get(busAddressIdentifier).add(listener);
    }

    private void removeBusAddressListeners(Device device) {
        try {
            for (Map.Entry<BusAddressIdentifier, List<BusListener>> entry : busAddressListenersOfTheCurrentTrack
                .entrySet()) {
                device.getBusAddress(entry.getKey().getBus(), (byte) entry.getKey().getAddress())
                    .removeListeners(entry.getValue());
            }
        } catch (DeviceAccessException e) {
            logger.error("can't register listeners to active device", e);
        }
    }

    private void registerBusAddressListeners(Device device) {
        try {
            for (Map.Entry<BusAddressIdentifier, List<BusListener>> entry : busAddressListenersOfTheCurrentTrack
                .entrySet()) {
                device.getBusAddress(entry.getKey().getBus(), (byte) entry.getKey().getAddress())
                    .addListeners(entry.getValue());
            }
        } catch (DeviceAccessException e) {
            logger.error("can't register listeners to active device", e);
        }

        // TODO
//        try {
//            trackBlockRegistry.registerListeners(device);
//        } catch (DeviceAccessException e) {
//            log.error("can't register track block listeners to active device", e);
//        }
//
//        try {
//            signalBlockRegistry.registerListeners(device);
//        } catch (DeviceAccessException e) {
//            log.error("can't register signal block listeners to active device", e);
//        }

    }

    public void toggleTrackPart(AbstractTrackPart trackPart) throws DeviceAccessException {
        if (trackPart instanceof HasToggleFunction) {
            var trackPartConfig = ((HasToggleFunction) trackPart).getToggleFunction();
            if (trackPartConfig != null && trackPartConfig.isValid()) {
                final var busAddress = deviceManager.getConnectedDevice()
                    .getBusAddress(trackPartConfig.getBus(), trackPartConfig.getAddress().byteValue());
                if (busAddress.getBitState(trackPartConfig.getBit())) {
                    busAddress.clearBit(trackPartConfig.getBit());
                    } else {
                    busAddress.setBit(trackPartConfig.getBit());
                    }
                    busAddress.send();
                }
            }
    }

    public void toggleTrackParts(Map<BusDataConfiguration, Boolean> trackPartStates) {

        List<BusAddressBit> busAddressBits = new ArrayList<>();

        for (Entry<BusDataConfiguration, Boolean> entry : trackPartStates.entrySet()) {
            BusDataConfiguration busDataConfiguration = entry.getKey();
            Boolean configState = entry.getValue();
            if (busDataConfiguration != null && busDataConfiguration.isValid()) {
                busAddressBits.add(new BusAddressBit(busDataConfiguration.getBus(), busDataConfiguration.getAddress(),
                    busDataConfiguration.getBit(), configState));
            }
        }
        sendTrackPartStates(busAddressBits);
    }

    @Deprecated
    public boolean getTrackPartState(BusDataConfiguration configuration) {
        // TODO method can be deleted?
        if (configuration.isValid()) {
            try {
                if (deviceManager.getConnectedDevice() != null) {
                    return deviceManager.getConnectedDevice().getBusAddress(1, configuration.getAddress().byteValue())
                        .getBitState(configuration.getBit());
                }
            } catch (DeviceAccessException e) {
                String msg = "can't load state of track part";
                LOG.error(msg, e);
                throw new RuntimeException(msg);
            }
        }
        throw new RuntimeException("invalid configuration: " + configuration);
    }

    private synchronized void sendTrackPartStates(List<BusAddressBit> busAddressBits) {
        try {
            Device connectedDevice = deviceManager.getConnectedDevice();

            // collect addresses to change to avoid multiple send calls for single address
            List<BusAddress> busAddressesToUpdateData = new ArrayList<>();
            for (BusAddressBit config : busAddressBits) {
                if (config != null) {
                    BusAddress busAddress = connectedDevice.getBusAddress(config.getBus(), (byte) config.getAddress());

                    if (!busAddressesToUpdateData.contains(busAddress)) {
                        busAddressesToUpdateData.add(busAddress);
                    }

                    if (config.isBitState()) {
                        busAddress.setBit(config.getBit());
                    } else {
                        busAddress.clearBit(config.getBit());
                    }
                }
            }

            // send data of each address
            for (BusAddress busAddress : busAddressesToUpdateData) {
                busAddress.send();
            }

        } catch (DeviceAccessException e) {
            String msg = "can't change data of addresses";
            LOG.error(msg, e);
            throw new RuntimeException(msg);
        }
    }

    private BusAddressBit convertFunctionConfig(BusDataConfiguration configuration) {
        if (configuration != null && configuration.isValid()) {
            return new BusAddressBit(configuration.getBus(), configuration.getAddress(), configuration.getBit(),
                configuration.getBitState());
        }
        return null;
    }

    /**
     * TODO refactor to signal function handling to provide different light schemata.
     *
     * @param signal {@link Signal}
     * @param signalFunction {@link Signal.FUNCTION}
     */
    public void switchSignal(Signal signal, Signal.FUNCTION signalFunction) {
        LOG.debugf("switch signal %s to %s", signal, signalFunction);
        Map<Signal.LIGHT, BusAddressBit> availableLightConfig = Maps.newHashMap();

        Signal.TYPE signalType = signal.getType();
        // set all lights to 'off'
        for (Signal.LIGHT light : signalType.getLights()) {
            BusDataConfiguration lightFunction = signal.getSignalConfiguration(light);
            if (lightFunction != null && lightFunction.isValid()) {
                availableLightConfig.put(light,
                    new BusAddressBit(lightFunction.getBus(), lightFunction.getAddress(), lightFunction.getBit(),
                        !lightFunction.getBitState()));
            }
        }
        // set lights to 'on' for the signal function
        switch (signalFunction) {
            case HP0:
                switch (signalType) {
                    case BLOCK:
                        availableLightConfig.put(Signal.LIGHT.RED1,
                            convertFunctionConfig(signal.getSignalConfiguration(Signal.LIGHT.RED1)));
                        break;
                    case BEFORE:
                        availableLightConfig.put(Signal.LIGHT.YELLOW1,
                            convertFunctionConfig(signal.getSignalConfiguration(Signal.LIGHT.YELLOW1)));
                        availableLightConfig.put(Signal.LIGHT.YELLOW2,
                            convertFunctionConfig(signal.getSignalConfiguration(Signal.LIGHT.YELLOW2)));
                        break;
                    case EXIT:
                        availableLightConfig.put(Signal.LIGHT.RED1,
                            convertFunctionConfig(signal.getSignalConfiguration(Signal.LIGHT.RED1)));
                        availableLightConfig.put(Signal.LIGHT.RED2,
                            convertFunctionConfig(signal.getSignalConfiguration(Signal.LIGHT.RED2)));
                        break;
                    case ENTER:
                        availableLightConfig.put(Signal.LIGHT.RED1,
                            convertFunctionConfig(signal.getSignalConfiguration(Signal.LIGHT.RED1)));
                        break;
                }
                break;
            case HP1:
                switch (signalType) {
                    case BLOCK:
                        availableLightConfig.put(Signal.LIGHT.GREEN1,
                            convertFunctionConfig(signal.getSignalConfiguration(Signal.LIGHT.GREEN1)));
                        break;
                    case BEFORE:
                        availableLightConfig.put(Signal.LIGHT.GREEN1,
                            convertFunctionConfig(signal.getSignalConfiguration(Signal.LIGHT.GREEN1)));
                        availableLightConfig.put(Signal.LIGHT.GREEN2,
                            convertFunctionConfig(signal.getSignalConfiguration(Signal.LIGHT.GREEN2)));
                        break;
                    case EXIT:
                        availableLightConfig.put(Signal.LIGHT.GREEN1,
                            convertFunctionConfig(signal.getSignalConfiguration(Signal.LIGHT.GREEN1)));
                        break;
                    case ENTER:
                        availableLightConfig.put(Signal.LIGHT.GREEN1,
                            convertFunctionConfig(signal.getSignalConfiguration(Signal.LIGHT.GREEN1)));
                        break;
                }
                break;
            case HP2:
                switch (signalType) {
                    case BEFORE:
                        availableLightConfig.put(Signal.LIGHT.GREEN1,
                            convertFunctionConfig(signal.getSignalConfiguration(Signal.LIGHT.GREEN1)));
                        availableLightConfig.put(Signal.LIGHT.YELLOW2,
                            convertFunctionConfig(signal.getSignalConfiguration(Signal.LIGHT.YELLOW2)));
                        break;
                    case EXIT:
                        availableLightConfig.put(Signal.LIGHT.GREEN1,
                            convertFunctionConfig(signal.getSignalConfiguration(Signal.LIGHT.GREEN1)));
                        availableLightConfig.put(Signal.LIGHT.YELLOW1,
                            convertFunctionConfig(signal.getSignalConfiguration(Signal.LIGHT.YELLOW1)));
                        break;
                    case ENTER:
                        availableLightConfig.put(Signal.LIGHT.GREEN1,
                            convertFunctionConfig(signal.getSignalConfiguration(Signal.LIGHT.GREEN1)));
                        availableLightConfig.put(Signal.LIGHT.YELLOW1,
                            convertFunctionConfig(signal.getSignalConfiguration(Signal.LIGHT.YELLOW1)));
                        break;
                }
                break;
            case HP0_SH1:
                switch (signalType) {
                    case EXIT:
                        availableLightConfig.put(Signal.LIGHT.RED1,
                            convertFunctionConfig(signal.getSignalConfiguration(Signal.LIGHT.RED1)));
                        availableLightConfig.put(Signal.LIGHT.WHITE,
                            convertFunctionConfig(signal.getSignalConfiguration(Signal.LIGHT.WHITE)));
                        break;
                }
                break;
        }
        sendTrackPartStates(Lists.newArrayList(availableLightConfig.values()));
    }


//    private void registerEventConfigurationOfTrackPart(final HasToggleFunction toggleFunctionEntity) {
//        // TODO
//        EventConfiguration eventConfiguration = toggleFunctionEntity.getEventConfiguration();
//        if (eventConfiguration != null && eventConfiguration.isActive()) {
//
//            // add state 'ON'
//            final BusDataConfiguration stateOnConfig = eventConfiguration.getStateOnConfig();
//            if (stateOnConfig.isValid()) {
//                addBusListener(stateOnConfig, new BusAddressBitListener(stateOnConfig.getBit()) {
//                    @Override
//                    public void bitChanged(boolean oldValue, boolean newValue) {
//                        if ((newValue && stateOnConfig.getBitState()) || (!newValue && !stateOnConfig.getBitState())) {
//                            try {
//                                switchToggleFunction(toggleFunctionEntity, true);
//                            } catch (DeviceAccessException e) {
//                                logger.error("can't toggle the 'on' state", e);
//                            }
//                        }
//                    }
//                });
//            }
//
//            // add state 'OFF'
//            final BusDataConfiguration stateOffConfig = eventConfiguration.getStateOffConfig();
//            if (stateOffConfig.isValid()) {
//                addBusListener(stateOffConfig, new BusAddressBitListener(stateOffConfig.getBit()) {
//                    @Override
//                    public void bitChanged(boolean oldValue, boolean newValue) {
//                        if ((newValue && stateOffConfig.getBitState()) || (!newValue && !stateOffConfig
//                            .getBitState())) {
//                            try {
//                                switchToggleFunction(toggleFunctionEntity, false);
//                            } catch (DeviceAccessException e) {
//                                logger.error("can't toggle the 'off' state", e);
//                            }
//                        }
//                    }
//                });
//            }
//        }
//    }
}
