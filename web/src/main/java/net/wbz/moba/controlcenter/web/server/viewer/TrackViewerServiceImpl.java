package net.wbz.moba.controlcenter.web.server.viewer;

import com.google.gwt.user.client.rpc.RpcTokenException;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.wbz.moba.controlcenter.web.server.EventBroadcaster;
import net.wbz.moba.controlcenter.web.shared.track.model.Configuration;
import net.wbz.moba.controlcenter.web.shared.viewer.TrackPartStateEvent;
import net.wbz.moba.controlcenter.web.shared.viewer.TrackViewerService;
import net.wbz.selectrix4java.api.bus.BusAddress;
import net.wbz.selectrix4java.api.device.DeviceAccessException;
import net.wbz.selectrix4java.manager.DeviceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
                        busAddress.setBit(configuration.getOutput());
                    } else {
                        busAddress.clearBit(configuration.getOutput());
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
                            getBitState(configuration.getOutput());
                }
            } catch (DeviceAccessException e) {
                String msg = "can't load state of track part";
                log.error(msg, e);
                throw new RpcTokenException(msg);
            }
        }
        throw new RpcTokenException("invalid configuration: " + configuration);
    }

}
