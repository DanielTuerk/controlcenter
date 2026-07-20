package io.github.danieltuerk.controlcenter.service.scenario.execution.route;

import io.github.danieltuerk.controlcenter.service.config.ConfigService;
import io.github.danieltuerk.controlcenter.service.track.TrackViewerService;
import io.github.danieltuerk.controlcenter.shared.track.model.BlockStraight;
import io.github.danieltuerk.controlcenter.shared.track.model.Signal;
import io.github.danieltuerk.selectrix4java.device.DeviceAccessException;
import io.github.danieltuerk.selectrix4java.device.DeviceManager;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;

@Slf4j
@ApplicationScoped
class SwitchSignalToDrive {

    private final DeviceManager deviceManager;
    private final TrackViewerService trackViewerService;
    private final ConfigService configService;

    @Inject
    SwitchSignalToDrive(DeviceManager deviceManager, TrackViewerService trackViewerService, ConfigService configService) {
        this.deviceManager = deviceManager;
        this.trackViewerService = trackViewerService;
        this.configService = configService;
    }

    void call(final Signal signal, BlockStraight startOfNextRoute, boolean switchBackToHp0) {
        var signalFunction = nextSignalFunction(startOfNextRoute);
        log.info("switch signal to {} {}", signalFunction, signal);
        trackViewerService.switchSignal(signal, signalFunction);

        if (switchBackToHp0) {
            // switch back to HP0 after a delay, without blocking the caller
            Uni.createFrom().voidItem().onItem()
                    .delayIt().by(Duration.ofSeconds(configService.getHp0AfterTrainPassDelayInSeconds()))
                    .emitOn(Infrastructure.getDefaultWorkerPool())
                    .invoke(() -> trackViewerService.switchSignal(signal, Signal.FUNCTION.HP0))
                    .subscribe().with(
                            unused -> log.debug("signal {} switched back to HP0", signal),
                            failure -> log.error("failed to switch signal {} back to HP0", signal, failure)
                    );
        }
    }

    private Signal.FUNCTION nextSignalFunction(BlockStraight startOfNextRoute) {
        if (startOfNextRoute != null && deviceManager.getConnectedDevice().isPresent()) {
            var device = deviceManager.getConnectedDevice().get();
            return startOfNextRoute.getAllTrackBlocks().stream().anyMatch(trackBlock -> {
                try {
                    return device.getBusAddress(trackBlock.getBlockFunction().getBus(),
                            trackBlock.getBlockFunction().getAddress())
                        .getBitState(trackBlock.getBlockFunction().getBit());
                } catch (DeviceAccessException e) {
                    log.error("cannot get device address for block of start", e);
                    return false;
                }
            }) ? Signal.FUNCTION.HP2 : Signal.FUNCTION.HP1;
        }
        return Signal.FUNCTION.HP1;
    }
}
