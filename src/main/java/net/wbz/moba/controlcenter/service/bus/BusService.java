package net.wbz.moba.controlcenter.service.bus;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.wbz.moba.controlcenter.EventBroadcaster;
import net.wbz.moba.controlcenter.shared.bus.BusDataEvent;
import net.wbz.moba.controlcenter.shared.bus.PlayerEvent;
import net.wbz.selectrix4java.bus.consumption.AllBusDataConsumer;
import net.wbz.selectrix4java.data.recording.BusDataPlayer;
import net.wbz.selectrix4java.data.recording.BusDataPlayerListener;
import net.wbz.selectrix4java.device.DeviceAccessException;
import org.jboss.logging.Logger;

/**
 * @author Daniel Tuerk
 */
@ApplicationScoped
public class BusService {

    private static final Logger LOGGER = Logger.getLogger(BusService.class);
    private final EventBroadcaster eventBroadcaster;
    private final AllBusDataConsumer allBusDataConsumer;
    private final DeviceRecorder deviceRecorder;
    private boolean trackingActive = false;
    private BusDataPlayer busDataPlayer;
    private final DeviceService deviceService;

    @Inject
    public BusService(EventBroadcaster eventBroadcaster,
        DeviceRecorder deviceRecorder,
        DeviceService deviceService) {
        this.eventBroadcaster = eventBroadcaster;
        this.deviceRecorder = deviceRecorder;
        this.deviceService = deviceService;

        this.allBusDataConsumer = new AllBusDataConsumer() {
            @Override
            public void valueChanged(int bus, int address, int oldValue, int newValue) {
                eventBroadcaster.fireEvent(new BusDataEvent(bus, address, newValue));
            }
        };
    }

    public boolean getRailVoltage() {
        return deviceService.getConnectedDevice()
            .map(device -> {
            try {
                return device.getRailVoltage();
            } catch (DeviceAccessException e) {
                LOGGER.error("can't read rail voltage", e);
                return false;
            }
            })
            .orElse(false);
    }

    public void toggleRailVoltage() {
        deviceService.getConnectedDevice()
            .ifPresent(device -> {
                try {
                    device.setRailVoltage(!device.getRailVoltage());
                } catch (DeviceAccessException e) {
                    LOGGER.error("can't toggle rail voltage", e);
                }
            });
    }

    public void startTrackingBus() {
        deviceService.getConnectedDevice().ifPresent(device -> {
            if (!trackingActive) {
                device.getBusDataDispatcher().registerConsumer(allBusDataConsumer);
                trackingActive = true;
            }
        });
    }

    public void stopTrackingBus() {
        deviceService.getConnectedDevice().ifPresent(device -> {
            if (trackingActive) {
                device.getBusDataDispatcher().unregisterConsumer(allBusDataConsumer);
                trackingActive = false;
            }
        });
    }

    public void sendBusData(int busNr, int address, int bit, boolean state) {
        deviceService.getConnectedDevice()
            .ifPresent(device -> {
            try {
                if (state) {
                    device.getBusAddress(busNr, (byte) address).setBit(bit).send();
                } else {
                    device.getBusAddress(busNr, (byte) address).clearBit(bit).send();
                }
            } catch (DeviceAccessException e) {
                LOGGER.error(String.format("can't send data (bus: %d, address: %d, bit: %d)", busNr, address, bit), e);
            }
            });
    }

    public void sendBusData(int busNr, int address, int data) {
        deviceService.getConnectedDevice().ifPresent(device -> {
                try {
                    device.getBusAddress(busNr, (byte) address).sendData((byte) data);
                } catch (DeviceAccessException e) {
                    LOGGER.error(
                        String.format("can't send data (bus: %d, address: %d, data: %d)", busNr, address, data),
                        e);
                }
            }
        );
    }

    public void startRecording(String fileName) {
        deviceService.getConnectedDevice()
            .ifPresent(device -> {
                deviceRecorder.startRecording(device, null);
            });
    }

    public void stopRecording() {
        deviceRecorder.stopRecording();
    }

    public void startPlayer(String absoluteFilePath, int playbackSpeed) {
        deviceService.getConnectedDevice().ifPresent(device -> {

            busDataPlayer = new BusDataPlayer(device.getBusDataDispatcher(), device.getBusDataChannel(),
                playbackSpeed);
            busDataPlayer.addListener(new BusDataPlayerListener() {
                @Override
                public void playbackStarted() {
                    LOGGER.info("playback started");
                    eventBroadcaster.fireEvent(new PlayerEvent(PlayerEvent.STATE.START));
                }

                @Override
                public void playbackStopped() {
                    LOGGER.info("playback stopped");
                    eventBroadcaster.fireEvent(new PlayerEvent(PlayerEvent.STATE.STOP));
                }
            });
            try {
                busDataPlayer.start(Paths.get(absoluteFilePath));
            } catch (Exception e) {
                LOGGER.error("can't start player", e);
            }
        });
    }

    public void stopPlayer() {
        busDataPlayer.stop();
    }

    public List<String> getRecords() {
        try (Stream<Path> stream = Files.list(deviceRecorder.getDestinationFolder())) {
            return stream
                .filter(Files::isRegularFile)
                .map(Path::toString)
                .collect(Collectors.toList());
        } catch (IOException e) {
            LOGGER.error("check files of records", e);
        }
        return List.of();
    }

}
