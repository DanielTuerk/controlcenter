package net.wbz.moba.controlcenter.web.shared.scenario;

/**
 * Command to start another {@link net.wbz.moba.controlcenter.web.shared.scenario.Scenario}.
 *
 * @author Daniel Tuerk (daniel.tuerk@jambit.com)
 */
public class StartScenarioScenarioCommand extends ScenarioCommand {

    private long scenarioId;

    public StartScenarioScenarioCommand(long scenarioId) {
        this.scenarioId = scenarioId;
    }

    public StartScenarioScenarioCommand() {
        scenarioId=-1L;
    }

    public long getScenarioId() {
        return scenarioId;
    }

    @Override
    public String getCommand() {
        return "startScenario";
    }

    @Override
    public void parseParameters(String... text) {
        scenarioId = Long.parseLong(text[0]);
    }

    @Override
    public String[] convertParameters() {
        return new String[]{String.valueOf(scenarioId)};
    }
}
