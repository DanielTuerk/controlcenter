package net.wbz.moba.controlcenter.web.server.web.viewer;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gwt.user.client.rpc.RpcTokenException;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.wbz.moba.controlcenter.web.shared.bus.BusAddressBit;
import net.wbz.moba.controlcenter.web.shared.track.model.Signal;
import net.wbz.moba.controlcenter.web.shared.track.model.TrackPartConfiguration;
import net.wbz.moba.controlcenter.web.shared.viewer.TrackViewerService;
import net.wbz.selectrix4java.bus.BusAddress;
import net.wbz.selectrix4java.device.Device;
import net.wbz.selectrix4java.device.DeviceAccessException;
import net.wbz.selectrix4java.device.DeviceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * @author Daniel Tuerk
 */
@Singleton
public class TrackViewerServiceImpl extends RemoteServiceServlet implements TrackViewerService {
    private static final Logger log = LoggerFactory.getLogger(TrackViewerServiceImpl.class);

    private final DeviceManager deviceManager;

    @Inject
    public TrackViewerServiceImpl(DeviceManager deviceManager) {
        this.deviceManager = deviceManager;
    }

    @Override
    public void toggleTrackPart(TrackPartConfiguration configuration, boolean state) {
        if (configuration.isValid()) {
            try {
                if (deviceManager.getConnectedDevice() != null) {
                    BusAddress busAddress = deviceManager.getConnectedDevice()
                            .getBusAddress(1, (byte) configuration.getAddress());
                    if (state) {
                        busAddress.setBit(configuration.getBit());
                    } else {
                        busAddress.clearBit(configuration.getBit());
                    }
                    busAddress.send();
                }
            } catch (DeviceAccessException e) {
                log.error("can't toggle track part", e);
                throw new RpcTokenException("can't toggle track part");
            }
        }
    }

    @Override
    public boolean getTrackPartState(TrackPartConfiguration configuration) {
        if (configuration.isValid()) {
            try {
                if (deviceManager.getConnectedDevice() != null) {
                    return deviceManager.getConnectedDevice().getBusAddress(1, (byte) configuration.getAddress())
                            .getBitState(configuration.getBit());
                }
            } catch (DeviceAccessException e) {
                String msg = "can't load state of track part";
                log.error(msg, e);
                throw new RpcTokenException(msg);
            }
        }
        throw new RpcTokenException("invalid configuration: " + configuration);
    }

    private void sendTrackPartStates(List<BusAddressBit> busAddressBits) {
        try {
            Device connectedDevice = deviceManager.getConnectedDevice();

            // collect addresses to change to avoid multiple send calls for single address
            List<BusAddress> busAddressesToUpdateData = Lists.newArrayList();
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
            log.error(msg, e);
            throw new RpcTokenException(msg);
        }
    }

    private BusAddressBit convertFunctionConfig(TrackPartConfiguration configuration) {
        if (configuration != null && configuration.isValid()) {
            return new BusAddressBit(configuration.getBus(), configuration.getAddress(),
                    configuration.getBit(), configuration.isBitState());
        }
        return null;
    }

    @Override
    public void switchSignal(Signal.TYPE signalType, Signal.FUNCTION signalFunction,
                             Map<Signal.LIGHT, TrackPartConfiguration> signalConfiguration) {
        // TODO
        Map<Signal.LIGHT, BusAddressBit> availableLightConfig = Maps.newHashMap();

        for (Signal.LIGHT light : signalType.getLights()) {
            TrackPartConfiguration lightFunction = signalConfiguration.get(light);
            if (lightFunction != null && lightFunction.isValid()) {
                availableLightConfig.put(light, new BusAddressBit(lightFunction.getBus(), lightFunction.getAddress(),
                        lightFunction.getBit(), !lightFunction.isBitState()));
            }
        }

        switch (signalFunction) {
            case HP0:
                switch (signalType) {
                    case BLOCK:
                        availableLightConfig.put(Signal.LIGHT.RED1, convertFunctionConfig(signalConfiguration.get(
                                Signal.LIGHT.RED1)));
                        break;
                    case BEFORE:
                        availableLightConfig.put(Signal.LIGHT.YELLOW1, convertFunctionConfig(signalConfiguration.get(
                                Signal.LIGHT.YELLOW1)));
                        availableLightConfig.put(Signal.LIGHT.YELLOW2, convertFunctionConfig(signalConfiguration.get(
                                Signal.LIGHT.YELLOW2)));
                        break;
                    case EXIT:
                        availableLightConfig.put(Signal.LIGHT.RED1, convertFunctionConfig(signalConfiguration.get(
                                Signal.LIGHT.RED1)));
                        availableLightConfig.put(Signal.LIGHT.RED2, convertFunctionConfig(signalConfiguration.get(
                                Signal.LIGHT.RED2)));
                        break;
                    case ENTER:
                        availableLightConfig.put(Signal.LIGHT.RED1, convertFunctionConfig(signalConfiguration.get(
                                Signal.LIGHT.RED1)));
                        break;
                }
                break;
            case HP1:
                switch (signalType) {
                    case BLOCK:
                        availableLightConfig.put(Signal.LIGHT.GREEN1, convertFunctionConfig(signalConfiguration.get(
                                Signal.LIGHT.GREEN1)));
                        break;
                    case BEFORE:
                        availableLightConfig.put(Signal.LIGHT.GREEN1, convertFunctionConfig(signalConfiguration.get(
                                Signal.LIGHT.GREEN1)));
                        availableLightConfig.put(Signal.LIGHT.GREEN2, convertFunctionConfig(signalConfiguration.get(
                                Signal.LIGHT.GREEN2)));
                        break;
                    case EXIT:
                        availableLightConfig.put(Signal.LIGHT.GREEN1, convertFunctionConfig(signalConfiguration.get(
                                Signal.LIGHT.GREEN1)));
                        break;
                    case ENTER:
                        availableLightConfig.put(Signal.LIGHT.GREEN1, convertFunctionConfig(signalConfiguration.get(
                                Signal.LIGHT.GREEN1)));
                        break;
                }
                break;
            case HP2:
                switch (signalType) {
                    case BEFORE:
                        availableLightConfig.put(Signal.LIGHT.GREEN1, convertFunctionConfig(signalConfiguration.get(
                                Signal.LIGHT.GREEN1)));
                        availableLightConfig.put(Signal.LIGHT.YELLOW2, convertFunctionConfig(signalConfiguration.get(
                                Signal.LIGHT.YELLOW2)));
                        break;
                    case EXIT:
                        availableLightConfig.put(Signal.LIGHT.GREEN1, convertFunctionConfig(signalConfiguration.get(
                                Signal.LIGHT.GREEN1)));
                        availableLightConfig.put(Signal.LIGHT.YELLOW1, convertFunctionConfig(signalConfiguration.get(
                                Signal.LIGHT.YELLOW1)));
                        break;
                    case ENTER:
                        availableLightConfig.put(Signal.LIGHT.GREEN1, convertFunctionConfig(signalConfiguration.get(
                                Signal.LIGHT.GREEN1)));
                        availableLightConfig.put(Signal.LIGHT.YELLOW1, convertFunctionConfig(signalConfiguration.get(
                                Signal.LIGHT.YELLOW1)));
                        break;
                }
                break;
            case HP0_SH1:
                switch (signalType) {
                    case EXIT:
                        availableLightConfig.put(Signal.LIGHT.RED1, convertFunctionConfig(signalConfiguration.get(
                                Signal.LIGHT.RED1)));
                        availableLightConfig.put(Signal.LIGHT.WHITE, convertFunctionConfig(signalConfiguration.get(
                                Signal.LIGHT.WHITE)));
                        break;
                }
                break;
        }

        sendTrackPartStates(Lists.newArrayList(availableLightConfig.values()));
    }

}
