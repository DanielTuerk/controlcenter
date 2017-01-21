package net.wbz.moba.controlcenter.web.shared.scenario;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import net.wbz.moba.controlcenter.web.guice.MyGuiceServletConfig;

/**
 * @author Daniel Tuerk
 */
@RemoteServiceRelativePath(MyGuiceServletConfig.SERVICE_SCENARIO)
public interface ScenarioService extends RemoteService {

    void start(long scenarioId);

    void stop(long scenarioId);

    void pause(long scenarioId);

}
