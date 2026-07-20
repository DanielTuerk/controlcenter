package io.github.danieltuerk.controlcenter.service.track;

import io.github.danieltuerk.controlcenter.shared.bus.BusAddressBit;
import io.github.danieltuerk.controlcenter.shared.track.model.*;
import io.github.danieltuerk.selectrix4java.bus.BusAddress;
import io.github.danieltuerk.selectrix4java.device.Device;
import io.github.danieltuerk.selectrix4java.device.DeviceAccessException;
import io.github.danieltuerk.selectrix4java.device.DeviceManager;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.Map.Entry;

/**
 * @author Daniel Tuerk
 */
@Slf4j
@ApplicationScoped
public class TrackViewerService {

    private final DeviceManager deviceManager;

    private final SignalStateService signalStateService;
    private final TrackRegistration trackRegistration;

    @Inject
    public TrackViewerService(DeviceManager deviceManager, SignalStateService signalStateService,
                              TrackRegistration trackRegistration) {
        this.deviceManager = deviceManager;
        this.signalStateService = signalStateService;
        this.trackRegistration = trackRegistration;
    }

    public void onSaved(@Observes TrackChangedEvent evt) {
        log.info("TrackChangedEvent: {}", evt);
        trackRegistration.reregisterTrack();
    }

    public void toggleTrackPart(AbstractTrackPart trackPart) throws DeviceAccessException {
        if (trackPart instanceof HasToggleFunction) {
            var trackPartConfig = ((HasToggleFunction) trackPart).getToggleFunction();
            if (trackPartConfig != null && trackPartConfig.isValid()) {
                final var busAddress = deviceManager.getConnectedDevice()
                    .orElseThrow(() -> new DeviceAccessException("no connected device"))
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
        Set<BusAddressBit> busAddressBits = new HashSet<>();
        for (final Entry<BusDataConfiguration, Boolean> entry : trackPartStates.entrySet()) {
            final var busDataConfiguration = entry.getKey();
            if (busDataConfiguration != null && busDataConfiguration.isValid()) {
                busAddressBits.add(new BusAddressBit(busDataConfiguration.getBus(), busDataConfiguration.getAddress(),
                    busDataConfiguration.getBit(), entry.getValue()));
            }
        }
        sendTrackPartStates(busAddressBits);
    }

    private synchronized void sendTrackPartStates(Set<BusAddressBit> busAddressBits) {
        try {
            Device connectedDevice = deviceManager.getConnectedDevice()
                .orElseThrow(() -> new DeviceAccessException("no connected device"));

            // collect addresses to change to avoid multiple send calls for single address
            List<BusAddress> busAddressesToUpdateData = new ArrayList<>();
            for (BusAddressBit config : busAddressBits) {
                if (config != null) {
                    BusAddress busAddress = connectedDevice.getBusAddress(config.bus(), (byte) config.address());

                    if (!busAddressesToUpdateData.contains(busAddress)) {
                        busAddressesToUpdateData.add(busAddress);
                    }

                    if (config.bitState()) {
                        busAddress.setBit(config.bit());
                    } else {
                        busAddress.clearBit(config.bit());
                    }
                }
            }

            // send data of each address
            for (BusAddress busAddress : busAddressesToUpdateData) {
                busAddress.send();
            }

        } catch (DeviceAccessException e) {
            String msg = "can't change data of addresses";
            log.error(msg, e);
            throw new RuntimeException(msg);
        }
    }

    public void switchSignal(Signal signal, Signal.FUNCTION signalFunction) {
        log.debug("switch signal {} to {}", signal, signalFunction);
        sendTrackPartStates(signalStateService.convertToLights(signal, signalFunction));
    }

}
