package net.wbz.moba.controlcenter.web.shared.constrution;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import net.wbz.moba.controlcenter.web.guice.MyGuiceServletConfig;

import java.util.Collection;

/**
 * @author Daniel Tuerk
 */
@RemoteServiceRelativePath(MyGuiceServletConfig.SERVICE_CONSTRUCTION)
public interface ConstructionService extends RemoteService {

    Collection<Construction> loadConstructions();

    Construction getCurrentConstruction();

    void setCurrentConstruction(Construction construction);

    void createConstruction(Construction construction);
}
