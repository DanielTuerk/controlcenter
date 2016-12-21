package net.wbz.moba.controlcenter.web.shared.viewer;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import net.wbz.moba.controlcenter.web.server.persist.construction.track.SignalEntity;
import net.wbz.moba.controlcenter.web.server.persist.construction.track.SignalConfiguration;
import net.wbz.moba.controlcenter.web.shared.track.model.ConfigurationProxy;

import java.util.Map;

/**
 * @author Daniel Tuerk
 */
@RemoteServiceRelativePath("trackviewer")
public interface TrackViewerService extends RemoteService {

    void toggleTrackPart(ConfigurationProxy configuration, boolean state);

    boolean getTrackPartState(ConfigurationProxy configuration);

//    void sendTrackPartStates(List<BusAddressBit> busAddressBits);

    /**
     * SwitchEntity the signal of the given {@link SignalConfiguration} to
     * the new {@link SignalEntity.FUNCTION}.
     *
     * @param signalType          {@link SignalEntity.TYPE}
     * @param signalFunction      {@link SignalEntity.FUNCTION}
     * @param signalConfiguration {@link SignalConfiguration}
     */
    void switchSignal(SignalEntity.TYPE signalType, SignalEntity.FUNCTION signalFunction, Map<SignalEntity.LIGHT, ConfigurationProxy> signalConfiguration);
}
