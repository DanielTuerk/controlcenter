package net.wbz.moba.controlcenter.web.shared.constrution;

import java.util.List;

import net.wbz.moba.controlcenter.web.guice.requestFactory.InjectingServiceLocator;
import net.wbz.moba.controlcenter.web.server.constrution.ConstructionService;

import com.google.web.bindery.requestfactory.shared.InstanceRequest;
import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.RequestContext;
import com.google.web.bindery.requestfactory.shared.Service;

/**
 * @author Daniel Tuerk
 */
@Service(value = ConstructionService.class, locator = InjectingServiceLocator.class)
public interface ConstructionRequest extends RequestContext {

    // Request<ConstructionProxy> createConstruction(Construction construction);
    Request<List<ConstructionProxy>> loadConstructions();

    Request<ConstructionProxy> getCurrentConstruction();

    Request<Void> setCurrentConstruction(ConstructionProxy construction);

    InstanceRequest<ConstructionProxy, Void> persist();

    InstanceRequest<ConstructionProxy, Void> remove();

}
