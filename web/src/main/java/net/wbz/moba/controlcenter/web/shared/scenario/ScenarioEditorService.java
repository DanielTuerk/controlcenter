package net.wbz.moba.controlcenter.web.shared.scenario;

import java.util.List;

/**
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */

public interface ScenarioEditorService {

    /**
     * @gwt.typeArgs <Scenario>
     */
    public List<Scenario> getScenarios();

    public void createScenario(String name);

    public void deleteScenario(long scenarioId);

    public void updateScenarioRunMode(long scenarioId, Scenario.MODE mode);


    public Scenario updateScenarioCommands(long scenarioId, String[] commands);

}
