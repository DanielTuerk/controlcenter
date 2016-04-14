package net.wbz.moba.controlcenter.web.shared.track.model;

import java.util.Map;

import net.wbz.moba.controlcenter.web.guice.requestFactory.InjectingEntityLocator;

import com.google.web.bindery.requestfactory.shared.ProxyFor;

/**
 * @author Daniel Tuerk
 */
@ProxyFor(value = Signal.class, locator = InjectingEntityLocator.class)
public interface SignalProxy extends StraightProxy {

    Signal.TYPE getType();

    void setType(Signal.TYPE type);

    /**
     * Wrapper to access the function configuration by the
     * {@link net.wbz.moba.controlcenter.web.shared.track.model.Signal.LIGHT} name.
     *
     * @param light {@link net.wbz.moba.controlcenter.web.shared.track.model.Signal.LIGHT}
     * @return {@link net.wbz.moba.controlcenter.web.shared.track.model.Configuration} or <code>null</code>
     */
//    ConfigurationProxy getLightFunction(Signal.LIGHT light);

    /**
     * Create or update the function configuration by the
     * {@link net.wbz.moba.controlcenter.web.shared.track.model.Signal.LIGHT} name.
     *
     * @param light {@link net.wbz.moba.controlcenter.web.shared.track.model.Signal.LIGHT}
     * @param configuration {@link net.wbz.moba.controlcenter.web.shared.track.model.Configuration} or <code>null</code>
     */
//    void setLightFunctionConfig(Signal.LIGHT light, ConfigurationProxy configuration);

    Map<Signal.LIGHT, ConfigurationProxy> getSignalConfiguration();

}
