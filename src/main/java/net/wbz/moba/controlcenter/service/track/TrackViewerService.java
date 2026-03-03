package net.wbz.moba.controlcenter.service.track;

import com.google.common.collect.Maps;
import io.quarkus.runtime.Startup;
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
 * TODO split class to multiples, e.g. trackpart vs signal or listener registration vs actions
 * @author Daniel Tuerk
 */
@Startup
@ApplicationScoped
public class TrackViewerService {

    /**
     * Map for all {@link BusListener}s of a single {@link BusAddressIdentifier} to avoid duplicated listeners to
     * register and to call from {@link net.wbz.selectrix4java.data.BusDataChannel}.
     */
    private final Map<BusAddressIdentifier, List<BusListener>> busAddressListenersOfTheCurrentTrack = Maps.newConcurrentMap();

    private final DeviceManager deviceManager;
    private final EventBroadcaster eventBroadcaster;
    private final SignalStateService signalStateService;

    private final Logger logger;

    @Inject
    public TrackViewerService(DeviceManager deviceManager, TrackProvider trackProvider,
        EventBroadcaster eventBroadcaster, TrackBlockRegistry trackBlockRegistry,
        SignalStateService signalStateService, Logger logger) {
        this.deviceManager = deviceManager;
        this.eventBroadcaster = eventBroadcaster;
        this.signalStateService = signalStateService;
        this.logger = logger;

        deviceManager.addDeviceConnectionListener(new DeviceConnectionListener() {
            @Override
            public void connected(Device device) {
                var track = trackProvider.getTrack();
                track.stream()
                    .filter(t -> t instanceof HasToggleFunction)
                    .forEach(t -> registerToggleFunctionOfTrackPart(t));

                track.stream()
                    .filter(t -> t instanceof Signal)
                    .map(t -> (Signal) t)
                    .forEach(t -> registerSignalFunction(t));

                registerBusAddressListeners(device);
            }

            @Override
            public void disconnected(Device device) {
                try {
                    trackBlockRegistry.removeListeners(device);
                } catch (DeviceAccessException e) {
                    logger.error("Failed to remove track block listeners for device " + device, e);
                }
                removeBusAddressListeners(device);
                busAddressListenersOfTheCurrentTrack.clear();
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

                try {
                    trackBlockRegistry.registerListeners(device);
                } catch (DeviceAccessException e) {
                    logger.error("can't register track block listeners to active device", e);
                }

            }

            private void removeBusAddressListeners(Device device) {
                try {
                    for (Map.Entry<BusAddressIdentifier, List<BusListener>> entry : busAddressListenersOfTheCurrentTrack
                        .entrySet()) {
                        device.getBusAddress(entry.getKey().getBus(), (byte) entry.getKey().getAddress())
                            .removeListeners(entry.getValue());
                    }
                } catch (DeviceAccessException e) {
                    logger.error("can't remove listeners to active device", e);
                }
            }
        });
    }

    protected void registerToggleFunctionOfTrackPart(AbstractTrackPart trackPart) {
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
        for (final Entry<BusDataConfiguration, Boolean> entry : trackPartStates.entrySet()) {
            final var busDataConfiguration = entry.getKey();
            if (busDataConfiguration != null && busDataConfiguration.isValid()) {
                busAddressBits.add(new BusAddressBit(busDataConfiguration.getBus(), busDataConfiguration.getAddress(),
                    busDataConfiguration.getBit(), entry.getValue()));
            }
        }
        sendTrackPartStates(busAddressBits);
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
            logger.errorf(msg, e);
            throw new RuntimeException(msg);
        }
    }

    public void switchSignal(Signal signal, Signal.FUNCTION signalFunction) {
        logger.debugf("switch signal %s to %s", signal, signalFunction);
        sendTrackPartStates(signalStateService.convertToLights(signal, signalFunction));
    }

    private void registerSignalFunction(Signal signal) {
        Map<BusAddressIdentifier, List<BusAddressBitListener>> busAddressListeners = new SignalFunctionReceiver(signal,
            eventBroadcaster).getBusAddressListeners();
        for (Map.Entry<BusAddressIdentifier, List<BusAddressBitListener>> entry : busAddressListeners.entrySet()) {
            if (!busAddressListenersOfTheCurrentTrack.containsKey(entry.getKey())) {
                busAddressListenersOfTheCurrentTrack.put(entry.getKey(), new ArrayList<>());
            }
            busAddressListenersOfTheCurrentTrack.get(entry.getKey()).addAll(entry.getValue());
        }
    }

}
