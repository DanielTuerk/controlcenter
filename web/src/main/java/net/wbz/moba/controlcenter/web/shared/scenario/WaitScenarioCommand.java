package net.wbz.moba.controlcenter.web.shared.scenario;

/**
 * @author Daniel Tuerk
 */
public class WaitScenarioCommand extends ScenarioCommand {

    private int seconds;

    public WaitScenarioCommand(int seconds) {
        this.seconds = seconds;
    }

    public WaitScenarioCommand() {
        seconds=0;
    }

    public int getSeconds() {
        return seconds;
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }

    @Override
    public String getCommand() {
        return "wait";
    }

    @Override
    public void parseParameters(String... text) {
        seconds = Integer.parseInt(text[0]);
    }

    @Override
    public String[] convertParameters() {
        return new String[]{String.valueOf(seconds)};
    }
}
