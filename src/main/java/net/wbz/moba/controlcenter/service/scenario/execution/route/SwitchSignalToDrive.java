package net.wbz.moba.controlcenter.service.scenario.execution.route;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.wbz.moba.controlcenter.service.track.TrackViewerService;
import net.wbz.moba.controlcenter.shared.track.model.BlockStraight;
import net.wbz.moba.controlcenter.shared.track.model.Signal;
import net.wbz.selectrix4java.device.DeviceAccessException;
import net.wbz.selectrix4java.device.DeviceManager;

import java.time.Duration;

@Slf4j
@ApplicationScoped
class SwitchSignalToDrive {

    private static final long HP0_AFTER_TRAIN_PASS_DELAY_IN_MILLIS = 15000L;

    private final DeviceManager deviceManager;
    private final TrackViewerService trackViewerService;

    @Inject
    SwitchSignalToDrive(DeviceManager deviceManager, TrackViewerService trackViewerService) {
        this.deviceManager = deviceManager;
        this.trackViewerService = trackViewerService;
    }

    Uni<Void> call(final Signal signal, BlockStraight startOfNextRoute) {
        var signalFunction = nextSignalFunction(startOfNextRoute);
        log.info("switch signal to {} {}", signalFunction, signal);
        trackViewerService.switchSignal(signal, signalFunction);

        return Uni.createFrom().voidItem().onItem()
            // the signal have no monitoring block and will never get the HP0 after drive
            .delayIt().by(Duration.ofMillis(HP0_AFTER_TRAIN_PASS_DELAY_IN_MILLIS))
            .invoke(() -> {
                /*
                 * Set to HP0 after delay to simulate a occupied monitoring block to switch the signal after the
                 * train passed.
                 */
                trackViewerService.switchSignal(signal, Signal.FUNCTION.HP0);
            });
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
