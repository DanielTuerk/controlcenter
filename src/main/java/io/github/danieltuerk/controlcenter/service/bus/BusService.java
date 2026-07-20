package io.github.danieltuerk.controlcenter.service.bus;

import io.github.danieltuerk.controlcenter.EventBroadcaster;
import io.github.danieltuerk.controlcenter.Workspace;
import io.github.danieltuerk.controlcenter.shared.bus.BusDataEvent;
import io.github.danieltuerk.controlcenter.shared.bus.PlayerEvent;
import io.github.danieltuerk.selectrix4java.bus.consumption.AllBusDataConsumer;
import io.github.danieltuerk.selectrix4java.data.recording.BusDataPlayer;
import io.github.danieltuerk.selectrix4java.data.recording.BusDataPlayerListener;
import io.github.danieltuerk.selectrix4java.device.Device;
import io.github.danieltuerk.selectrix4java.device.Device.SYSTEM_FORMAT;
import io.github.danieltuerk.selectrix4java.device.DeviceAccessException;
import io.github.danieltuerk.selectrix4java.device.test.TestDevice;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Daniel Tuerk
 */
@Slf4j
@ApplicationScoped
public class BusService {

    private final EventBroadcaster eventBroadcaster;
    private final DeviceRecorder deviceRecorder;
    private BusDataPlayer busDataPlayer;
    private final DeviceService deviceService;
    private final Workspace workspace;

    private final Map<String, AllBusDataConsumer> connectionIdsOfBusTracking = new HashMap<>();

    @Inject
    public BusService(EventBroadcaster eventBroadcaster,
                      DeviceRecorder deviceRecorder,
                      DeviceService deviceService,
                      Workspace workspace) {
        this.eventBroadcaster = eventBroadcaster;
        this.deviceRecorder = deviceRecorder;
        this.deviceService = deviceService;
        this.workspace = workspace;
    }

    public boolean getRailVoltage() {
        return deviceService.getConnectedDevice()
            .map(device -> {
            try {
                return device.getRailVoltage();
            } catch (DeviceAccessException e) {
                log.error("can't read rail voltage", e);
                return false;
            }
            })
            .orElse(false);
    }

    public SYSTEM_FORMAT getSystemFormat() {
        return deviceService.getConnectedDevice()
            .map(device -> {
                try {
                    return device.getActualSystemFormat();
                } catch (DeviceAccessException e) {
                    log.error("can't read actual system format", e);
                    return SYSTEM_FORMAT.UNKNOWN;
                }
            })
            .orElse(SYSTEM_FORMAT.UNKNOWN);
    }

    public void toggleRailVoltage() {
        deviceService.getConnectedDevice()
            .ifPresent(device -> {
                try {
                    device.setRailVoltage(!device.getRailVoltage());
                } catch (DeviceAccessException e) {
                    log.error("can't toggle rail voltage", e);
                }
            });
    }

    public void switchSystemFormat() {
        deviceService.getConnectedDevice()
            .ifPresent(Device::switchDeviceSystemFormat);
    }

    public void startTrackingBus(String connectionId) throws DeviceAccessException {
        final var device = fetchDevice();
        if (!connectionIdsOfBusTracking.containsKey(connectionId)) {

            var value = new AllBusDataConsumer() {
                @Override
                public void valueChanged(int bus, int address, int oldValue, int newValue) {
                    eventBroadcaster.fireTrackingEvent(new BusDataEvent(bus, address, newValue),
                        Set.of(connectionId));
                }
            };
            connectionIdsOfBusTracking.put(connectionId, value);
            device.getBusDataDispatcher().registerConsumer(value);

        }
    }

    public void stopTrackingBus(String connectionId) throws DeviceAccessException {
        final var device = fetchDevice();

        if (connectionIdsOfBusTracking.containsKey(connectionId)) {
            // TODO missing connection loss for the connectionIdsOfBusTracking
            device.getBusDataDispatcher().unregisterConsumer(connectionIdsOfBusTracking.get(connectionId));
            connectionIdsOfBusTracking.remove(connectionId);
        }
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
                log.error("can't send data (bus: {}, address: {}, bit: {})", busNr, address, bit, e);
            }
            });
    }

    public void sendBusData(int busNr, int address, int data) {
        deviceService.getConnectedDevice().ifPresent(device -> {
                try {
                    device.getBusAddress(busNr, (byte) address).sendData((byte) data);
                } catch (DeviceAccessException e) {
                    log.error("can't send data (bus: {}, address: {}, data: {})", busNr, address, data, e);
                }
            }
        );
    }

    public int fetchBusData(int busNr, int address) throws DeviceAccessException {
        final var device = fetchDevice();
        return Byte.valueOf(device.getBusAddress(busNr, (byte) address).getData()).intValue();
    }

    public String dumpBusData() throws DeviceAccessException {
        final var device = fetchDevice();
        return workspace.createBusDump(new Workspace.BusDump(
                device.getBusDataDispatcher().getData(0),
                device.getBusDataDispatcher().getData(1)
        ));
    }

    public void importBusDump(Workspace.BusDump busDump) throws DeviceAccessException {
        final var device = fetchDevice();
        if (!(device instanceof TestDevice)) {
            throw new DeviceAccessException("device is not a TestDevice");
        }
        sendDataFromDump(device, 0, busDump.bus0Data());
        sendDataFromDump(device, 1, busDump.bus1Data());
    }

    private Device fetchDevice() throws DeviceAccessException {
        return deviceService.getConnectedDevice()
                .orElseThrow(() -> new DeviceAccessException("not connected"));
    }

    public void startRecording(String fileName) {
        deviceService.getConnectedDevice()
            .ifPresent(device -> deviceRecorder.startRecording(device, null));
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
                    log.info("playback started");
                    eventBroadcaster.fireEvent(new PlayerEvent(PlayerEvent.PLAYER_STATE.START));
                }

                @Override
                public void playbackStopped() {
                    log.info("playback stopped");
                    eventBroadcaster.fireEvent(new PlayerEvent(PlayerEvent.PLAYER_STATE.STOP));
                }
            });
            try {
                busDataPlayer.start(Paths.get(absoluteFilePath));
            } catch (Exception e) {
                log.error("can't start player", e);
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
            log.error("check files of records", e);
        }
        return List.of();
    }

    private static void sendDataFromDump(Device device, int bus, byte[] busDump) throws DeviceAccessException {
        for (int i = 0; i < busDump.length; i++) {
            final byte b = busDump[i];
            device.getBusAddress(bus, i).sendData(b);
        }
    }

}
