package net.wbz.moba.controlcenter.web.server.web.scenario;

import net.wbz.moba.controlcenter.web.shared.scenario.Scenario;

/**
 * Default implementation of {@link ScenarioStateListener}.
 *
 * @author Daniel Tuerk
 */
public interface DefaultScenarioStateListener extends ScenarioStateListener {

    default void scenarioStarted(Scenario scenario) {
    }

    default void scenarioStopped(Scenario scenario) {
    }

    default void scenarioExecuteWithError(Scenario scenario) {
    }

    default void scenarioSuccessfullyExecuted(Scenario scenario) {
    }

    default void scenarioFinished(Scenario scenario) {
    }

    default void scenarioQueued(Scenario scenario) {
    }

    default void scenarioPaused(Scenario scenario) {
    }

}
