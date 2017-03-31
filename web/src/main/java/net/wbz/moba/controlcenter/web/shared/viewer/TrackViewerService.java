package net.wbz.moba.controlcenter.web.shared.viewer;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import java.util.Map;
import net.wbz.moba.controlcenter.web.guice.MyGuiceServletConfig;
import net.wbz.moba.controlcenter.web.shared.track.model.BusDataConfiguration;
import net.wbz.moba.controlcenter.web.shared.track.model.Signal;

/**
 * @author Daniel Tuerk
 */
@RemoteServiceRelativePath(MyGuiceServletConfig.SERVICE_TRACK)
public interface TrackViewerService extends RemoteService {

    void toggleTrackPart(BusDataConfiguration configuration, boolean state);

    /**
     * @deprecated should be dropped
     * @param configuration
     * @return
     */
    boolean getTrackPartState(BusDataConfiguration configuration);

    /**
     * Switch the signal to the new {@link Signal.FUNCTION}.
     *
     * @param signal {@link Signal}
     * @param signalFunction {@link Signal.FUNCTION}
     */
    void switchSignal(Signal signal, Signal.FUNCTION signalFunction);

    void toggleTrackParts(Map<BusDataConfiguration, Boolean> trackPartStates);
}
