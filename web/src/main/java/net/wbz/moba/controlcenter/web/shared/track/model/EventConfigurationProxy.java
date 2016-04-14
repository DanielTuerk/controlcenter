package net.wbz.moba.controlcenter.web.shared.track.model;

import net.wbz.moba.controlcenter.web.guice.requestFactory.InjectingEntityLocator;

import com.google.web.bindery.requestfactory.shared.ProxyFor;
import net.wbz.moba.controlcenter.web.shared.EntityProxyWithIdAndVersion;

/**
 * @author Daniel Tuerk
 */
@ProxyFor(value = EventConfiguration.class, locator = InjectingEntityLocator.class)
public interface EventConfigurationProxy extends EntityProxyWithIdAndVersion {

    void setStateOnConfig(ConfigurationProxy config);

    void setStateOffConfig(ConfigurationProxy config);

    ConfigurationProxy getStateOnConfig();

    ConfigurationProxy getStateOffConfig();

    boolean isActive();

    void setId(long id);
}
