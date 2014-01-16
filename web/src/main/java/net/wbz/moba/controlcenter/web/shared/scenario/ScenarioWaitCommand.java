package net.wbz.moba.controlcenter.web.shared.scenario;

/**
 * @author Daniel Tuerk (daniel.tuerk@jambit.com)
 */
public class ScenarioWaitCommand extends ScenarioCommand {

    private int seconds;

    public ScenarioWaitCommand(int seconds) {
        this.seconds = seconds;
    }

    public ScenarioWaitCommand() {
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
