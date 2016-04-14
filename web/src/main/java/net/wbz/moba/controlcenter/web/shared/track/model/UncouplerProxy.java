package net.wbz.moba.controlcenter.web.shared.track.model;

import java.util.List;
import java.util.Set;

import net.wbz.moba.controlcenter.web.guice.requestFactory.InjectingEntityLocator;
import net.wbz.moba.controlcenter.web.shared.EntityProxyWithIdAndVersion;

import com.google.web.bindery.requestfactory.shared.ProxyFor;

/**
 * @author Daniel Tuerk
 */
@ProxyFor(value = Uncoupler.class, locator = InjectingEntityLocator.class)
public interface UncouplerProxy extends StraightProxy {

}
