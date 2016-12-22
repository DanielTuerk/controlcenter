package net.wbz.moba.controlcenter.web.shared.constrution;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import java.util.List;

/**
 * @author Daniel Tuerk
 */
@RemoteServiceRelativePath("construction")
public interface ConstructionService extends RemoteService {

    List<Construction> loadConstructions();

    Construction getCurrentConstruction();

    void setCurrentConstruction(Construction construction);

    void createConstruction(Construction construction);
}
