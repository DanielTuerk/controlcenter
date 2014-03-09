package net.wbz.moba.controlcenter.web.shared.scenario;

import com.google.gwt.user.client.rpc.IsSerializable;
import net.wbz.moba.controlcenter.web.shared.AbstractIdModel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Daniel Tuerk (daniel.tuerk@jambit.com)
 */
public class Scenario extends AbstractIdModel {

    private String name;

    /**
     * @gwt.typeArgs <ScenarioCommand>
     */
    private List<ScenarioCommand> commands = new ArrayList<ScenarioCommand>();

    public enum RUN_STATE {RUNNING, IDLE, PAUSED}

    private RUN_STATE runState = RUN_STATE.IDLE;

    public enum MODE {SINGLE, REPEAT}

    private MODE mode = MODE.SINGLE;

    public Scenario() {
    }

    public Scenario(String name) {
        this.name = name;
    }


    public MODE getMode() {
        return mode;
    }

    public void setMode(MODE mode) {
        this.mode = mode;
    }

    public RUN_STATE getRunState() {
        return runState;
    }

    public void setRunState(RUN_STATE runState) {
        this.runState = runState;
    }

    public String getName() {
        return name;
    }

    /**
     * @gwt.typeArgs <ScenarioCommand>
     */
    public List<ScenarioCommand> getCommands() {
        return commands;
    }

    public void addCommand(ScenarioCommand command) {
        if (runState == RUN_STATE.IDLE) {
            commands.add(command);
        }
    }

}
