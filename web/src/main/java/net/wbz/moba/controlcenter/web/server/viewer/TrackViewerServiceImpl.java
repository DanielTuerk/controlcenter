package net.wbz.moba.controlcenter.web.server.viewer;

import com.google.common.collect.Lists;
import com.google.gwt.user.client.rpc.RpcTokenException;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.wbz.moba.controlcenter.web.shared.bus.BusAddressBit;
import net.wbz.moba.controlcenter.web.shared.track.model.Configuration;
import net.wbz.moba.controlcenter.web.shared.viewer.TrackViewerService;
import net.wbz.selectrix4java.bus.BusAddress;
import net.wbz.selectrix4java.device.Device;
import net.wbz.selectrix4java.device.DeviceAccessException;
import net.wbz.selectrix4java.device.DeviceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
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
    public void toggleTrackPart(Configuration configuration, boolean state) {
        if (configuration.isValid()) {
            try {
                if (deviceManager.getConnectedDevice() != null) {
                    BusAddress busAddress = deviceManager.getConnectedDevice().getBusAddress(1, (byte) configuration.getAddress());
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
    public boolean getTrackPartState(Configuration configuration) {
        if (configuration.isValid()) {
            try {
                if (deviceManager.getConnectedDevice() != null) {
                    return deviceManager.getConnectedDevice().getBusAddress(1, (byte) configuration.getAddress()).
                            getBitState(configuration.getBit());
                }
            } catch (DeviceAccessException e) {
                String msg = "can't load state of track part";
                log.error(msg, e);
                throw new RpcTokenException(msg);
            }
        }
        throw new RpcTokenException("invalid configuration: " + configuration);
    }

    @Override
    public void sendTrackPartStates(List<BusAddressBit> busAddressBits) {
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

}
