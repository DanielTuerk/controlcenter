package net.wbz.moba.controlcenter.web.shared.track.model;

import net.wbz.moba.controlcenter.web.guice.requestFactory.InjectingEntityLocator;

import com.google.web.bindery.requestfactory.shared.ProxyFor;
import net.wbz.moba.controlcenter.web.shared.EntityProxyWithIdAndVersion;

/**
 * @author Daniel Tuerk
 */
@ProxyFor(value = GridPosition.class, locator = InjectingEntityLocator.class)
public interface GridPositionProxy extends EntityProxyWithIdAndVersion {

    int getX();

    void setX(int x);

    int getY();

    void setY(int y);


    void setId(long id);

}
