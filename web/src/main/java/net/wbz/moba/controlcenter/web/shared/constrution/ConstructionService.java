package net.wbz.moba.controlcenter.web.shared.constrution;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
@RemoteServiceRelativePath("construction")
public interface ConstructionService extends RemoteService {

    public void createConstruction(Construction construction);
    public Construction[] loadConstructions();
    public void setCurrentConstruction(Construction construction);
    public Construction getCurrentConstruction();

}
