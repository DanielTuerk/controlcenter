package net.wbz.moba.controlcenter.web.shared.scenario;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import net.wbz.moba.controlcenter.web.shared.Identity;

/**
 * @author Daniel Tuerk
 * @deprecated not in use
 */
@Deprecated
public class Scenario implements Identity {

    @Id
    @GeneratedValue
    private long id = -1L;
    private String name;
    /**
     * @gwt.typeArgs <ScenarioCommand>
     */
    private List<ScenarioCommand> commands = new ArrayList<ScenarioCommand>();
    private RUN_STATE runState = RUN_STATE.IDLE;
    private MODE mode = MODE.SINGLE;

    public Scenario() {
    }

    public Scenario(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public enum RUN_STATE {
        RUNNING, IDLE, PAUSED
    }

    public enum MODE {
        SINGLE, REPEAT
    }

}
