package net.wbz.moba.controlcenter.web.shared.scenario;

import net.wbz.moba.controlcenter.web.server.persist.construction.track.TrackPartConfigurationEntity;

/**
 * @author Daniel Tuerk
 */
public class ToggleScenarioCommand extends ScenarioCommand {

    private TrackPartConfigurationEntity configuration;
    private boolean state;

    public ToggleScenarioCommand(TrackPartConfigurationEntity configuration, boolean state) {
        this.configuration = configuration;
        this.state = state;
    }

    public ToggleScenarioCommand() {
    }

    public TrackPartConfigurationEntity getConfiguration() {
        return configuration;
    }

    public void setConfiguration(TrackPartConfigurationEntity configuration) {
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
        configuration = new TrackPartConfigurationEntity();
        configuration.setAddress(Integer.parseInt(text[0]));
        configuration.setBit(Integer.parseInt(text[1]));
        state = Boolean.parseBoolean(text[2]);
    }

    @Override
    public String[] convertParameters() {
        return new String[]{String.valueOf(configuration.getAddress()),
                String.valueOf(configuration.getBit()), String.valueOf(state)};
    }
}
