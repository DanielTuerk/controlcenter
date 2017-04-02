package net.wbz.moba.controlcenter.web.server.web.scenario;

import net.wbz.moba.controlcenter.web.shared.scenario.Scenario;

/**
 * Listener for state changes of a {@link Scenario}.
 * 
 * @author Daniel Tuerk
 */
public interface ScenarioStateListener {

    /**
     * The given scenario has started.
     *
     * @param scenario {@link Scenario}
     */
    void scenarioStarted(Scenario scenario);

    /**
     * The given scenario has stopped.
     *
     * @param scenario {@link Scenario}
     */
    void scenarioStopped(Scenario scenario);
}