package net.wbz.moba.controlcenter.web.shared.track.model;

import net.wbz.moba.controlcenter.web.guice.requestFactory.InjectingEntityLocator;
import net.wbz.moba.controlcenter.web.shared.EntityProxyWithIdAndVersion;

import com.google.web.bindery.requestfactory.shared.ProxyFor;

/**
 * @author Daniel Tuerk
 */
@ProxyFor(value = Curve.class, locator = InjectingEntityLocator.class)
public interface CurveProxy extends TrackPartProxy {

    Curve.DIRECTION getDirection();

    void setDirection(Curve.DIRECTION direction);

}
