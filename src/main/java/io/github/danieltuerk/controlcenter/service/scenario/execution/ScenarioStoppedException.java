package io.github.danieltuerk.controlcenter.service.scenario.execution;

public final class ScenarioStoppedException extends RuntimeException {
    ScenarioStoppedException(String msg) {
        super(msg);
    }
}
