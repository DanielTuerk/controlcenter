package net.wbz.moba.controlcenter.web.shared.track.model;

import java.util.List;

import net.wbz.moba.controlcenter.web.guice.requestFactory.InjectingEntityLocator;
import net.wbz.moba.controlcenter.web.shared.EntityProxyWithIdAndVersion;

import com.google.web.bindery.requestfactory.shared.ProxyFor;

/**
 * @author Daniel Tuerk
 */
@ProxyFor(value = TrackPartFunctions.class, locator = InjectingEntityLocator.class)
public interface TrackPartFunctionsProxy extends EntityProxyWithIdAndVersion {

    List<TrackPartFunctionProxy> getFunctions();

    void setFunctions(List<TrackPartFunctionProxy> functions);

    TrackPartProxy getTrackPart();

    void setTrackPart(TrackPartProxy trackPart);
}
