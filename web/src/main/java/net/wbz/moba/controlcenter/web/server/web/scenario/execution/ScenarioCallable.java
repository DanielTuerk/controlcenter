package net.wbz.moba.controlcenter.web.server.web.scenario.execution;

import java.util.concurrent.Callable;

/**
 * Callable to start the {@link ScenarioExecution}.
 * No return value because it won't be joined.
 * The {@link ScenarioExecution} itself will fire events and release dependencies afterwards.
 * 
 * @author Daniel Tuerk
 */
class ScenarioCallable implements Callable<Void> {
    private final ScenarioExecution scenarioExecution;

    ScenarioCallable(ScenarioExecution scenarioExecution) {
        this.scenarioExecution = scenarioExecution;
    }

    @Override
    public Void call() throws Exception {
        scenarioExecution.start();
        return null;
    }
}
