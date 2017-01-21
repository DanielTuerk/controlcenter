package net.wbz.moba.controlcenter.web.shared.scenario;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import net.wbz.moba.controlcenter.web.guice.MyGuiceServletConfig;

/**
 * @author Daniel Tuerk
 */
@RemoteServiceRelativePath(MyGuiceServletConfig.SERVICE_SCENARIO_EDITOR)
public interface ScenarioEditorService extends RemoteService {

    /**
     * @gwt.typeArgs <Scenario>
     */
    // public List<Scenario> getScenarios();
    //
    // public void createScenario(String name);
    //
    // public void deleteScenario(long scenarioId);
    //
    // public void updateScenarioRunMode(long scenarioId, Scenario.MODE mode);
    //
    //
    // public Scenario updateScenarioCommands(long scenarioId, String[] commands);

}
