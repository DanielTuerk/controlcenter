package io.github.danieltuerk.controlcenter.shared.scenario;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Daniel Tuerk
 */
public record ScenarioStatistic(long scenarioId,
                                LocalDateTime lastRun,
                                int total,
                                int failed,
                                int successful,
                                long averageRunTimeInMillis,
                                List<ScenarioStatisticRun> runs) {
}
