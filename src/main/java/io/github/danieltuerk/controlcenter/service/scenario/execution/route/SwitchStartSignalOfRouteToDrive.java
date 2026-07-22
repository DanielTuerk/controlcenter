package io.github.danieltuerk.controlcenter.service.scenario.execution.route;

import io.github.danieltuerk.controlcenter.service.track.TrackProvider;
import io.github.danieltuerk.controlcenter.shared.scenario.Route;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class SwitchStartSignalOfRouteToDrive {

    private final TrackProvider trackProvider;
    private final SwitchSignalToDrive switchSignalToDrive;

    @Inject
    SwitchStartSignalOfRouteToDrive(TrackProvider trackProvider, SwitchSignalToDrive switchSignalToDrive) {
        this.trackProvider = trackProvider;
        this.switchSignalToDrive = switchSignalToDrive;
    }

    void call(Route route, boolean switchBackToHp0) {
        trackProvider.findStartSignal(route)
                .ifPresent(signal ->
                        switchSignalToDrive.call(signal, route.getStart(), switchBackToHp0));
    }
}
