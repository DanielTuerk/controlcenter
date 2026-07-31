package io.github.danieltuerk.controlcenter.shared.scenario;

import java.time.LocalDateTime;

public record ScenarioStatisticRun(LocalDateTime start,
                                   LocalDateTime end,
                                   long averageRunTimeInMillis,
                                   STATE state) {
    public enum STATE {SUCCESS, FAILED}
}
