package net.wbz.moba.controlcenter.web.shared.scenario;

import net.wbz.moba.controlcenter.web.shared.track.model.Configuration;

/**
 * @author Daniel Tuerk (daniel.tuerk@jambit.com)
 */
public class ScenarioToggleCommand extends ScenarioCommand {

    private Configuration configuration;
    private boolean state;

    public ScenarioToggleCommand(Configuration configuration, boolean state) {
        this.configuration = configuration;
        this.state = state;
    }

    public ScenarioToggleCommand() {
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    @Override
    public String getCommand() {
        return "toggle";
    }

    @Override
    public void parseParameters(String... text) {
        configuration = new Configuration();
        configuration.setAddress(Integer.parseInt(text[0]));
        configuration.setOutput(Integer.parseInt(text[1]));
        state = Boolean.parseBoolean(text[2]);
    }

    @Override
    public String[] convertParameters() {
        return new String[]{String.valueOf(configuration.getAddress()),
                String.valueOf(configuration.getOutput()), String.valueOf(state)};
    }
}
