package net.wbz.moba.controlcenter.web.shared.scenario;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * @author Daniel Tuerk (daniel.tuerk@jambit.com)
 */
@RemoteServiceRelativePath("scenarioservice")
public interface ScenarioService extends RemoteService {

    public void start(long scenarioId);
    public void stop(long scenarioId);
    public void pause(long scenarioId);

}
