package net.wbz.moba.controlcenter.web.shared.track.model;

import net.wbz.moba.controlcenter.web.guice.requestFactory.InjectingEntityLocator;

import com.google.web.bindery.requestfactory.shared.ProxyFor;

/**
 * @author Daniel Tuerk
 */
@ProxyFor(value = Straight.class, locator = InjectingEntityLocator.class)
public interface StraightProxy extends TrackPartProxy {

    Straight.DIRECTION getDirection();

    void setDirection(Straight.DIRECTION direction);

}
