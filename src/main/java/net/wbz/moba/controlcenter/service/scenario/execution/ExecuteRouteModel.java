package net.wbz.moba.controlcenter.service.scenario.execution;

import net.wbz.moba.controlcenter.shared.scenario.RouteSequence;
import net.wbz.moba.controlcenter.shared.track.model.Signal;
import net.wbz.moba.controlcenter.shared.train.Train;

import java.util.Optional;

public record ExecuteRouteModel(long scenarioId,
                                RouteSequence routeSequence,
                                Optional<RouteSequence> previousRouteSequence,
                                Optional<RouteSequence> nextRouteSequence,
                                Train train,
                                Train.DRIVING_DIRECTION scenarioDrivingDirection,
                                int trainStartDrivingLevel,
                                Optional<Signal> startSignal,
                                boolean checkTrainInStartBlock) {
}
