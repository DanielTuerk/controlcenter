package net.wbz.moba.controlcenter.web.shared.viewer;

import net.wbz.moba.controlcenter.web.shared.bus.BusAddressBit;
import net.wbz.moba.controlcenter.web.shared.track.model.Configuration;
import net.wbz.moba.controlcenter.web.shared.track.model.Signal;

import javax.ws.rs.Path;
import java.util.List;
import java.util.Map;

/**
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
@Path("trackviewer")
public interface TrackViewerService {

    public void toggleTrackPart(Configuration configuration, boolean state);

    public boolean getTrackPartState(Configuration configuration);

    public void sendTrackPartStates(List<BusAddressBit> busAddressBits);

    /**
     * Switch the signal of the given {@link net.wbz.moba.controlcenter.web.shared.track.model.SignalConfiguration} to
     * the new {@link net.wbz.moba.controlcenter.web.shared.track.model.Signal.FUNCTION}.
     *
     * @param signalType          {@link net.wbz.moba.controlcenter.web.shared.track.model.Signal.TYPE}
     * @param signalFunction      {@link net.wbz.moba.controlcenter.web.shared.track.model.Signal.FUNCTION}
     * @param signalConfiguration {@link net.wbz.moba.controlcenter.web.shared.track.model.SignalConfiguration}
     */
    public void switchSignal(Signal.TYPE signalType, Signal.FUNCTION signalFunction,
                             Map<Signal.LIGHT, Configuration> signalConfiguration);
}
