package net.wbz.moba.controlcenter.web.shared.config;

import net.wbz.moba.controlcenter.web.guice.requestFactory.InjectingServiceLocator;
import net.wbz.moba.controlcenter.web.server.config.ConfigService;

import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.RequestContext;
import com.google.web.bindery.requestfactory.shared.Service;

/**
 * @author Daniel Tuerk
 */
@Service(value = ConfigService.class, locator = InjectingServiceLocator.class)
public interface ConfigRequest extends RequestContext {

    Request<String> loadValue(String configKey) throws ConfigNotAvailableException;

    Request<Void> saveValue(String configKey, String value);
}
