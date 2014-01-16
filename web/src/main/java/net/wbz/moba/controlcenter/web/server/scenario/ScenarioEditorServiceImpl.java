package net.wbz.moba.controlcenter.web.server.scenario;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.wbz.moba.controlcenter.web.shared.scenario.Scenario;
import net.wbz.moba.controlcenter.web.shared.scenario.ScenarioCommand;
import net.wbz.moba.controlcenter.web.shared.scenario.ScenarioEditorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author Daniel Tuerk (daniel.tuerk@jambit.com)
 */
@Singleton
public class ScenarioEditorServiceImpl extends RemoteServiceServlet implements ScenarioEditorService {
    private static final Logger LOG = LoggerFactory.getLogger(ScenarioEditorServiceImpl.class);

    private final ScenarioManager scenarioManager;

    @Inject
    public ScenarioEditorServiceImpl(ScenarioManager scenarioManager) {
        this.scenarioManager = scenarioManager;
    }

    @Override
    public List<Scenario> getScenarios() {
        try {
            return scenarioManager.getScenarios();
        } catch (Exception e) {
            throw new RuntimeException(String.format("can't load scenarios"), e);
        }
    }

    @Override
    public void createScenario(String name) {
        try {
            scenarioManager.createScenario(new Scenario(name));
        } catch (Exception e) {
            LOG.error(String.format("can't create scenario '%s'", name), e);
            throw new RuntimeException(String.format("can't create scenario '%s'", name), e);
        }
    }

    @Override
    public void deleteScenario(long scenarioId) {
        scenarioManager.deleteDatabase(scenarioId);
    }

    @Override
    public void updateScenarioRunMode(long scenarioId, Scenario.MODE mode) {
        scenarioManager.getScenarioById(scenarioId).setMode(mode);
    }

    @Override
    public Scenario updateScenarioCommands(long scenarioId, String[] commands) {
        List<ScenarioCommand> commandList = scenarioManager.loadCommandsFromText(commands);
        Scenario scenario = scenarioManager.getScenarioById(scenarioId);
        scenario.getCommands().clear();
        scenario.getCommands().addAll(commandList);
        try {
            scenarioManager.updateScenario(scenario);
            return scenario;
        } catch (Exception e) {
            throw new RuntimeException(String.format("can't update scenario: %s", scenario), e);
        }
    }

}
