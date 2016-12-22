package net.wbz.moba.controlcenter.web.shared.scenario;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * @author Daniel Tuerk
 */
@RemoteServiceRelativePath("scenarioservice")
public interface ScenarioService extends RemoteService {

    void start(long scenarioId);
    void stop(long scenarioId);
    void pause(long scenarioId);

}
