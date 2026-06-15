package net.wbz.moba.controlcenter.service.scenario.execution.route;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.wbz.moba.controlcenter.service.config.ConfigService;
import net.wbz.moba.controlcenter.service.track.TrackViewerService;
import net.wbz.moba.controlcenter.shared.track.model.BlockStraight;
import net.wbz.moba.controlcenter.shared.track.model.Signal;
import net.wbz.selectrix4java.device.DeviceAccessException;
import net.wbz.selectrix4java.device.DeviceManager;

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

    Uni<Void> call(final Signal signal, BlockStraight startOfNextRoute) {
        var signalFunction = nextSignalFunction(startOfNextRoute);
        log.info("switch signal to {} {}", signalFunction, signal);
        trackViewerService.switchSignal(signal, signalFunction);

        // switch back to HP0 after delay
        return Uni.createFrom().voidItem().onItem()
            .delayIt().by(Duration.ofSeconds(configService.getHp0AfterTrainPassDelayInSeconds()))
            .invoke(() -> trackViewerService.switchSignal(signal, Signal.FUNCTION.HP0));
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
