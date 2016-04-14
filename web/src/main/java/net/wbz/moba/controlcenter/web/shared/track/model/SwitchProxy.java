package net.wbz.moba.controlcenter.web.shared.track.model;

import net.wbz.moba.controlcenter.web.guice.requestFactory.InjectingEntityLocator;

import com.google.web.bindery.requestfactory.shared.ProxyFor;

/**
 * @author Daniel Tuerk
 */
@ProxyFor(value = Switch.class, locator = InjectingEntityLocator.class)
public interface SwitchProxy extends TrackPartProxy {

    void setCurrentDirection(Switch.DIRECTION currentDirection);

    void setCurrentPresentation(Switch.PRESENTATION currentPresentation);

    Switch.PRESENTATION getCurrentPresentation();

    Switch.DIRECTION getCurrentDirection();

}
