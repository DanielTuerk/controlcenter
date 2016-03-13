package net.wbz.moba.controlcenter.web.shared.viewer;

import java.util.List;
import java.util.Map;

import net.wbz.moba.controlcenter.web.shared.bus.BusAddressBit;
import net.wbz.moba.controlcenter.web.shared.track.model.Configuration;
import net.wbz.moba.controlcenter.web.shared.track.model.Signal;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * @author Daniel Tuerk
 */
@RemoteServiceRelativePath("trackviewer")
public interface TrackViewerService extends RemoteService {

    public void toggleTrackPart(Configuration configuration, boolean state);

    public boolean getTrackPartState(Configuration configuration);

    public void sendTrackPartStates(List<BusAddressBit> busAddressBits);

    /**
     * Switch the signal of the given {@link net.wbz.moba.controlcenter.web.shared.track.model.SignalConfiguration} to
     * the new {@link net.wbz.moba.controlcenter.web.shared.track.model.Signal.FUNCTION}.
     *
     * @param signalType {@link net.wbz.moba.controlcenter.web.shared.track.model.Signal.TYPE}
     * @param signalFunction {@link net.wbz.moba.controlcenter.web.shared.track.model.Signal.FUNCTION}
     * @param signalConfiguration {@link net.wbz.moba.controlcenter.web.shared.track.model.SignalConfiguration}
     */
    public void switchSignal(Signal.TYPE signalType, Signal.FUNCTION signalFunction,
            Map<Signal.LIGHT, Configuration> signalConfiguration);
}
