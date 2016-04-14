package net.wbz.moba.controlcenter.web.shared.track.model;

import java.util.Map;

import net.wbz.moba.controlcenter.web.guice.requestFactory.InjectingEntityLocator;
import net.wbz.moba.controlcenter.web.shared.EntityProxyWithIdAndVersion;

import com.google.web.bindery.requestfactory.shared.ProxyFor;

/**
 * @author Daniel Tuerk
 */
@ProxyFor(value = SignalConfiguration.class, locator = InjectingEntityLocator.class)
public interface SignalConfigurationProxy extends EntityProxyWithIdAndVersion {

    Map<Signal.LIGHT, ConfigurationProxy> getLightsConfiguration();

    void setLightsConfiguration(Map<Signal.LIGHT, ConfigurationProxy> lightsConfiguration);

//    ConfigurationProxy get(Signal.LIGHT light);

}
