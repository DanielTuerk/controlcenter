package net.wbz.moba.controlcenter.web.shared.constrution;

import net.wbz.moba.controlcenter.web.guice.requestFactory.InjectingEntityLocator;
import net.wbz.moba.controlcenter.web.shared.EntityProxyWithIdAndVersion;

import com.google.web.bindery.requestfactory.shared.ProxyFor;

/**
 * @author Daniel Tuerk
 */
@ProxyFor(value = Construction.class, locator = InjectingEntityLocator.class)
public interface ConstructionProxy extends EntityProxyWithIdAndVersion {

    void setId(long id);

    String getName();

    void setName(String name);
}
