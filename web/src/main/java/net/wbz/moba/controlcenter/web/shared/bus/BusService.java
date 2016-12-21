package net.wbz.moba.controlcenter.web.shared.bus;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import java.util.List;

/**
 * @author Daniel Tuerk
 */
@RemoteServiceRelativePath("bus")
public interface BusService extends RemoteService {

    void connectBus();

    void disconnectBus();

    boolean isBusConnected();

    void changeDevice(DeviceInfo deviceInfo);

    void createDevice(DeviceInfo deviceInfo);

    void deleteDevice(DeviceInfo deviceInfo);

    List<DeviceInfo> getDevices();

    boolean getRailVoltage();

    void toggleRailVoltage();

    /**
     * Start the listener for all buses.
     * Consumer will receive all changes from device and delegate to throw the
     * {@link net.wbz.moba.controlcenter.web.shared.bus.BusDataEvent}s.
     */
    void startTrackingBus();

    /**
     * Stop the listener for all buses.
     * Unregister the Consumer will stop throwing the {@link net.wbz.moba.controlcenter.web.shared.bus.BusDataEvent}s.
     */
    void stopTrackingBus();

    /**
     * Simple access to the selectrix bus. Set value for the bit of the current connected device.
     *
     * @param busNr   number of bus
     * @param address address of bit
     * @param bit     bit number (1-8)
     * @param state   {@link java.lang.Boolean} new state for the bit
     */
    void sendBusData(int busNr, int address, int bit, boolean state);

    /**
     * Send value for the given address of the bus number to the current connected device.
     *
     * @param busNr   number of bus
     * @param address address
     * @param data    data of address
     */
    void sendBusData(int busNr, int address, int data);

    void startRecording(String fileName);

    void stopRecording();

    void startPlayer(String absoluteFilePath);

    void stopPlayer();

    List<String> getRecords();
}
