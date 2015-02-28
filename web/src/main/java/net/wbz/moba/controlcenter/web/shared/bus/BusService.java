package net.wbz.moba.controlcenter.web.shared.bus;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import net.wbz.selectrix4java.data.recording.RecordingException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
@RemoteServiceRelativePath("bus")
public interface BusService extends RemoteService {

    public void connectBus();
    public void disconnectBus();
    public boolean isBusConnected();

    public void changeDevice(DeviceInfo deviceInfo);

    public void createDevice(DeviceInfo deviceInfo);

    public void deleteDevice(DeviceInfo deviceInfo);

    public ArrayList<DeviceInfo> getDevices();

    public boolean getRailVoltage();

    public void toggleRailVoltage();

    public BusData[] readBusData(int busNr);

    /**
     * Start the listener for all buses.
     * Consumer will receive all changes from device and delegate to throw the {@link net.wbz.moba.controlcenter.web.shared.bus.BusDataEvent}s.
     */
    public void startTrackingBus();

    /**
     * Stop the listener for all buses.
     * Unregister the Consumer will stop throwing the {@link net.wbz.moba.controlcenter.web.shared.bus.BusDataEvent}s.
     */
    public void stopTrackingBus();
    /**
     * Simple access to the selectrix bus. Set value for the bit of the current connected device.
     *
     * @param busNr number of bus
     * @param address address of bit
     * @param bit bit number (1-8)
     * @param state {@link java.lang.Boolean} new state for the bit
     */
    public void sendBusData(int busNr, int address, int bit, boolean state);

    /**
     * Send value for the given address of the bus number to the current connected device.
     *
     * @param busNr number of bus
     * @param address address
     * @param data data of address
     */
    public void sendBusData(int busNr, int address, int data);

    public void startRecording(String fileName);
    public void stopRecording();

    public void startPlayer(String absoluteFilePath);
    public void stopPlayer();

    public List<String> getRecords();
}
