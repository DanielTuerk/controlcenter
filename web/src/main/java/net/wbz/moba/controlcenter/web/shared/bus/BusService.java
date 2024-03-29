package net.wbz.moba.controlcenter.web.shared.bus;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import java.util.Collection;
import net.wbz.moba.controlcenter.web.client.event.StateEvent;
import net.wbz.moba.controlcenter.web.guice.MyGuiceServletConfig;

/**
 * @author Daniel Tuerk
 */
@RemoteServiceRelativePath(MyGuiceServletConfig.SERVICE_BUS)
public interface BusService extends RemoteService {

    void connectBus();

    void disconnectBus();

    boolean isBusConnected();

    void changeDevice(DeviceInfo deviceInfo);

    void createDevice(DeviceInfo deviceInfo);

    void deleteDevice(DeviceInfo deviceInfo);

    Collection<DeviceInfo> getDevices();

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
     * @param busNr number of bus
     * @param address address of bit
     * @param bit bit number (1-8)
     * @param state {@link java.lang.Boolean} new state for the bit
     */
    void sendBusData(int busNr, int address, int bit, boolean state);

    /**
     * Send value for the given address of the bus number to the current connected device.
     *
     * @param busNr number of bus
     * @param address address
     * @param data data of address
     */
    void sendBusData(int busNr, int address, int data);

    /**
     * Start recording to the give file name.
     *
     * @param fileName name of file to store the record on local system
     */
    void startRecording(String fileName);

    /**
     * Stop the actual running recording.
     */
    void stopRecording();

    /**
     * Start playback from the give file path.
     *
     * @param absoluteFilePath path of file on local system
     * @param playbackSpeed playback speed (1 is normal speed; 2 double speed)
     */
    void startPlayer(String absoluteFilePath, int playbackSpeed);

    /**
     * Stop the actual running playback.
     */
    void stopPlayer();

    /**
     * List all available records from local file system.
     *
     * @return file names
     */
    Collection<String> getRecords();

    /**
     * List off all {@link StateEvent}s from cache which was send last time.
     *
     * @param eventClazzName class name of the {@link StateEvent}
     * @return {@link StateEvent}s
     */
    Collection<StateEvent> getLastSendEvent(String eventClazzName);
}
