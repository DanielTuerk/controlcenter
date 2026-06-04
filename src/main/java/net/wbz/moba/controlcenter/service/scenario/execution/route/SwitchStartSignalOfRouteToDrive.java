package net.wbz.moba.controlcenter.service.scenario.execution.route;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import net.wbz.moba.controlcenter.service.track.TrackProvider;
import net.wbz.moba.controlcenter.shared.scenario.Route;

@ApplicationScoped
public class SwitchStartSignalOfRouteToDrive {

    private final TrackProvider trackProvider;
    private final SwitchSignalToDrive switchSignalToDrive;

    @Inject
    SwitchStartSignalOfRouteToDrive(TrackProvider trackProvider, SwitchSignalToDrive switchSignalToDrive) {
        this.trackProvider = trackProvider;
        this.switchSignalToDrive = switchSignalToDrive;
    }

    Uni<Void> call(Route route) {
        return trackProvider.findStartSignal(route)
            .map(signal -> switchSignalToDrive.call(signal, route.getStart()))
            .orElse(Uni.createFrom().voidItem());
    }
}
