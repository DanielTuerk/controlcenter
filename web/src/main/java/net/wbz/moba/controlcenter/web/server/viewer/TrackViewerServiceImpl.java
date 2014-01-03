package net.wbz.moba.controlcenter.web.server.viewer;

import com.google.gwt.user.client.rpc.RpcTokenException;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.wbz.moba.controlcenter.communication.api.Device;
import net.wbz.moba.controlcenter.communication.api.DeviceAccessException;
import net.wbz.moba.controlcenter.communication.manager.DeviceManager;
import net.wbz.moba.controlcenter.web.shared.track.model.Configuration;
import net.wbz.moba.controlcenter.web.shared.viewer.TrackViewerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Daniel Tuerk (daniel.tuerk@jambit.com)
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
    public void toogleTrackPart(Configuration configuration, boolean state) {
        if (configuration.isValid()) {
            try {
                deviceManager.getConnectedDevice().getOutputModule((byte) configuration.getAddress())
                        .setBit(getBitOfConfiguration(configuration), state);
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
                return deviceManager.getConnectedDevice().getOutputModule((byte) configuration.getAddress())
                        .getBitState(getBitOfConfiguration(configuration));
            } catch (DeviceAccessException e) {
                String msg = "can't load state of track part";
                log.error(msg, e);
                throw new RpcTokenException(msg);
            }
        }
        throw new RpcTokenException("invalid configuration: " + configuration);
    }

    private Device.BIT getBitOfConfiguration(Configuration configuration) {
        switch (configuration.getOutput()) {
            case 1:
                return Device.BIT.BIT_1;
            case 2:
                return Device.BIT.BIT_2;
            case 3:
                return Device.BIT.BIT_3;
            case 4:
                return Device.BIT.BIT_4;
            case 5:
                return Device.BIT.BIT_5;
            case 6:
                return Device.BIT.BIT_6;
            case 7:
                return Device.BIT.BIT_7;
            case 8:
                return Device.BIT.BIT_8;
            default:
                throw new RuntimeException("invalid output bit " + configuration.getOutput());
        }
    }
}
