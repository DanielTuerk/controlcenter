package net.wbz.moba.controlcenter.web.shared.constrution;

import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.RequestContext;
import com.google.web.bindery.requestfactory.shared.Service;
import net.wbz.moba.controlcenter.web.guice.requestFactory.InjectingServiceLocator;
import net.wbz.moba.controlcenter.web.server.constrution.ConstructionService;

import java.util.List;

/**
 * @author Daniel Tuerk
 */
@Service(value = ConstructionService.class, locator = InjectingServiceLocator.class)
public interface ConstructionRequest extends RequestContext {

    Request<List<ConstructionProxy>> loadConstructions();

    Request<ConstructionProxy> getCurrentConstruction();

    Request<Void> setCurrentConstruction(ConstructionProxy construction);

    Request<Void> createConstruction(ConstructionProxy construction);
}
