package io.github.danieltuerk.controlcenter.shared.scenario;

import java.time.LocalDateTime;

public record ScenarioStatisticRun(LocalDateTime start,
                                   LocalDateTime end,
                                   double averageRunTimeInMillis,
                                   STATE state) {
    public enum STATE {SUCCESS, FAILED}
}
