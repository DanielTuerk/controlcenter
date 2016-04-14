package net.wbz.moba.controlcenter.web.shared.track.model;

import net.wbz.moba.controlcenter.web.guice.requestFactory.InjectingEntityLocator;
import net.wbz.moba.controlcenter.web.shared.EntityProxyWithIdAndVersion;

import com.google.web.bindery.requestfactory.shared.ProxyFor;

/**
 * @author Daniel Tuerk
 */
@ProxyFor(value = TrackPartFunction.class, locator = InjectingEntityLocator.class)
public interface TrackPartFunctionProxy extends EntityProxyWithIdAndVersion {

    void setId(long id);

    String getFunctionKey();

    void setFunctionKey(String functionKey);

    ConfigurationProxy getConfiguration();

    void setConfiguration(ConfigurationProxy configuration);

}
