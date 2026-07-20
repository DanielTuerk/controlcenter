package io.github.danieltuerk.controlcenter.service.scenario.execution;

import io.github.danieltuerk.controlcenter.shared.scenario.RouteSequence;
import io.github.danieltuerk.controlcenter.shared.train.Train;

import java.util.Optional;

public record ExecuteRouteModel(long scenarioId,
                                RouteSequence routeSequence,
                                Optional<RouteSequence> previousRouteSequence,
                                Optional<RouteSequence> nextRouteSequence,
                                Train train,
                                Train.DRIVING_DIRECTION scenarioDrivingDirection,
                                int trainStartDrivingLevel,
                                boolean wasSkipped) {
}
