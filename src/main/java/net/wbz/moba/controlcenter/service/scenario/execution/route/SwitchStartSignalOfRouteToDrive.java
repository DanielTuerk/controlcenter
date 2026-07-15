package net.wbz.moba.controlcenter.service.scenario.execution.route;

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

    void call(Route route) {
        trackProvider.findStartSignal(route)
                .ifPresent(signal -> switchSignalToDrive.call(signal, route.getStart()));
    }
}
