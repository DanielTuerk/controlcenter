package net.wbz.moba.controlcenter.service.track;

import io.quarkus.runtime.Startup;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.wbz.moba.controlcenter.BusAddressIdentifier;
import net.wbz.moba.controlcenter.EventBroadcaster;
import net.wbz.moba.controlcenter.service.track.block.FeedbackTrackBlockRegistry;
import net.wbz.moba.controlcenter.shared.track.model.AbstractTrackPart;
import net.wbz.moba.controlcenter.shared.track.model.BusDataConfiguration;
import net.wbz.moba.controlcenter.shared.track.model.HasToggleFunction;
import net.wbz.moba.controlcenter.shared.track.model.Signal;
import net.wbz.moba.controlcenter.shared.viewer.TrackPartStateEvent;
import net.wbz.selectrix4java.bus.BusAddressBitListener;
import net.wbz.selectrix4java.bus.BusListener;
import net.wbz.selectrix4java.device.Device;
import net.wbz.selectrix4java.device.DeviceAccessException;
import net.wbz.selectrix4java.device.DeviceConnectionListener;
import net.wbz.selectrix4java.device.DeviceManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Daniel Tuerk
 */
@Slf4j
@Startup
@ApplicationScoped
public class TrackRegistration {

    /**
     * Map for all {@link BusListener}s of a single {@link BusAddressIdentifier} to avoid duplicated listeners to
     * register and to call from {@link net.wbz.selectrix4java.data.BusDataChannel}.
     */
    private final Map<BusAddressIdentifier, List<BusListener>> busAddressListenersOfTheCurrentTrack =
            new ConcurrentHashMap<>();

    private final DeviceManager deviceManager;
    private final TrackProvider trackProvider;
    private final EventBroadcaster eventBroadcaster;
    private final FeedbackTrackBlockRegistry feedbackTrackBlockRegistry;

    @Inject
    public TrackRegistration(DeviceManager deviceManager, TrackProvider trackProvider,
                             EventBroadcaster eventBroadcaster, FeedbackTrackBlockRegistry feedbackTrackBlockRegistry) {
        this.deviceManager = deviceManager;
        this.trackProvider = trackProvider;
        this.eventBroadcaster = eventBroadcaster;
        this.feedbackTrackBlockRegistry = feedbackTrackBlockRegistry;
        deviceManager.addDeviceConnectionListener(new DeviceConnectionListener() {
            @Override
            public void connected(Device device) {
                registerTrack(device);
            }

            @Override
            public void disconnected(Device device) {
                unregisterTrack(device);
            }
        });
    }

    void reregisterTrack() {
        deviceManager.getConnectedDevice().ifPresent(device -> {
            unregisterTrack(device);
            registerTrack(device);
        });
    }

    private void registerTrack(Device device) {
        log.debug("register track to: " + device.getDeviceId());
        var track = trackProvider.getTrack();
        track.stream()
            .filter(t -> t instanceof HasToggleFunction)
            .forEach(this::registerToggleFunctionOfTrackPart);

        track.stream()
            .filter(t -> t instanceof Signal)
            .map(t -> (Signal) t)
            .forEach(this::registerSignalFunction);

        registerBusAddressListeners(device);
    }

    private void unregisterTrack(Device device) {
        log.debug("unregister track from: " + device.getDeviceId());
        try {
            feedbackTrackBlockRegistry.removeListeners(device);
        } catch (DeviceAccessException e) {
            log.error("Failed to remove track block listeners for device " + device, e);
        }
        removeBusAddressListeners(device);
        busAddressListenersOfTheCurrentTrack.clear();
    }

    private void registerBusAddressListeners(Device device) {
        try {
            for (Map.Entry<BusAddressIdentifier, List<BusListener>> entry : busAddressListenersOfTheCurrentTrack
                .entrySet()) {
                device.getBusAddress(entry.getKey().bus(), (byte) entry.getKey().address())
                    .addListeners(entry.getValue());
            }
        } catch (DeviceAccessException e) {
            log.error("can't register listeners to active device", e);
        }

        try {
            feedbackTrackBlockRegistry.registerListeners(device);
        } catch (DeviceAccessException e) {
            log.error("can't register track block listeners to active device", e);
        }

    }

    private void removeBusAddressListeners(Device device) {
        try {
            for (Map.Entry<BusAddressIdentifier, List<BusListener>> entry : busAddressListenersOfTheCurrentTrack
                .entrySet()) {
                device.getBusAddress(entry.getKey().bus(), (byte) entry.getKey().address())
                    .removeListeners(entry.getValue());
            }
        } catch (DeviceAccessException e) {
            log.error("can't remove listeners to active device", e);
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
