package net.wbz.moba.controlcenter.web.shared.viewer;

import java.util.List;
import java.util.Map;

import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.RequestContext;
import com.google.web.bindery.requestfactory.shared.Service;
import net.wbz.moba.controlcenter.web.guice.requestFactory.InjectingServiceLocator;
import net.wbz.moba.controlcenter.web.server.viewer.TrackViewerService;
import net.wbz.moba.controlcenter.web.shared.bus.BusAddressBit;
import net.wbz.moba.controlcenter.web.shared.track.model.Configuration;
import net.wbz.moba.controlcenter.web.shared.track.model.ConfigurationProxy;
import net.wbz.moba.controlcenter.web.shared.track.model.Signal;

/**
 * @author Daniel Tuerk
 */
@Service(value = TrackViewerService.class, locator = InjectingServiceLocator.class)
public interface TrackViewerRequest extends RequestContext {

    Request<Void> toggleTrackPart(ConfigurationProxy configuration, boolean state);

    Request<Boolean> getTrackPartState(ConfigurationProxy configuration);

//    Request<Void> sendTrackPartStates(List<BusAddressBit> busAddressBits);

    /**
     * Switch the signal of the given {@link net.wbz.moba.controlcenter.web.shared.track.model.SignalConfiguration} to
     * the new {@link net.wbz.moba.controlcenter.web.shared.track.model.Signal.FUNCTION}.
     *
     * @param signalType {@link net.wbz.moba.controlcenter.web.shared.track.model.Signal.TYPE}
     * @param signalFunction {@link net.wbz.moba.controlcenter.web.shared.track.model.Signal.FUNCTION}
     * @param signalConfiguration {@link net.wbz.moba.controlcenter.web.shared.track.model.SignalConfiguration}
     */
    Request<Void> switchSignal(Signal.TYPE signalType, Signal.FUNCTION signalFunction, Map<Signal.LIGHT, ConfigurationProxy> signalConfiguration);
}
