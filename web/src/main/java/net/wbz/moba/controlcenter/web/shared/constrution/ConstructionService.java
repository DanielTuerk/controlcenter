package net.wbz.moba.controlcenter.web.shared.constrution;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.web.bindery.requestfactory.shared.RequestContext;
import com.google.web.bindery.requestfactory.shared.Service;
import net.wbz.moba.controlcenter.web.guice.requestFactory.InjectingServiceLocator;
import net.wbz.moba.controlcenter.web.server.web.constrution.ConstructionServiceImpl;

import java.util.List;

/**
 * @author Daniel Tuerk
 */
//@Service(value = ConstructionServiceImpl.class, locator = InjectingServiceLocator.class)
@RemoteServiceRelativePath("construction")
public interface ConstructionService extends RemoteService {

    List<Construction> loadConstructions();

    Construction getCurrentConstruction();

    void setCurrentConstruction(Construction construction);

    void createConstruction(Construction construction);
}
